package frc.robot;

public class Timer{

    private int seconds = 0;

    public Timer(int seconds){
        this.seconds = seconds;
    }

    private long lastTimeStamp = 0;
    private int numberFrames = 0;
    private int numberFramesSinceLastUpdate = 0;
    private double frameRate = 0.0;
    private long getDelayIntervalInMillis(){
        return seconds*1000;
    }

    public boolean shouldWrite(){
        numberFrames += 1;
        numberFramesSinceLastUpdate += 1;
        long currentStamp = System.currentTimeMillis();
        boolean shouldWrite = false;
        
        if(currentStamp - lastTimeStamp > getDelayIntervalInMillis()){
            frameRate = numberFramesSinceLastUpdate / this.seconds;
            numberFramesSinceLastUpdate = 0;
            lastTimeStamp = currentStamp;
            shouldWrite = true;
        }
        return shouldWrite;
    }
    public long getFrameCount(){
        return numberFrames;
    }
    public double getFrameRate(){
        return frameRate;
    }
}