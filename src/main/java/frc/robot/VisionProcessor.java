package frc.robot;

import edu.wpi.first.vision.VisionPipeline;
import frc.robot.filters.DumbAndAloneRectangleFilter;
import frc.robot.filters.GibberishRectangleFilter;
import frc.timers.PeriodicReporter;
import frc.timers.TimeTracker;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.*;
import java.util.*;

/**
 * This has a lot of the code that used to be inside of GripPipeline
 *
 * @author dcowden
 */
public class VisionProcessor implements VisionPipeline {

    public static double LATERAL_DISTANCE_FACTOR=1.0;
    public static double PERPENDICULAR_DISTANCE_FACTOR=0.6178;
    
    public static int CONSOLE_REPORTING_INTERVAL_MILLIS = 1000;
    private GripPipeline parent;
    private double distanceFromTarget = 0.0;
    private double lateralDistance = 0.0;
    private Mat lastFrame = null;

    private final PeriodicReporter periodicReporter = 
            new PeriodicReporter(CONSOLE_REPORTING_INTERVAL_MILLIS);
    private final TimeTracker timer;

    private interface TIMERS {

        String GRIP = "p:grip";
        String RESIZE = "p:resize";
        String PROCESS = "p:process";
        String PNP = "p:pnp";
        String OUTPUT = "p:output";
        String REPORT = "p:report";
    }

    public interface COLORS{
        Scalar BLUE = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);
        Scalar RED = new Scalar(0,0,255);
}
    public VisionProcessor(GripPipeline parent, TimeTracker timer) {
        this.parent = parent;
        this.timer = timer;
    }

    private Mat cropImage(Mat input ){
        int startRow = CameraConstants.RECOGNIZE_TOP;
        int endRow = CameraConstants.RECOGNIZE_BOTTOM;
        

        Point topLeft = new Point(0, startRow);
        Point bottomRight = new Point(CameraConstants.PROCESS_WIDTH, endRow);
        Rect rectCrop = new Rect(topLeft, bottomRight);

        
        return new Mat(input, rectCrop);        
    }
    
    public void computeAndSetDistances(Mat rvec, Mat tvec){
        double[] distanceTarget = tvec.get(2, 0);
        double[] lateralDist = tvec.get(0, 0);
        distanceFromTarget = distanceTarget[0] * PERPENDICULAR_DISTANCE_FACTOR;
        lateralDistance = lateralDist[0] * LATERAL_DISTANCE_FACTOR;        
    }
    
    @Override
    public void process(Mat sourceFrame) {

        timer.start(TIMERS.PROCESS);
        timer.start(TIMERS.RESIZE);
        Mat resizedImage = cropImage(sourceFrame);
        timer.end(TIMERS.RESIZE);
        
        timer.start(TIMERS.GRIP);
        parent.process(resizedImage);
        timer.end(TIMERS.GRIP);

        ArrayList<RotatedRect> initial = minimumBoundingRectangle(parent.filterContoursOutput());        
        ArrayList<RotatedRect> ok = new GibberishRectangleFilter().filter(initial);
        ArrayList<RotatedRect> selected = new DumbAndAloneRectangleFilter().filter(ok);
        
        Mat rvec = CameraConstants.getRvec();
        Mat tvec = CameraConstants.getTvec();

        if (selected.size() == 2) {
            timer.start(TIMERS.PNP);
            Calib3d.solvePnP(CameraConstants.getObjectPoints(),
                    CameraConstants.getImgPoint(selected),
                    CameraConstants.getCameraMatrix(),
                    CameraConstants.getDistCoeffs(), rvec, tvec, true);
            timer.end(TIMERS.PNP);
        }

        timer.start(TIMERS.OUTPUT);
        drawRectanglesOnImage(resizedImage,initial,COLORS.BLUE);
        drawRectanglesOnImage(resizedImage,ok,COLORS.RED);
        drawRectanglesOnImage(resizedImage,selected,COLORS.GREEN);
        computeAndSetDistances(rvec,tvec);
        putOutputTextOnFrame(resizedImage);
        //putSelectedTargetsOnFrame(resizedImage, selected, rvec, tvec);

        timer.end(TIMERS.OUTPUT);
        timer.start(TIMERS.REPORT);
        periodicReporter.reportIfNeeded(
                String.format("Dist:%.3f, Lateral:%.3f, Contours:%d. Targets:\nInitial:%d\nok:%d\nselected:%d ",
                        distanceFromTarget,
                        lateralDistance,
                        parent.findContoursOutput().size(),
                        initial.size(),
                        ok.size(),                        
                        selected.size()

                ));
        timer.end(TIMERS.REPORT);
        timer.end(TIMERS.PROCESS);
        lastFrame = resizedImage;
    }

    public Mat getLastFrame() {
        return lastFrame;
    }

    public double getDistanceFromTarget() {
        return distanceFromTarget;
    }

    public double getLateralDistance() {
        return lateralDistance;
    }

    public void drawRectanglesOnImage( Mat img, List<RotatedRect> rectangles, Scalar color){
        Point points[] = new Point[4];
        for (RotatedRect r : rectangles) {
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], color, 5);
            }
        }        
    }

    public void putOutputTextOnFrame( Mat img){      
        Imgproc.putText(img, String.format("D: %.2f",distanceFromTarget), new Point(30, 60), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255,255,255), 2);
        Imgproc.putText(img, String.format("L: %.2f",lateralDistance), new Point(30, 80), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255,255,255), 2);
    }
    public void putSelectedTargetsOnFrame( Mat img, List<RotatedRect> selected, Mat rvec, Mat tvec) {
        
        //TODO: i dont like this: it mixes computing the distances with the logic to display the text
  
       
    }
    
    public ArrayList<RotatedRect> minimumBoundingRectangle(List<MatOfPoint> inputContours){
                
        var visionTarget = new ArrayList<RotatedRect>();

        for (MatOfPoint contour: inputContours){
                visionTarget.add(Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray())));
        }
        return visionTarget;

    }
}
