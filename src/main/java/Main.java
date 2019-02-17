
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

import frc.robot.CameraConstants;
import frc.robot.ImageResizer;
import frc.robot.GripPipeline;
import frc.robot.VisionProcessor;
import frc.robot.VisionReporter;
import frc.timers.PeriodicReporter;
import frc.timers.TimeTracker;
import org.opencv.core.Size;

public final class Main {

    private static String configFileName = "/boot/frc.json";

    private static final int TEAM = 281;
    private static int frameNumber = 0;
    private interface TIMERS {
        String FRAME = "frame";
        String RESIZE = "resize";
        String REPORT= "report";

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
        //NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
        //ntinst.startClientTeam(TEAM);
        TimeTracker timeTracker = new TimeTracker();
        
        MjpegServer rawVideoServer = new MjpegServer("raw_video_server", 8081);
        CvSource cvsource = new CvSource("processed",
                VideoMode.PixelFormat.kMJPEG, 
                CameraConstants.PROCESS_WIDTH, 
                CameraConstants.PROCESS_HEIGHT, 30);

        //rawVideoServer.setSource(cvsource);
        UsbCamera source = new UsbCamera("PiCamera", "/dev/video0");        
        boolean success = source.setConfigJson(configFileText);
        System.out.println("Camera Configured: " + success);
        VideoMode videoMode = new VideoMode(VideoMode.PixelFormat.kBGR, CameraConstants.PROCESS_WIDTH, CameraConstants.PROCESS_HEIGHT, 90);
        source.setVideoMode(videoMode);
        rawVideoServer.setSource(cvsource);

        VisionReporter reporter = new VisionReporter();
        
        PeriodicReporter consoleReporter = new PeriodicReporter(5000);
        VisionProcessor processor = new VisionProcessor(new GripPipeline());
        CvSink sink = new CvSink("From Camera");
        sink.setSource(source);
        sink.setEnabled(true);
        Mat inputFrame = new Mat();
        ImageResizer cameraResizer = new ImageResizer();
        while (true) {
            timeTracker.startTimer(TIMERS.FRAME);
            try{
                
                sink.grabFrame(inputFrame);

                //do not process empty images
                if ( inputFrame.size().height <= 0 || inputFrame.size().width <= 0 ){
                    continue;
                }

                timeTracker.startTimer(TIMERS.RESIZE);
                Mat resized = cameraResizer.resizeImage(inputFrame,
                        new Size(
                                CameraConstants.PROCESS_WIDTH, 
                                CameraConstants.PROCESS_HEIGHT)
                );
                timeTracker.endTimer(TIMERS.RESIZE);
                
                timeTracker.startTimer(TIMERS.REPORT);
                reporter.reportDistance(processor.getDistanceFromTarget(), processor.getLateralDistance(), frameNumber);                
                timeTracker.endTimer(TIMERS.REPORT);
                processor.process(inputFrame);
                cvsource.putFrame(processor.getLastFrame());

                consoleReporter.reportIfNeeded(timeTracker);
             
            }
            catch ( Exception ex){
                System.out.println("Err: " + ex.getMessage());
            }
            frameNumber++;
            timeTracker.endTimer(TIMERS.FRAME);
        }
    }

}
