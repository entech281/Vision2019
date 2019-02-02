package frc.team281.robot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

public class ConvertRectToPoint {


    public static Point[] getOrderedPoints(RotatedRect first, RotatedRect second) {

        var allpoints = new Point[8];

        Point[] vertices_first = new Point[4];
        Point[] vertices_second = new Point[4];

        // Stores vertices in this order: bottomleft, topleft, topright, bottomright
        first.points(vertices_first);
        second.points(vertices_second);

        if (isFirstLeftMost(first, second)) {
            allpoints = concatenate(vertices_first,vertices_second);
        } else{
            allpoints = concatenate(vertices_second, vertices_first);
        }
        return allpoints;
    }

    public static  Point[] concatenate(Point[] a, Point[] b) 
    { 
        Point[] combine = new Point[8];

        for (int i=0; i<8; i++){
            if (i<4){
                combine[i] = a[i];
            }
            else{
                combine[i] = b[i-4];
            }
        }
        

        return combine;

    }

public static boolean isFirstLeftMost( RotatedRect first, RotatedRect second){
        if (first.center.x<second.center.x){
            return true;
        }else{
            return false;
        }

    }
}


    /*public static ArrayList<Point> centerAtOrigin(RotatedRect rect) {
        //Order: Top right, bottom right, bottom left, top left
        double height = rect.size.height;
        double width = rect.size.width;

        Point topRight = new Point((0.5)*height,(0.5)*width);
        Point bottomRight = new Point((0.5)* height,-((0.5)*width));
        Point bottomLeft = new Point(-((0.5)* height),-(0.5)*width);
        Point topLeft = new Point(-(0.5)* height,(0.5)*width);
        
        var vertices = new ArrayList<Point>();
        vertices.add(topRight);
        vertices.add(bottomRight);
        vertices.add(bottomLeft);
        vertices.add(topLeft);

        return vertices;
    }

    public static ArrayList<Point> rotateAroundOrigin(ArrayList<Point> vertices, RotatedRect rect) {
        
        for(Point p: vertices){
            DecimalFormat df = new DecimalFormat("#, ###.##");
            String xPrime = df.format(p.x * Math.cos(Math.PI-rect.angle) - p.y * Math.sin(Math.PI-rect.angle));
            String yPrime = df.format(p.x * Math.sin(Math.PI-rect.angle) + p.y * Math.cos(Math.PI-rect.angle));

            p.x=Double.parseDouble(xPrime);
            p.y=Double.parseDouble(yPrime);
        }         
        return vertices;
    }
    public static ArrayList<Point> translate(ArrayList<Point> vertices, RotatedRect rect) {
        double i=0;
        for(Point p: vertices){
            
            double translationx = rect.center.x;
            double translationy= rect.center.y;

            double xPrime = p.x + translationx;
            double yPrime = p.y + translationy;
            
            if (i==0){
                xPrime = Math.abs(p.x) + translationx;
                yPrime = p.y + translationy;
            }
            else if (i==1){
                xPrime = Math.abs(p.x) + translationx;
                yPrime = p.y + translationy;
            }
            else if (i==2){
                xPrime = p.x + translationx;
                yPrime = Math.abs(p.y) + translationy;
            }
            else{
                xPrime = p.x + translationx;
                yPrime = p.y + translationy;
            }
            i+=1;

    
            p.x=xPrime;
            p.y=yPrime;
        }         
        return vertices;
    }*/

