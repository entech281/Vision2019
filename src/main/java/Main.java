
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

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.CameraConstants;
import frc.robot.FrameReader;
import frc.robot.ImageResizer;
import frc.robot.GripPipeline;
import frc.robot.VisionProcessor;
import frc.robot.VisionReporter;
import frc.timers.FramerateTracker;
import frc.timers.PeriodicReporter;
import frc.timers.TimeTracker;
import org.opencv.core.Size;

public final class Main {

    private static String configFileName = "/boot/frc.json";

    private static final int TEAM = 281;

    private interface TIMERS {

        String FRAME = "m:all";
        String RESIZE = "m:resize";
        String REPORT = "m:report";
        String GRAB = "m:grab";
        String PROCESS= "m:process";
        String PUT ="m:put";
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String... args) throws IOException {

        if (args.length > 0) {
            configFileName = args[0];
        }

        String configFileText = readFile(configFileName, StandardCharsets.UTF_8);

        // start NetworkTables
        NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
        ntinst.startClientTeam(TEAM);
        ntinst.startClient();

        TimeTracker timer = new TimeTracker();
        timer.setEnabled(true);
        MjpegServer rawVideoServer = new MjpegServer("raw_video_server", 1183);
        CvSource cvsource = new CvSource("processed",
                VideoMode.PixelFormat.kMJPEG,
                CameraConstants.PROCESS_WIDTH,
                CameraConstants.PROCESS_HEIGHT, 5);

 
        UsbCamera source = new UsbCamera("PiCamera", "/dev/video0");

        boolean success = source.setConfigJson(configFileText);
        System.out.println("Camera Configured: " + success);
        rawVideoServer.setSource(cvsource);

        VisionReporter reporter = new VisionReporter();
        FramerateTracker frames = new FramerateTracker();
        PeriodicReporter consoleReporter = new PeriodicReporter(2000);
        FramerateTracker frameRate = new FramerateTracker();
        VisionProcessor processor = new VisionProcessor(new GripPipeline(timer),timer, reporter, frames);
        
        CvSink sink = new CvSink("From Camera");
        sink.setSource(source);
        sink.setEnabled(true);
        
        //Mat inputFrame = new Mat();
        ImageResizer cameraResizer = new ImageResizer();
        
        FrameReader frameReader  = new FrameReader(cameraResizer,sink,timer);
        frameReader.setDaemon(true);
        frameReader.start();
        
        while (true) {
            
            try {
                if ( frameReader.hasNewFrame()){
                    timer.start(TIMERS.FRAME);
                    processor.process(frameReader.getCurrentFrame());
                    timer.end(TIMERS.PROCESS);

                    timer.start(TIMERS.PUT);
                    cvsource.putFrame(processor.getLastFrame());
                    timer.end(TIMERS.PUT);

                    timer.start(TIMERS.REPORT);
                    reporter.reportDistance(processor.getDistanceFromTarget(), processor.getLateralDistance(), frameRate.getTicks(), processor.getFoundTarget());
                    consoleReporter.reportIfNeeded(timer,frameRate);
                    timer.end(TIMERS.REPORT);   
                    frameRate.frame();
                    timer.end(TIMERS.FRAME);
                }
          

            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
           
            
        }
    }

}
