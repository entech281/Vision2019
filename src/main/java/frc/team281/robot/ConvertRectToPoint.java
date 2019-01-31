package frc.team281.robot;

import java.util.ArrayList;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;


public class ConvertRectToPoint {

    public ConvertRectToPoint(){
        
    }
    public ArrayList <Point> getOrderedPoints(RotatedRect first, RotatedRect second){

        var allpoints = new ArrayList<Point>();
        var firstpoints = new ArrayList<Point>();
        var secondpoints = new ArrayList<Point>();
    
        if (isFirstLeftMost(first, second)){
            firstpoints = getPointsFromLeftRect(first);
            secondpoints = getPointsFromRightRect(second);
            allpoints.addAll(firstpoints);
            allpoints.addAll(secondpoints);
        }
        else{
            firstpoints = getPointsFromLeftRect(second);
            secondpoints = getPointsFromRightRect(first);
            allpoints.addAll(secondpoints);
            allpoints.addAll(firstpoints);
        }
        return allpoints;
    }


    public ArrayList <Point> getPointsFromLeftRect(RotatedRect first){
        var arraypoints = new ArrayList<Point>();
        
        double h=first.size.width;
        double w=first.size.height;
        double hyp = Math.sqrt(h*h + w*w);
        double c= Math.PI/180;
        double total_angle_radian=first.angle+Math.asin(w/h);
        double angle_radian = first.angle;

        //angle of rotated rect is in refrence to vertical

        double first_x=first.center.x+Math.sin(total_angle_radian)*hyp/2;
        double first_y=first.center.y-Math.cos(total_angle_radian)*hyp/2;

        //Starts at rightmost point and goes clockwise ending at uppermost point

        arraypoints.add(new Point(first_x, first_y));
        arraypoints.add(
            new Point(
                first_x-w*Math.sin(angle_radian), 
                first_y+w*Math.cos(angle_radian)
                )
            );
        arraypoints.add(
            new Point(
                first_x-hyp*Math.sin(total_angle_radian), 
                first_y+hyp*Math.cos(total_angle_radian)
                )
            );
        arraypoints.add(
            new Point(
                first_x-h*Math.sin(angle_radian), 
                first_y-h*Math.cos(angle_radian)
                )
            );
        return arraypoints;
    } 

    public ArrayList <Point> getPointsFromRightRect(RotatedRect first){
        var arraypoints = new ArrayList<Point>();
        
        double h=first.size.width;
        double w=first.size.height;
        double hyp = Math.sqrt(h*h + w*w);
        double c= Math.PI/180;
        double total_angle_radian=first.angle+Math.asin(w/h);
        double angle_radian = first.angle;

        //angle of rotated rect is in refrence to vertical

        double first_x=first.center.x-Math.sin(total_angle_radian)*hyp/2;
        double first_y=first.center.y-Math.cos(total_angle_radian)*hyp/2;

        //Starts at rightmost point and goes clockwise ending at uppermost point

        arraypoints.add(new Point(first_x, first_y));
        arraypoints.add(
            new Point(
                first_x+w*Math.sin(angle_radian), 
                first_y+w*Math.cos(angle_radian)
                )
            );
        arraypoints.add(
            new Point(
                first_x+hyp*Math.sin(total_angle_radian), 
                first_y+hyp*Math.cos(total_angle_radian)
                )
            );
        arraypoints.add(
            new Point(
                first_x+h*Math.sin(angle_radian), 
                first_y-h*Math.cos(angle_radian)
                )
            );
        return arraypoints;
    } 

    public boolean isFirstLeftMost( RotatedRect first, RotatedRect second){
        if (first.center.x<second.center.x){
            return true;
        }else{
            return false;
        }

    }
}