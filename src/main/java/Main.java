
import java.awt.Point;
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionRunner;

import frc.robot.CameraConstants;
import frc.robot.ConvertRectToPoint;
import frc.robot.DoNothingPipeline;
import frc.robot.GripPipeline;
import frc.robot.ResizeCamera;
import frc.robot.Timer;
import frc.robot.DCGripPipeline;
import frc.robot.VisionProcessor;
import frc.robot.VisionReporter;
/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ]
           }
       ]
   }
 */

public final class Main {
  private static String configFile = "/boot/frc.json";
  private static long offsetTime = 0;
  private static int counter = 0; 
  @SuppressWarnings("MemberName")
  public static class CameraConfig {
    public String name;
    public String path;
    public JsonObject config;
  }

  public static int team;
  public static boolean server;
  public static List<CameraConfig> cameraConfigs = new ArrayList<>();

  static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  private Main() {
  }

  /**
   * Report parse error.
   */
  public static void parseError(String str) {
    System.err.println("config error in '" + configFile + "': " + str);
  }

  /**
   * Read single camera configuration.
   */
  public static boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      parseError("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    cam.config = config;

    cameraConfigs.add(cam);
    return true;
  }

  /**
   * Read configuration file.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  public static boolean readConfig() {
    // parse file
    JsonElement top;
    try {
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
    } catch (IOException ex) {
      System.err.println("could not open '" + configFile + "': " + ex);
      return false;
    }

    // top level must be an object
    if (!top.isJsonObject()) {
      parseError("must be JSON object");
      return false;
    }
    JsonObject obj = top.getAsJsonObject();

    // team number
    JsonElement teamElement = obj.get("team");
    if (teamElement == null) {
      parseError("could not read team number");
      return false;
    }
    team = teamElement.getAsInt();

    // ntmode (optional)
    if (obj.has("ntmode")) {
      String str = obj.get("ntmode").getAsString();
      if ("client".equalsIgnoreCase(str)) {
        server = false;
      } else if ("server".equalsIgnoreCase(str)) {
        server = true;
      } else {
        parseError("could not understand ntmode value '" + str + "'");
      }
    }

    // cameras
    JsonElement camerasElement = obj.get("cameras");
    if (camerasElement == null) {
      parseError("could not read cameras");
      return false;
    }
    JsonArray cameras = camerasElement.getAsJsonArray();
    for (JsonElement camera : cameras) {
      if (!readCameraConfig(camera.getAsJsonObject())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    //System.out.println("Starting camera '" + config.name + "' on " + config.path);
    VideoSource camera = CameraServer.getInstance().startAutomaticCapture(
        config.name, config.path);

    Gson gson = new GsonBuilder().create();

    camera.setConfigJson(gson.toJson(config.config));

    return camera;
  }

  public static void main(String... args) throws IOException {
    System.err.println("TEAM 281 CODE UPDATED JUST ENTECH");
    if (args.length > 0) {
      configFile = args[0];
    }


    String configFileText = readFile(configFile, StandardCharsets.UTF_8);

    // read configuration
    /*if (!readConfig()) {
      return;
    }*/

    // start NetworkTables
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    if (server) {
      System.out.println("EDITED:Setting up NetworkTables server");
      } else {
      System.out.println("EDITED:Setting up NetworkTables client for team " + team);
      ntinst.startClientTeam(team);
    }
    

    // start cameras
    /*List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : cameraConfigs) {
      cameras.add(startCamera(cameraConfig));
    }*/

    // start image processing on camera 0 if present
    //VideoSource source = cameras.get(0);

    
    MjpegServer rawVideoServer = new MjpegServer("raw_video_server", 8081);

    CvSource cvsource = new CvSource("processed",
        VideoMode.PixelFormat.kMJPEG, CameraConstants.PROCESS_WIDTH,CameraConstants.PROCESS_HEIGHT, 30);

    //rawVideoServer.setSource(cvsource);
    
    UsbCamera source = new UsbCamera("the camera", "/dev/video0");
    boolean success = source.setConfigJson(configFileText);
    System.out.println("IS CONFIG JSON WORKING?" + success);
    System.out.println("EXPOSURE: " + source.getProperty("exposure").getKind());
    

    VideoMode videoMode = new VideoMode(VideoMode.PixelFormat.kBGR, CameraConstants.PROCESS_WIDTH, CameraConstants.PROCESS_HEIGHT, 90);
    source.setVideoMode(videoMode);
    rawVideoServer.setSource(cvsource);

    VisionReporter reporter = new VisionReporter();
    DCGripPipeline grip = new DCGripPipeline();
    Timer frameTimer = new Timer(2);
    offsetTime = System.currentTimeMillis();
    VisionProcessor processor = new VisionProcessor(grip);
    CvSink sink = new CvSink("From Camera");
    sink.setSource(source);
    sink.setEnabled(true);
    Mat m = new Mat();
    int i =0;
  

    while(true){
      sink.grabFrame(m);
      i +=1;
      if(frameTimer.shouldWrite()){
        System.out.println("~~~~~~~~~~~~~~~~~THIS IS THE FRAME RATE:" + frameTimer.getFrameRate());
        System.out.println("INITIAL_DIMENSIONS:"+m.size());
      }
      if(i>1){
        ResizeCamera rc = new ResizeCamera(m,
          new Size(CameraConstants.PROCESS_WIDTH, CameraConstants.PROCESS_HEIGHT));
        Mat resized = rc.getResizedImage();
        processor.process(resized);
        reporter.reportDistance(processor.getDistanceFromTarget(), processor.getLateralDistance(), frameTimer.getFrameCount());

        if(frameTimer.getFrameCount()%5 == 0){
          cvsource.putFrame(processor.getLastFrame());
        }
      }
    }


    /*VisionRunner runner = new VisionRunner(source, new VisionProcessor( grip ), processed -> {
        
        try{

            if(frameTimer.shouldWrite()){
              System.out.println("~~~~~~~~~~~~~~~~~THIS IS THE FRAME RATE:" + frameTimer.getFrameRate());
              //System.out.println("~~~~~~~~~~~~~~~~~THIS IS FPS:" + source.getActualFPS());
              //System.out.println("~~~~~~~~~~~~~~~~~THIS IS THE SOURCE FPS: " + source.getVideoMode().fps + "SOURCE HEIGHT: " + source.getVideoMode().height +  "SOURCE WIDTH: " +source.getVideoMode().width);
            }
            VisionProcessor processor = (VisionProcessor)processed;            
            reporter.reportDistance(processor.getDistanceFromTarget(), processor.getLateralDistance(), frameTimer.getFrameCount());
            cvsource.putFrame(processor.getLastFrame());              
            //cvsource.putFrame(grip.hsvThresholdOutput());
        }
        catch ( Exception ex){
            ex.printStackTrace();
        }


    });
    runner.runForever();
    // loop forever
    
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }*/
  }

}
