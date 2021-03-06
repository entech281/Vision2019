package frc.test.learning;

import java.util.Arrays;

import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import frc.robot.ConvertRectToPoint;


public class TestConvertRectToPoint {
    @Test
    public void TestConvertRectToPoint2(){

        
        Point center1 = new Point(4,5);
        Size s1 = new Size(4, 8);
        double a1 = 45;
        

        RotatedRect rect1 = new RotatedRect(center1, s1, a1);
        Point[] orderedPoints = ConvertRectToPoint.getOrderedPoints(rect1, rect1);
        
        System.out.println(Arrays.toString(orderedPoints));
    } 
} 

