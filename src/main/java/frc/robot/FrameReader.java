/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot;

import edu.wpi.cscore.CvSink;
import frc.timers.TimeTracker;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 *
 * @author dcowden
 */
public class FrameReader extends Thread{

    public FrameReader(ImageResizer imageResizer,CvSink sink,TimeTracker timer ){
        this.imageResizer = imageResizer;
        this.sink = sink;
        this.timer = timer;
    }
    
    private CvSink sink;
    private ImageResizer imageResizer;
    private long lastFrameNumberChecked = 0;
    private long currentFrameNumber = 0;
    private Mat currentFrame = new Mat();
    private TimeTracker timer;
    
    private interface TIMERS {

        String RESIZE = "fr:resize";
        String GRAB = "fr:grab";
    }
    
    public synchronized Mat getCurrentFrame(){
        return currentFrame;
    }
    
    public synchronized boolean hasNewFrame(){
        if (currentFrameNumber > lastFrameNumberChecked){
            lastFrameNumberChecked = currentFrameNumber;
            return true;
        }
        return false;
    }
    
    @Override
    public void run(){
        Mat raw = new Mat();
        while(true){
                
                timer.start(TIMERS.GRAB);
                sink.grabFrame(raw);
                currentFrameNumber++;
                timer.end(TIMERS.GRAB);
                
                //do not process empty images
                if (raw.size().height <= 0 || raw.size().width <= 0) {
                    continue;
                }

              
                synchronized(this){
                    timer.start(TIMERS.RESIZE);
                    currentFrame = imageResizer.resizeImage(raw,
                            new Size(
                                    CameraConstants.PROCESS_WIDTH,
                                    CameraConstants.PROCESS_HEIGHT)
                    );
                    timer.end(TIMERS.RESIZE);               
                }
         
        }
    }
    
}
