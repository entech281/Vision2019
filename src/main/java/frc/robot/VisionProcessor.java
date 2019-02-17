package frc.robot;

import edu.wpi.first.vision.VisionPipeline;
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
        boolean debug = true;

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
        ArrayList<RotatedRect> nondumb = getRidOfDumbandAloneRectangles(targets);
        ArrayList<RotatedRect> initial = getRidofGibberishRectangles(targets);

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
        if (debug) {
            lastFrame = putFrameWithVisionTargetsIfSwitchIsOn(resizedImage, nondumb, initial, rvec, tvec);
        } else {
            lastFrame = putFrameWithVisionTargets(resizedImage, nondumb, rvec, tvec);
        }
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

    public static ArrayList<RotatedRect> getRidofGibberishRectangles(ArrayList<RotatedRect> input) {
        var filteredFromRandom = new ArrayList<RotatedRect>();
        var filteredFromSmall = new ArrayList<RotatedRect>();

        double MIN_WIDTH = 25;
        double MIN_HEIGHT = 15;
        double ASPECT_RATIO = 2;
        double aspectRatioRect = 0;

        for (RotatedRect rect : input) {
            /*if ( rect.boundingRect().y > MIN_Y && rect.boundingRect().y < MAX_Y){
                filteredFromRandom.add(rect);
            }*/

            aspectRatioRect = rect.size.height / rect.size.width;
            if (rect.size.height > MIN_HEIGHT && rect.size.width > MIN_WIDTH && aspectRatioRect >= ASPECT_RATIO) {
                filteredFromRandom.add(rect);
            }

        }

        //System.out.println("FilteredFromRandom" + filteredFromRandom.size());
        for (RotatedRect rect : filteredFromRandom) {
            if (rect.angle != 0) {
                filteredFromSmall.add(rect);
            }
        }

        return filteredFromSmall;
    }

    public static ArrayList<RotatedRect> getRidOfDumbandAloneRectangles(ArrayList<RotatedRect> input) {

        //this gets rid of rectangles in the top of the image, which are usually lights
        var filtered = new ArrayList<RotatedRect>();
        var filteredFromSmall = new ArrayList<RotatedRect>();
        var filteredFinal = new ArrayList<RotatedRect>();

        filteredFromSmall = getRidofGibberishRectangles(input);

        Collections.sort(filteredFromSmall, new RotatedRectangleComparator());
        int i = 0;
        int len = filteredFromSmall.size();

        //System.out.println("filtered_from_small"+ len);
        while (i < (len - 1)) {
            RotatedRect rect1 = filteredFromSmall.get(i);
            RotatedRect rect2 = filteredFromSmall.get(i + 1);
            System.out.println("RECT1: " + rect1.angle);
            System.out.println("RECT2: " + rect2.angle);
            if (rect1.angle < -75 && rect1.angle > -100 && rect2.angle > -50 && rect2.angle < 0) {
                filtered.add(rect1);
                filtered.add(rect2);
                i = i + 2;
            } else {
                i = i + 1;
            }
        }

        //System.out.println("filtered"+ filtered.size());
        int n = 1;
        int lenFiltered = filtered.size();
        int indexMax = 0;
        if (lenFiltered > 1) {
            while (n < (lenFiltered - 1)) {
                RotatedRect rect1 = filtered.get(n);
                RotatedRect minRect = filtered.get(indexMax);
                double distanceToCenter1 = Math.abs(CameraConstants.PROCESS_WIDTH / 2 - rect1.center.x);
                double distanceToCenterMin = Math.abs(CameraConstants.PROCESS_WIDTH / 2 - minRect.center.x);
                if (distanceToCenter1 < distanceToCenterMin) {
                    indexMax = n;
                }
                n = n + 1;
            }

            if (indexMax == 0) {
                filteredFinal.add(filtered.get(indexMax));
                filteredFinal.add(filtered.get(indexMax + 1));
            } else if (indexMax % 2 == 0) {
                filteredFinal.add(filtered.get(indexMax));
                filteredFinal.add(filtered.get(indexMax + 1));
            } else {
                filteredFinal.add(filtered.get(indexMax - 1));
                filteredFinal.add(filtered.get(indexMax));
            }
        }
        //System.out.println("filtered_final"+filteredFinal.size());
        return filteredFinal;

    }

    public ArrayList<RotatedRect> minimumBoundingRectangle(List<MatOfPoint> inputContours) {
        //System.out.println(inputContours.size() + " inputContours");

        var visionTarget = new ArrayList<RotatedRect>();

        for (MatOfPoint contour : inputContours) {
            visionTarget.add(Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray())));
        }
        return visionTarget;

    }

    public Mat putFrameWithVisionTargets(Mat img, List<RotatedRect> l, Mat rvec, Mat tvec) {
        Point points[] = new Point[4];
        var centers = new ArrayList<Point>();

        for (RotatedRect r : l) {
            centers.add(r.center);
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], new Scalar(255, 0, 0), 5);
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

    public Mat putFrameWithVisionTargetsIfSwitchIsOn(Mat img, List<RotatedRect> filtered, List<RotatedRect> initial, Mat rvec, Mat tvec) {
        Point points[] = new Point[4];
        var centers = new ArrayList<Point>();

        for (RotatedRect r : filtered) {
            centers.add(r.center);
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], new Scalar(255, 255, 0), 5);
            }

        }

        for (RotatedRect r : initial) {
            r.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(img, points[i], points[(i + 1) % 4], new Scalar(255, 0, 0), 5);
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
