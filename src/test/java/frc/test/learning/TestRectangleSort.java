package frc.test.learning;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import frc.robot.ConvertRectToPoint;
import frc.robot.VisionProcessor;
import frc.robot.filters.DumbAndAloneRectangleFilter;


public class TestRectangleSort {
    @Test
    public void Case1Perfect(){

        Point center1 = new Point(240,200);
        Size s1 = new Size(4, 8);
        double a1 = 45;
        RotatedRect rect1 = new RotatedRect(center1, s1, a1);

        Point center2 = new Point(300,200);
        double a2 = -45;
        RotatedRect rect2 = new RotatedRect(center2, s1, a2);

        var rotatedRects = new ArrayList<RotatedRect>();
        rotatedRects.add(rect2);
        rotatedRects.add(rect1);

        var RotatedRectFinal = new ArrayList<RotatedRect>();
        RotatedRectFinal =new DumbAndAloneRectangleFilter().filter(rotatedRects);
        
        System.out.println(RotatedRectFinal.toString());
    } 

    @Test
    public void Case2Backwards(){

        Point center1 = new Point(240,200);
        Size s1 = new Size(4, 8);
        double a1 = -45;
        RotatedRect rect1 = new RotatedRect(center1, s1, a1);

        Point center2 = new Point(300,200);
        double a2 = 45;
        RotatedRect rect2 = new RotatedRect(center2, s1, a2);

        var RotatedRects = new ArrayList<RotatedRect>();
        RotatedRects.add(rect2);
        RotatedRects.add(rect1);

        var RotatedRectFinal = new ArrayList<RotatedRect>();
        RotatedRectFinal =new DumbAndAloneRectangleFilter().filter(RotatedRects);
        
        System.out.println(RotatedRectFinal.toString());
    } 

    @Test
    public void Case3Three(){

        Point center1 = new Point(240,200);
        Size s1 = new Size(4, 8);
        double a1 = 45;
        RotatedRect rect1 = new RotatedRect(center1, s1, a1);

        Point center2 = new Point(300,200);
        double a2 = -45;
        RotatedRect rect2 = new RotatedRect(center2, s1, a2);

        Point center3 = new Point(400,200);
        double a3 = 45;
        RotatedRect rect3 = new RotatedRect(center3, s1, a3);

        var rotatedRects = new ArrayList<RotatedRect>();
        rotatedRects.add(rect2);
        rotatedRects.add(rect1);
        rotatedRects.add(rect3);

        var RotatedRectFinal = new ArrayList<RotatedRect>();
        RotatedRectFinal =new DumbAndAloneRectangleFilter().filter(rotatedRects);
        
        System.out.println(RotatedRectFinal.toString());
    } 

    @Test
    public void Case3ThreeShifted(){

        Point center1 = new Point(140,200);
        Size s1 = new Size(4, 8);
        double a1 = 45;
        RotatedRect rect1 = new RotatedRect(center1, s1, a1);

        Point center2 = new Point(200,200);
        double a2 = -45;
        RotatedRect rect2 = new RotatedRect(center2, s1, a2);

        Point center3 = new Point(300,200);
        double a3 = 45;
        RotatedRect rect3 = new RotatedRect(center3, s1, a3);

        var rotatedRects = new ArrayList<RotatedRect>();
        rotatedRects.add(rect2);
        rotatedRects.add(rect1);
        rotatedRects.add(rect3);

        var RotatedRectFinal = new ArrayList<RotatedRect>();
        RotatedRectFinal =new DumbAndAloneRectangleFilter().filter(rotatedRects);
        
        System.out.println(RotatedRectFinal.toString());
        //Expected Output (140,200) (200,200)
    } 
    @Test
    public void Case4Five(){

        Point center1 = new Point(240,200);
        Size s1 = new Size(4, 8);
        double a1 = 45;
        RotatedRect rect1 = new RotatedRect(center1, s1, a1);

        Point center2 = new Point(300,200);
        double a2 = -45;
        RotatedRect rect2 = new RotatedRect(center2, s1, a2);

        Point center3 = new Point(360,200);
        double a3 = 45;
        RotatedRect rect3 = new RotatedRect(center3, s1, a3);

        Point center4 = new Point(420,200);
        double a4 = -45;
        RotatedRect rect4 = new RotatedRect(center4, s1, a4);

        Point center5 = new Point(480,200);
        double a5 = 45;
        RotatedRect rect5 = new RotatedRect(center5, s1, a5);

        var rotatedRects = new ArrayList<RotatedRect>();
        rotatedRects.add(rect4);
        rotatedRects.add(rect2);
        rotatedRects.add(rect1);
        rotatedRects.add(rect5);
        rotatedRects.add(rect3);

        var RotatedRectFinal = new ArrayList<RotatedRect>();
        RotatedRectFinal =new DumbAndAloneRectangleFilter().filter(rotatedRects);
        
        System.out.println(RotatedRectFinal.toString());
    } 
} 
