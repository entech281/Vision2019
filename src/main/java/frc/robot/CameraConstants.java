package frc.robot;

import java.awt.List;
import java.util.ArrayList;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.RotatedRect;
import org.opencv.core.Point;

public class CameraConstants {

    public static MatOfPoint3f getObjectPoints() {
        MatOfPoint3f objectPoint = new MatOfPoint3f();
        var point = new Point3[8];
        point[0] = new Point3(-7.174, -4.91, 0.0);
        point[1] = new Point3(-5.875, 0.484, 0.0);
        point[2] = new Point3(-4.0, 0.0, 0.0);
        point[3] = new Point3(-5.331, -5.337, 0.0);
        point[4] = new Point3(5.331, -5.337, 0);
        point[5] = new Point3(4.0, 0.0, 0.0);
        point[6] = new Point3(5.42, 1.98, 0.0);
        point[7] = new Point3(6.747, -5.167, 0.0);
        objectPoint.fromArray(point);
        return objectPoint;
    }

    public static MatOfPoint2f getImgPoint(ArrayList<RotatedRect> VisionTargets) {
        RotatedRect rect1 = VisionTargets.get(0);
        RotatedRect rect2 = VisionTargets.get(1);
        MatOfPoint2f objectPoint = new MatOfPoint2f();
        Point[] point = new Point[8];
        point = ConvertRectToPoint.getOrderedPoints(rect1, rect2);

        objectPoint.fromArray(point);
        return objectPoint;
    }

    public static MatOfDouble getDistCoeffs() {
        MatOfDouble distCoeffs = new MatOfDouble();

        return distCoeffs;
    }

    public static Mat getCameraMatrix() {
        MatOfDouble cameraMat = new MatOfDouble();
        cameraMat.zeros(3, 3, CvType.CV_64FC1);
        cameraMat.put(0, 0, 320);
        cameraMat.put(1, 1, 240);
        cameraMat.put(0, 2, 160);
        cameraMat.put(1, 2, 120);
        
        return cameraMat;
    }

    public static Mat getRvec(){
        MatOfDouble rvec = new MatOfDouble();
        rvec.put(0, 0, 0);
        rvec.put(1, 0, 0);
        rvec.put(2, 0, 0);
        return null;
    }
    public static Mat getTvec(){
        MatOfDouble rvec = new MatOfDouble();
        rvec.put(0, 0, 0);
        rvec.put(1, 0, 0);
        rvec.put(2, 0, 30);
        return null;
    }
}