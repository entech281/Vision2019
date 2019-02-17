package frc.robot;

import java.util.ArrayList;
import java.util.Comparator;

import org.opencv.core.RotatedRect;

public class RotatedRectangleComparator implements Comparator<RotatedRect> {

    @Override
    public int compare(RotatedRect arg0, RotatedRect arg1) {
        return new Double(arg0.center.x).compareTo(new Double(arg1.center.x));
    }
    
}