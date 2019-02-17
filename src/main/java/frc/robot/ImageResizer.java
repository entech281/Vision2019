package frc.robot;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageResizer {

    public Mat resizeImage(Mat src, Size outputSize) {
        Mat output = new Mat();
        Imgproc.resize(src, output, outputSize, 0.0, 0.0, Imgproc.INTER_NEAREST);
        return output;
    }
}
