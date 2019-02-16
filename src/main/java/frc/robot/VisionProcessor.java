
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
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Size;
import org.opencv.imgproc.*;
import java.util.*; 
import java.lang.*; 
import java.io.*; 

/**
 * This has a lot of the code that used to be inside of GripPipeline
 * @author dcowden
 */
public class VisionProcessor implements VisionPipeline{


    public static int MIN_Y = 50;
    public static int MAX_Y = 300;
    private DCGripPipeline parent;
    private double distanceFromTarget = 0.0;
    private double lateralDistance = 0.0;
    private Mat lastFrame = null;
    
    public VisionProcessor(DCGripPipeline parent ){
        this.parent = parent;
    }
    
    @Override
    public void process(Mat sourceFrame) {
        
    
        //The values of the output image
        int startRow = (CameraConstants.PROCESS_HEIGHT)/3;
        int endRow = (2*CameraConstants.PROCESS_HEIGHT)/3;
        
        //Rect rectCrop = new Rect(p1.x, p1.y , (p4.x-p1.x+1), (p4.y-p1.y+1));
        Mat resizedImage = sourceFrame.submat(startRow, endRow, 0, CameraConstants.PROCESS_WIDTH);
        System.out.println("Resized:" + resizedImage.dump());
        
        //Mat resizedImage = sourceFrame;        
        
        parent.process(resizedImage);

        System.out.println("FindContours " + parent.findContoursOutput().size() + "!!!");
        System.out.println("FilterContours " + parent.filterContoursOutput().size() + "controus!!!");
        //ArrayList<RotatedRect> targets = minimumBoundingRectangle(parent.filterContoursOutput());
        ArrayList<RotatedRect> targets = minimumBoundingRectangle(parent.findContoursOutput());
        System.out.println("Target Rects=" + targets.size());
        ArrayList<RotatedRect> nondumb = getRidOfDumbandAloneRectangles(targets);
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
        lastFrame = putFrameWithVisionTargets(resizedImage, nondumb, rvec, tvec);            
        
    }
    

    public Mat getLastFrame() {
        return lastFrame;
    }
    
    public double getDistanceFromTarget() {
        return distanceFromTarget;
    }
    public double getLateralDistance() {
        return lateralDistance;
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
        var filteredFromRandom = new ArrayList<RotatedRect>();
        var filteredFromSmall = new ArrayList<RotatedRect>();

        var filteredFinal = new ArrayList<RotatedRect>();

        for ( RotatedRect rect: input){
            if ( rect.boundingRect().y > MIN_Y && rect.boundingRect().y < MAX_Y){
                filteredFromRandom.add(rect);
            }

        }

        for(RotatedRect rect: filteredFromRandom){
            if(rect.angle!=0 && rect.size.height>5){
                filteredFromSmall.add(rect);
            }
        }

        Collections.sort(filteredFromSmall, new RotatedRectangleSortLeftToRight());
        int i = 0;
        int len = filteredFromSmall.size();
        
        System.out.println("filtered_from_small"+ len);

		while (i<(len-1)){
            RotatedRect rect1 = filteredFromSmall.get(i);
            RotatedRect rect2 = filteredFromSmall.get(i+1);
            System.out.println("RECT1: "+rect1.angle);
            System.out.println("RECT2: "+rect2.angle);
            if (rect1.angle<-75 && rect1.angle>-100 && rect2.angle>-50 && rect2.angle<0){
                filtered.add(rect1);
                filtered.add(rect2);
                i = i + 2;
            }
            else{
                i = i + 1;
            }
        }

        System.out.println("filtered"+ filtered.size());

        int n = 1;
        int lenFiltered = filtered.size();
        int indexMax = 0;
        if (lenFiltered>1){
        while (n<(lenFiltered-1)){
            RotatedRect rect1 = filtered.get(n);
            RotatedRect minRect = filtered.get(indexMax);
            double distanceToCenter1 = Math.abs(320 - rect1.center.x);
            double distanceToCenterMin = Math.abs(320 - minRect.center.x);
            if (distanceToCenter1<distanceToCenterMin){
                indexMax = n;
            }
            n = n+1;
        }



        if (indexMax==0){
            filteredFinal.add(filtered.get(indexMax));
            filteredFinal.add(filtered.get(indexMax+1)); 
        }
        else if (indexMax%2 == 0){
            filteredFinal.add(filtered.get(indexMax));
            filteredFinal.add(filtered.get(indexMax+1));
        }
        else{
            filteredFinal.add(filtered.get(indexMax-1));
            filteredFinal.add(filtered.get(indexMax)); 
        }
    }
    System.out.println("filtered_final"+filteredFinal.size());
        return filteredFinal;

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
            double [] distanceTarget = tvec.get(2, 0);
            double [] lateralDist = tvec.get(0, 0);
            distanceFromTarget = distanceTarget[0];
            lateralDistance = lateralDist[0];

            Imgproc.putText(img, df.format(distanceFromTarget), new Point(20, 10), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255), 2);

            return img;
    }        


    
}
