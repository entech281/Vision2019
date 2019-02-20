package frc.timers;

/**
 *
 * @author dcowden
 */
public class FramerateTracker {
    
    private long lastSampleTime = 0;
    private long lastTicks = 0;
    private long ticks = 0;
    
    public void frame(){
        ticks += 1;
    }
    
    private void reset(){
        lastTicks = ticks;
        lastSampleTime = System.currentTimeMillis();
    }
    public double getFrameRate(){
        long currentTime = System.currentTimeMillis();
        long elaspedTime = currentTime - lastSampleTime;
        long ticksElapsed = ticks - lastTicks;
        reset();
        return (double)ticksElapsed / (double)elaspedTime * 1000.0;
    }
    
    public String toString(){
        return String.format("Framerate: %.2f fps", getFrameRate());
    }

    public long getTicks(){
        return ticks;
    }
}
