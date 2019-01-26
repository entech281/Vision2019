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
        firstpoints = getPointsFromRect(first);
        secondpoints = getPointsFromRect(second);
        if (isFirstLeftMost(first, second)){
            allpoints.addAll(firstpoints);
            allpoints.addAll(secondpoints);
        }
        else{
            allpoints.addAll(secondpoints);
            allpoints.addAll(firstpoints);
        }
        return allpoints;
    }
    public ArrayList <Point> getPointsFromRect(RotatedRect first){
        var arraypoints = new ArrayList<Point>();
        double translationTop = (first.size.width/2)/(Math.sin(first.angle));
        double translationHorizontal= first.size.height*Math.cos(90-first.angle);
        double translationVertical = first.size.width/2-first.size.height*Math.sin(90-first.angle);
        double translationBottom = -(first.size.width/2)/(Math.sin(first.angle));
        //Starts at top point and goes clockwise
        arraypoints.add(new Point(first.center.x,first.center.y + translationTop));
        arraypoints.add(new Point(first.center.x + translationHorizontal,first.center.y + translationVertical));
        arraypoints.add(new Point(first.center.x,first.center.y + translationBottom));
        arraypoints.add(new Point(first.center.x-translationHorizontal,first.center.y - translationVertical));
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