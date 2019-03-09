package frc.robot.filters;

import java.util.ArrayList;
import java.util.Collections;

import org.opencv.core.RotatedRect;

import frc.robot.RotatedRectangleComparator;

public class GetRidOfAloneRect {
    public ArrayList<RotatedRect> getRidofAloneRects(ArrayList<RotatedRect> toFilter){
        Collections.sort(toFilter, new RotatedRectangleComparator());
        int i = 0;
        int len = toFilter.size();
        boolean pairRect = false;

        var filtered = new ArrayList<RotatedRect>();
            while (i < (len - 1)) {
                RotatedRect rect1 = toFilter.get(i);
                RotatedRect rect2 = toFilter.get(i + 1);
                if(rect2.angle > -50 && rect2.angle < 0 || rect2.angle == 90){
                    pairRect = true;
                }
            

                if (rect1.angle < -70 && rect1.angle > -100 && pairRect) {
                    filtered.add(rect1);
                    filtered.add(rect2);
                    i = i + 2;
                } else {
                    i = i + 1;
                }
            }
return filtered;
}
}