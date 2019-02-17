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

    @Override
    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter) {
        var filteredFromRandom = new ArrayList<RotatedRect>();
        var filteredFromSmall = new ArrayList<RotatedRect>();

        double MIN_WIDTH = 25;
        double MIN_HEIGHT = 15;
        double ASPECT_RATIO = 2;
        double aspectRatioRect = 0;

        for (RotatedRect rect : toFilter) {
            /*if ( rect.boundingRect().y > MIN_Y && rect.boundingRect().y < MAX_Y){
                filteredFromRandom.add(rect);
            }*/

            aspectRatioRect = rect.size.height / rect.size.width;
            if (rect.size.height > MIN_HEIGHT && rect.size.width > MIN_WIDTH && aspectRatioRect >= ASPECT_RATIO) {
                filteredFromRandom.add(rect);
            }

        }

        //System.out.println("FilteredFromRandom" + filteredFromRandom.size());
        for (RotatedRect rect : filteredFromRandom) {
            if (rect.angle != 0) {
                filteredFromSmall.add(rect);
            }
        }

        return filteredFromSmall;
    }
    
}
