/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot.filters;

import java.util.ArrayList;
import org.opencv.core.RotatedRect;

/**
 *
 * @author dcowden
 */
public class GibberishRectangleFilter{

    public static double MIN_DIMENSION = 2;
    public static double MIN_ASPECT_RATIO = 1.4;
    public static double MAX_ASPECT_RATIO = 8;    
    /*
    
    CAREFUL! the angles are twisting the rectangles sideways, so basically
    height and weidth can be inverted!
        - H33.33974075317383W=13.532013893127441
        - H13.457060813903809W=33.832725524902344
        - H3.0W=3.0
        - H33.33974075317383W=13.139781951904297
        - H13.435029029846191W=33.799705505371094
        - H3.535533905029297W=4.242640495300293
        - H33.298828125W=13.34122371673584
        - H13.457060813903809W=33.832725524902344
        - H2.0W=5.0
        - H33.33974075317383W=13.139781951904297

    
    */

    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter)  {
        double ar;
        var filteredFromSmall = new ArrayList<RotatedRect>();
        boolean aspectRatioConditionsMet = false;
        for (RotatedRect rect : toFilter) {
            ar = rect.size.height/rect.size.width;
            aspectRatioConditionsMet=getAspectRatioConditionMet(ar);

            if ( valuesLargerThan(MIN_DIMENSION,rect.size.height, rect.size.width) &&
                 aspectRatioConditionsMet){
                filteredFromSmall.add(rect);
            }
        }

        return filteredFromSmall;
    }
    public boolean valuesLargerThan(double limit, double ... values ){
        for ( double d: values){
            if ( d < limit){
                return false;
            }
        }
        return true;
    }
    public static boolean isRatioConditionMetWhenAspectLessThanOne(double ar, double minAspectRatio, double maxAspectRatio){
        return (ar > 1/maxAspectRatio && ar<1/minAspectRatio);

    }

    public static boolean isRatioConditionMetWhenAspectLargerThanOne(double ar, double minAspectRatio, double maxAspectRatio){
        return (ar<maxAspectRatio && ar>minAspectRatio);
    }

    public static boolean getAspectRatioConditionMet(double ar){
        boolean aspectRatioConditionMet;
        if(ar<1){
            aspectRatioConditionMet = isRatioConditionMetWhenAspectLessThanOne(ar, MIN_ASPECT_RATIO, MAX_ASPECT_RATIO);
        }
        else{
            aspectRatioConditionMet = isRatioConditionMetWhenAspectLargerThanOne(ar, MIN_ASPECT_RATIO, MAX_ASPECT_RATIO);
        }
        return aspectRatioConditionMet;
    }

}
