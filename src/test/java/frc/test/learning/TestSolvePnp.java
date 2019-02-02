package frc.test.learning;

import java.util.ArrayList;

import org.junit.Test;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.core.Point;

import frc.robot.CameraConstants;
import frc.robot.ConvertRectToPoint;


public class TestSolvePnp {
    
    public void TestValidResponse(){
        MatOfPoint3f objectPoints = new MatOfPoint3f();
        objectPoints = CameraConstants.getObjectPoints();

        MatOfDouble distCoeff = new MatOfDouble();
        distCoeff = CameraConstants.getDistCoeffs();

        Mat cameraMat = new Mat();
        cameraMat = CameraConstants.getCameraMatrix();

        Point center1 = new Point(100,120);
        Point center2 = new Point(220,120);
        Size size = new Size(60, 165);
        double a1 = 14;
        double a2 = -14;
        
        RotatedRect rect1 = new RotatedRect(center1, size, a1);
        RotatedRect rect2 = new RotatedRect(center2, size, a2);
        ArrayList<RotatedRect> VisionTarget = new ArrayList<RotatedRect>();
        VisionTarget.add(rect1);
        VisionTarget.add(rect2);

        MatOfPoint2f imgPoint = CameraConstants.getImgPoint(VisionTarget);

        Mat rvec = CameraConstants.getRvec();
        Mat tvec = CameraConstants.getTvec();

        Calib3d.solvePnP(objectPoints, imgPoint, cameraMat, distCoeff, rvec, tvec, true);

        System.out.print(rvec);
        System.out.print(tvec);
    } 
} 