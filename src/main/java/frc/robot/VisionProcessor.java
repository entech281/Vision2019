package frc.robot;

import edu.wpi.first.vision.VisionPipeline;
import frc.robot.filters.CenterRectangleIndexFinder;
import frc.robot.filters.FinalVisionTargetFilter;
import frc.robot.filters.GibberishRectangleFilter;
import frc.timers.FramerateTracker;
import frc.timers.PeriodicReporter;
import frc.timers.TimeTracker;
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

import frc.robot.lock.*;

/**
 * This has a lot of the code that used to be inside of GripPipeline
 *
 * @author dcowden
 */
public class VisionProcessor implements VisionPipeline {

    private interface TIMERS {

        String GRIP = "p:grip";
        String RESIZE = "p:resize";
        String PNP = "p:pnp";
        String OUTPUT = "p:output";
        String REPORT = "p:report";
        String ALL = "p:all";
    }

    public interface COLORS {

        Scalar BLUE = new Scalar(255, 0, 0);
        Scalar GREEN = new Scalar(0, 255, 0);
        Scalar RED = new Scalar(0, 0, 255);
        Scalar YELLOW = new Scalar(0, 165, 255);
        Scalar PURPLE = new Scalar(211, 0, 148);
    }
    public static double LATERAL_DISTANCE_FACTOR = 1.0;
    public static double PERPENDICULAR_DISTANCE_FACTOR = 0.6178;
    public static int CONSOLE_REPORTING_INTERVAL_MILLIS = 1000;
    public static double UNKNOWN = 99999;
    public static final boolean SWITCH_ONE_RECTANGLE_IS_SUFFICIENT = false;
    private GripPipeline parent;
    private double averageArea = 0.0;
    private double averageDistanceToCenter = 0.0;
    private double distanceFromTarget= 0.0;
    private double lateralDistance = 0.0;
    private int sizeSelected = 0;
    private double pixelPerInch = 0.0;
    private Mat lastFrame = null;
    public boolean foundTarget = false;

    private int index = 0;
    private boolean targetLock = false;
    private int prevNumRects = 0;
    private int numRects = 0;

    private final PeriodicReporter periodicReporter
            = new PeriodicReporter(CONSOLE_REPORTING_INTERVAL_MILLIS);
    private final TimeTracker timer;
    private final TargetLockTracker lockTracker;

    public VisionProcessor(GripPipeline parent, TimeTracker timer, TargetLockTracker lockTracker) {
        this.parent = parent;
        this.timer = timer;
        this.lockTracker = lockTracker;
    }

    private Mat cropImage(Mat input) {
        int startRow = CameraConstants.RECOGNIZE_TOP;
        int endRow = CameraConstants.RECOGNIZE_BOTTOM;

        Point topLeft = new Point(0, startRow);
        Point bottomRight = new Point(CameraConstants.PROCESS_WIDTH, endRow);
        Rect rectCrop = new Rect(topLeft, bottomRight);

        return new Mat(input, rectCrop);

    }

    public void computeArea(ArrayList<RotatedRect> input) {
        double areaRect = 0.0;
        double totalArea = 0.0;
        for(RotatedRect rect : input) {
            areaRect = rect.size.height *rect.size.width;
            totalArea = totalArea + areaRect;
        }
        averageArea = totalArea/input.size();
    }

    public void computeAverageDistanceToCenter(ArrayList<RotatedRect> input){
        double distanceToCenter = 0.0;
        double netDistanceToCenter=0.0;
        for(RotatedRect rect : input){
            distanceToCenter = rect.center.x - (CameraConstants.PROCESS_WIDTH)/2;
            netDistanceToCenter = netDistanceToCenter + distanceToCenter;
        }
        averageDistanceToCenter = netDistanceToCenter/input.size();
    }

    public void computeDistanceIfOnlyOneRectangle(double pixelPerInch, double averageDistanceToCenter){
        if(averageDistanceToCenter<0){
            lateralDistance = averageDistanceToCenter/pixelPerInch - 5.5;
        }
        else{
            lateralDistance = averageDistanceToCenter/pixelPerInch + 5.5;
        }
    }

