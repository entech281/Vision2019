package frc.team281.test;

import org.opencv.core.RotatedRect;
import java.util.ArrayList;
import org.opencv.core.Point;
import frc.team281.robot.ConvertRectToPoint;
import org.junit.*;

public class TestConvertRectToPoint{
    @Test
    public void testgetPointsFromLeftRect(){
        var actual_list= new ArrayList <Point>();
        actual_list.add(new Point(.5,.5));
        actual_list.add(new Point(.5,-.5));
        actual_list.add(new Point(-.5,-.5));
        actual_list.add(new Point(-.5,.5));
        Point center = new Point(0,0);
        //RotatedRect rect = new RotatedRect(center, 1, 1, 0);
        
        //assertEquals(actual_list, arraypoints);

    } 
}
