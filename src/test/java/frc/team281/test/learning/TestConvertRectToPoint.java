package frc.team281.test.learning;

import java.util.Arrays;

import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import frc.team281.robot.ConvertRectToPoint;


public class TestConvertRectToPoint {
    @Test
    public void TestConvertRectToPoint2(){

        
        Point center1 = new Point(4,5);
        Size s1 = new Size(4, 8);
        double a1 = Math.PI/4;
        

        RotatedRect rect1 = new RotatedRect(center1, s1, a1);
        Point[] orderedPoints = ConvertRectToPoint.getOrderedPoints(rect1, rect1);
        
        System.out.println(Arrays.toString(orderedPoints));
    } 
} 

