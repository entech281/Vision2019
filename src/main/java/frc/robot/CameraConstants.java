package frc.robot;

import java.awt.List;
import java.util.ArrayList;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.RotatedRect;
import org.opencv.core.Point;
import org.opencv.core.*;

public class CameraConstants {

    public static int PROCESS_WIDTH=640;
    public static int PROCESS_HEIGHT=360;
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

        MatOfPoint2f imgPoint = new MatOfPoint2f();
        Point[] point = new Point[8];

            RotatedRect rect1 = VisionTargets.get(0);
            RotatedRect rect2 = VisionTargets.get(1);
            
            point = ConvertRectToPoint.getOrderedPoints(rect1, rect2);

        imgPoint.fromArray(point);
        return imgPoint;

        
    }

    public static MatOfDouble getDistCoeffs() {
        MatOfDouble distCoeffs = new MatOfDouble();
        //distCoeffs.zeros(1, 4, CvType.CV_32F);
        return distCoeffs;
    }

    public static Mat getCameraMatrix() {
        Mat cameraMat = new Mat(3,3,CvType.CV_32FC1);

        cameraMat.put(0, 0, PROCESS_WIDTH, 0.0, PROCESS_WIDTH/2.0, 
                      0.0, PROCESS_HEIGHT, PROCESS_HEIGHT/2.0, 
                    0.0, 0.0, 1.0 );

        //cameraMat.put(0, 0, 320.0);
        //cameraMat.put(1, 1, 240.0);
        //cameraMat.put(0, 2, 160.0);
        //cameraMat.put(1, 2, 120.0);
        
        return cameraMat;
    }

    public static Mat getRvec(){
        Mat rvec = new Mat(3,1,CvType.CV_64FC1);
        rvec.put(0, 0, 0.0);
        rvec.put(1, 0, 0.0);
        rvec.put(2, 0, 0.0);
        return rvec;
    }
    public static Mat getTvec(){
        Mat tvec = new Mat(3,1,CvType.CV_64FC1);
        tvec.put(0, 0, 0.0);
        tvec.put(1, 0, 0.0);
        tvec.put(2, 0, 30.0);
        return tvec;
    }


}