    public void computeAndSetDistanceFromTargets(double area, double averageDistanceToCenter, int sizeSelected){
        distanceFromTarget = 430*Math.pow(area/2, -0.494) - CameraConstants.DISTANCE_BETWEEN_CAMERA_AND_FRONT;
        pixelPerInch = 183.3526/distanceFromTarget;
        if(sizeSelected == 1){
            computeDistanceIfOnlyOneRectangle(pixelPerInch, averageDistanceToCenter);
        }
        else{
        lateralDistance = averageDistanceToCenter/pixelPerInch;
        }
    }

    @Override
    public void process(Mat sourceFrame) {
        timer.start(TIMERS.ALL);
        timer.start(TIMERS.RESIZE);
        Mat resizedImage = cropImage(sourceFrame);
        timer.end(TIMERS.RESIZE);

        timer.start(TIMERS.GRIP);
        parent.process(resizedImage);
        timer.end(TIMERS.GRIP);

        
        ArrayList<RotatedRect> initial = minimumBoundingRectangle(parent.filterContoursOutput());
        ArrayList<RotatedRect> findContours = minimumBoundingRectangle(parent.findContoursOutput());
        ArrayList<RotatedRect> ok = new GibberishRectangleFilter().filter(initial);
        index = new CenterRectangleIndexFinder().getIndex(ok);
        if(lockTracker.isTargetLockOn()){
            lockTracker.checkLock(ok.size());
        }else{
            lockTracker.setupLock(ok.size(), index);
        }        
        ArrayList<RotatedRect> selected = new FinalVisionTargetFilter().filter(ok, index);

        timer.start(TIMERS.OUTPUT);
        drawRectanglesOnImage(resizedImage, initial, COLORS.BLUE, false);
        drawRectanglesOnImage(resizedImage, findContours, COLORS.PURPLE, false);
        drawRectanglesOnImage(resizedImage, ok, COLORS.RED, false);
        drawRectanglesOnImage(resizedImage, selected, COLORS.BLUE, targetLock);
        
        computeArea(selected);
        computeAverageDistanceToCenter(selected);
        computeAndSetDistanceFromTargets(averageArea, averageDistanceToCenter, selected.size());
        putOutputTextOnFrame(resizedImage);

        timer.end(TIMERS.OUTPUT);
        timer.start(TIMERS.REPORT);
        periodicReporter.reportIfNeeded(
                String.format("lateral:%.3f, Distance:%.3f, Contours:%d. Targets:\nInitial:%d\nok:%d\nselected:%d ",
                        lateralDistance,
                        distanceFromTarget,
                        parent.findContoursOutput().size(),
                        initial.size(),
                        ok.size(),
                        selected.size()
                ));
        timer.end(TIMERS.REPORT);
        timer.end(TIMERS.ALL);
        lastFrame = resizedImage;
    }

    public Mat getLastFrame() {
        return lastFrame;
    }

    public boolean getFoundTarget(){
        return foundTarget;
    }

    public double getDistanceFromTarget() {
        return distanceFromTarget;
    }

    public double getLateralDistance() {
        if(sizeSelected == 2){
        return lateralDistance;
        }
        else{
            return UNKNOWN;
        }
    }

    public void drawRectanglesOnImage(Mat img, List<RotatedRect> rectangles, Scalar color, boolean targetLock) {
        Point points[] = new Point[4];
        for (RotatedRect r : rectangles) {
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], color, 5);
            }
            if(targetLock){
                targetLockDraw(img, r.center, color);
            }
        }
    }

    public void targetLockDraw(Mat img, Point center, Scalar color) {
            Imgproc.line(img, new Point(center.x, center.y + 5), new Point(center.x, center.y - 5), color, 1);
            Imgproc.line(img, new Point(center.x + 5, center.y), new Point(center.x - 5, center.y), color, 1);
            Imgproc.circle(img, center, 3, color);
    }

    public void putOutputTextOnFrame(Mat img) {
        Imgproc.putText(img, String.format("lateralDistance: %.2f", lateralDistance), new Point(30, 60), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
        Imgproc.putText(img, String.format("Distance: %.2f", distanceFromTarget), new Point(30, 90), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
    }

    public ArrayList<RotatedRect> minimumBoundingRectangle(List<MatOfPoint> inputContours) {

        var visionTarget = new ArrayList<RotatedRect>();
        for (MatOfPoint contour : inputContours) {
            visionTarget.add(Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray())));
        }
        return visionTarget;

    }
}
