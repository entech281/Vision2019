
package frc.robot;

import edu.wpi.first.vision.VisionPipeline;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Size;
import org.opencv.imgproc.*;

/**
 * This has a lot of the code that used to be inside of GripPipeline
 * @author dcowden
 */
public class VisionProcessor implements VisionPipeline{
  
    public static int MIN_Y = 160;
    public static int MAX_Y = 600;
    private DCGripPipeline parent;
    private double distanceFromTarget = 0.0;
    private Mat lastFrame = null;
    
    public VisionProcessor(DCGripPipeline parent ){
        this.parent = parent;
    }
    
    @Override
    public void process(Mat sourceFrame) {
        
        
        Mat resizedImage = sourceFrame;
        Size sz = new Size(CameraConstants.PROCESS_WIDTH,CameraConstants.PROCESS_HEIGHT);
        Imgproc.resize( sourceFrame, resizedImage, sz );        
        
        parent.process(resizedImage);

        System.out.println("FindContours " + parent.findContoursOutput().size() + "!!!");
        System.out.println("FilterContours " + parent.filterContoursOutput().size() + "controus!!!");
        ArrayList<RotatedRect> targets = minimumBoundingRectangle(parent.filterContoursOutput());
        System.out.println("Target Rects=" + targets.size());
        ArrayList<RotatedRect> nondumb = getRidOfDumbRectangles(targets);
        //ArrayList<RotatedRect> nondumb = targets;
        System.out.println("NonDumb Rects= " + nondumb.size());

        //SolvePnp Implementation
        Mat rvec = CameraConstants.getRvec();
        Mat tvec = CameraConstants.getTvec();

        if (nondumb.size()==2){
            Calib3d.solvePnP(CameraConstants.getObjectPoints(), 
                    CameraConstants.getImgPoint(nondumb), 
                    CameraConstants.getCameraMatrix(),
                    CameraConstants.getDistCoeffs(), rvec, tvec, true);
            
        }
        lastFrame = putFrameWithVisionTargets(resizedImage, targets, rvec, tvec);            
        
    }
    

    public Mat getLastFrame() {
        return lastFrame;
    }
    
    public double getDistanceFromTarget() {
        return distanceFromTarget;
    }
    

    public  static ArrayList<RotatedRect> getRidOfDumbRectangles(ArrayList<RotatedRect> input){
        var filtered = new ArrayList<RotatedRect>();


        for ( RotatedRect rect: input){
            if ( rect.boundingRect().y > MIN_Y && rect.boundingRect().y < MAX_Y){
                filtered.add(rect);
            }

        }        
        return filtered;
    }
    public  static ArrayList<RotatedRect> getRidOfDumbandAloneRectangles(ArrayList<RotatedRect> input){

        //this gets rid of rectangles in the top of the image, which are usually lights
        var filtered = new ArrayList<RotatedRect>();
        for ( RotatedRect rect: input){
            if ( rect.boundingRect().y > MIN_Y && rect.boundingRect().y < MAX_Y){
                filtered.add(rect);
            }
        }
        return filtered;
    }
    
    public ArrayList<RotatedRect> minimumBoundingRectangle(List<MatOfPoint> inputContours){
        //System.out.println(inputContours.size() + " inputContours");
                
        var visionTarget = new ArrayList<RotatedRect>();

        for (MatOfPoint contour: inputContours){
                visionTarget.add(Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray())));
        }
        return visionTarget;

    }
    
    public Mat putFrameWithVisionTargets( Mat img, List<RotatedRect> l, Mat rvec, Mat tvec){
            Point points[] = new Point[4];
            var centers = new ArrayList<Point>();

            for(RotatedRect r:l){
                    centers.add(r.center);
                    r.points(points);
                    for( int i = 0; i<4; i++){
                            Imgproc.line(img, points[i], points[(i+1)%4], new Scalar(255, 0, 0), 5);
                    }

            }
            DecimalFormat df = new DecimalFormat("#, ###.##");
            if(centers.size()==2){
                    Imgproc.line(img, centers.get(0), centers.get(1), new Scalar(0, 255, 0), 6);
                    Point midpoint = new Point(100,200);
                    String distance = df.format(Math.sqrt((centers.get(0).x-centers.get(1).x)*(centers.get(0).x-centers.get(1).x) +(centers.get(0).y-centers.get(1).y)*(centers.get(0).y-centers.get(1).y)));
                    Imgproc.putText(img, distance, midpoint, Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255), 2);
            }
            double [] distance_from_target = tvec.get(2, 0);
            distanceFromTarget = distance_from_target[0];

            Imgproc.putText(img, df.format(distanceFromTarget), new Point(20, 10), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255), 2);

            return img;
    }        


    
}
