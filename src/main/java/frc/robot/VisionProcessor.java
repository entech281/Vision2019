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

    public static int MIN_Y = 50;
    public static int MAX_Y = 300;
    public static int CONSOLE_REPORTING_INTERVAL_MILLIS = 1000;
    private GripPipeline parent;
    private double distanceFromTarget = 0.0;
    private double lateralDistance = 0.0;
    private Mat lastFrame = null;

    private final PeriodicReporter periodicReporter = new PeriodicReporter(CONSOLE_REPORTING_INTERVAL_MILLIS);
    private final TimeTracker timer = new TimeTracker();

    private interface TIMERS {

        String GRIP = "grip";
        String RESIZE = "resize";
        String PROCESS = "process";
        String PNP = "pnp";
        String OUTPUT = "output";
    }

    public VisionProcessor(GripPipeline parent) {
        this.parent = parent;
    }

    @Override
    public void process(Mat sourceFrame) {

        timer.start(TIMERS.PROCESS);

        int startRow = (CameraConstants.PROCESS_HEIGHT) / 4;
        int endRow = (11 * CameraConstants.PROCESS_HEIGHT) / 12;
        

        Point topLeft = new Point(0, startRow);
        Point bottomRight = new Point(CameraConstants.PROCESS_WIDTH, endRow);

        Rect rectCrop = new Rect(topLeft, bottomRight);

        //Mat resizedImage = sourceFrame.submat(rectCrop);
        timer.start(TIMERS.RESIZE);
        Mat resizedImage = new Mat(sourceFrame, rectCrop);
        timer.end(TIMERS.RESIZE);

        timer.start(TIMERS.GRIP);
        parent.process(resizedImage);
        timer.end(TIMERS.GRIP);

        ArrayList<RotatedRect> targets = minimumBoundingRectangle(parent.findContoursOutput());
        ArrayList<RotatedRect> nondumb = new DumbAndAloneRectangleFilter().filter(targets);
        ArrayList<RotatedRect> initial = new GibberishRectangleFilter().filter(targets);

        Mat rvec = CameraConstants.getRvec();
        Mat tvec = CameraConstants.getTvec();

        if (nondumb.size() == 2) {
            timer.start(TIMERS.PNP);
            Calib3d.solvePnP(CameraConstants.getObjectPoints(),
                    CameraConstants.getImgPoint(nondumb),
                    CameraConstants.getCameraMatrix(),
                    CameraConstants.getDistCoeffs(), rvec, tvec, true);
            timer.end(TIMERS.PNP);
        }

        timer.start(TIMERS.OUTPUT);
        boolean debug = false;
        lastFrame = putFrameWithVisionTargets(debug,resizedImage, nondumb, initial, rvec, tvec);

        timer.end(TIMERS.OUTPUT);

        periodicReporter.reportIfNeeded(
                String.format("Dist:%.3f, Lateral:%.3f, Contours:%d, Filtered:%d,Targets:%d, FilteredTargets:%d ",
                        distanceFromTarget,
                        lateralDistance,
                        parent.findContoursOutput().size(),
                        parent.filterContoursOutput().size(),
                        targets.size(),
                        nondumb.size()
                ));
        timer.end(TIMERS.PROCESS);
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

    public Mat putFrameWithVisionTargets(boolean showAll, Mat img, List<RotatedRect> selected, List<RotatedRect> initial, Mat rvec, Mat tvec) {
        Point points[] = new Point[4];
        var centers = new ArrayList<Point>();

        for (RotatedRect r : selected) {
            centers.add(r.center);
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], new Scalar(0, 0, 255), 5);
            }
        }
        if ( showAll ){
            for (RotatedRect r : initial) {
                centers.add(r.center);
                r.points(points);
                for (int i = 0; i < 4; i++) {
                    Imgproc.line(img, points[i], points[(i + 1) % 4], new Scalar(255, 0, 0), 5);
                }
            }              
        }
      
        
        DecimalFormat df = new DecimalFormat("#, ###.##");
        if (centers.size() == 2) {
            Imgproc.line(img, centers.get(0), centers.get(1), new Scalar(0, 255, 0), 6);
            Point midpoint = new Point(100, 200);
            String distance = df.format(Math.sqrt((centers.get(0).x - centers.get(1).x) * (centers.get(0).x - centers.get(1).x) + (centers.get(0).y - centers.get(1).y) * (centers.get(0).y - centers.get(1).y)));
            Imgproc.putText(img, distance, midpoint, Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255), 2);
        }
        double[] distanceTarget = tvec.get(2, 0);
        double[] lateralDist = tvec.get(0, 0);
        distanceFromTarget = distanceTarget[0];
        lateralDistance = lateralDist[0];

        Imgproc.putText(img, df.format(distanceFromTarget), new Point(20, 10), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255), 2);

        return img;
    }

}
