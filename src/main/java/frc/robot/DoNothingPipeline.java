/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot;

import edu.wpi.first.vision.VisionPipeline;
import org.opencv.core.Mat;

/**
 *
 * @author dcowden
 */
public class DoNothingPipeline implements VisionPipeline{
    private Mat output = new Mat();
    @Override
    public void process(Mat arg0) {
        output = arg0;
    }
    
    public Mat getOutput(){
        return output;
    }
}
