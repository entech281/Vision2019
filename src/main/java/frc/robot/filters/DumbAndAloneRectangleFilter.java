/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot.filters;

import frc.robot.CameraConstants;
import frc.robot.RotatedRectangleComparator;
import frc.robot.VisionReporter;

import java.util.ArrayList;
import java.util.Collections;
import org.opencv.core.RotatedRect;

import edu.wpi.first.vision.VisionPipeline;

/**
 *
 * @author dcowden
 */
public class DumbAndAloneRectangleFilter implements RectangleFilter{

    int counter = 0;
    int numRectsPrev = 0;
    int numRects = 0;
    boolean targetLockInitiated = false;

    VisionReporter reporter = new VisionReporter();
    @Override
    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter) {
       //this gets rid of rectangles in the top of the image, which are usually lights
        counter += 1;
        if(counter == 1){
            numRectsPrev = toFilter.size();
        }
        numRects = toFilter.size();

        Collections.sort(toFilter, new RotatedRectangleComparator());
        int i = 0;
        int len = toFilter.size();
        boolean pairRect = false;

        var filtered = new ArrayList<RotatedRect>();
        var filteredFinal = new ArrayList<RotatedRect>();
        
        if(len==1){
            RotatedRect rect = toFilter.get(0);
            filteredFinal.add(rect);
        }
        else{
            while (i < (len - 1)) {
                RotatedRect rect1 = toFilter.get(i);
                RotatedRect rect2 = toFilter.get(i + 1);
                //System.out.println("RECT1: " + rect1.angle);
                //System.out.println("RECT2: " + rect2.angle);

            if((rect2.angle > -50 && rect2.angle < 0 || rect2.angle == 90)){
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

            //System.out.println("filtered"+ filtered.size());
            if(numRects == numRectsPrev && reporter.getTargetAlignButtonPressed() && counter > 1){
                numRectsPrev = numRects;
                targetLockInitiated = true;
            }
            else{
                int n = 1;
                int lenFiltered = filtered.size();
                int indexMax = 0;
                if (lenFiltered > 1) {
                    while (n < (lenFiltered - 1)) {
                        RotatedRect rect1 = filtered.get(n);
                        RotatedRect minRect = filtered.get(indexMax);
                        double distanceToCenter1 = Math.abs(CameraConstants.PROCESS_WIDTH / 2 - rect1.center.x);
                        double distanceToCenterMin = Math.abs(CameraConstants.PROCESS_WIDTH / 2 - minRect.center.x);
                            if (distanceToCenter1 < distanceToCenterMin) {
                                indexMax = n;
                            }
                    
                        n = n + 1;
                    }
                targetLockInitiated = false;
                }
                if (indexMax == 0) {
                    filteredFinal.add(filtered.get(indexMax));
                    filteredFinal.add(filtered.get(indexMax + 1));
                } else if (indexMax % 2 == 0) {
                    filteredFinal.add(filtered.get(indexMax));
                    filteredFinal.add(filtered.get(indexMax + 1));
                } else {
                    filteredFinal.add(filtered.get(indexMax - 1));
                    filteredFinal.add(filtered.get(indexMax));
                }
            }
        }
            //System.out.println("filtered_final"+filteredFinal.size());
            return filteredFinal;
        }
    
    public boolean getTargetLockInitiated(){
        return targetLockInitiated;
    }
    
}
