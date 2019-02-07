package frc.robot;



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


    
