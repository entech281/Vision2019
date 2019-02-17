package frc.robot;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType.*;




public class ResizeCamera {
    Mat input = new Mat();
    Mat output = new Mat();
    Size outputSize = new Size();

    public ResizeCamera( Mat src, Size dsize){
        this.input = src;
        this.outputSize = dsize;

    }
    
    public Mat getResizedImage(){
        Imgproc.resize(input, output, outputSize, 0.0, 0.0, Imgproc.INTER_NEAREST);
        return output;
    }
}