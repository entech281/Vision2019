package frc.robot.filters;

import frc.robot.CameraConstants;
import frc.robot.RotatedRectangleComparator;
import java.util.ArrayList;
import java.util.Collections;
import org.opencv.core.RotatedRect;

/**
 *
 * @author dcowden
 */
public class FinalVisionTargetFilter implements RectangleFilter{

    @Override
    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter, int index) {
        Collections.sort(toFilter, new RotatedRectangleComparator());
        
        var visionTargets = new ArrayList<RotatedRect>();
        visionTargets.add(toFilter.get(index));
        visionTargets.add(toFilter.get(index + 1));

        return visionTargets;
        }
    
}