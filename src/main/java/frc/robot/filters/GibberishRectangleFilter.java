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
public class GibberishRectangleFilter implements RectangleFilter{

    public static double MIN_DIMENSION = 2;
    public static double ASPECT_RATIO = 2;    
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
    @Override
    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter) {

        var filteredFromSmall = new ArrayList<RotatedRect>();
        for (RotatedRect rect : toFilter) {
            if ( valuesLargerThan(MIN_DIMENSION,rect.size.height, rect.size.width) &&
                 isRatioLargerThan(rect.size.height, rect.size.width, ASPECT_RATIO)){
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
    public boolean isRatioLargerThan(double val1, double val2, double aspectRatio){
        double ar = val1 / val2;
        double invAR = 1.0/ar;
        return (ar > aspectRatio) || ((1.0/ar) > (1.0/aspectRatio)) ;

    }
}
