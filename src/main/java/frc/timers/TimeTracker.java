package frc.timers;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a running average of times from various operations
 * @author dcowden
 */
public class TimeTracker {
    
    public static int PERIOD=20;
    private Map<String,SampleStatistics> samples = new HashMap<String,SampleStatistics>();
    private Map<String,StopWatch> timers = new HashMap<String,StopWatch>();
    
    public TimeTracker(){
        
    }
    
    public void recordTiming(String name, long value){
        getOrCreateSample(name).add(value);
    }
    
    public void start(String name){
        getOrCreateStopWatch(name);
    }
    
    public void end(String name){
        long elapsed = getOrCreateStopWatch(name).elapsedMs();
        recordTiming(name,elapsed);
        timers.remove(name);
    }
    
    public long getLatest(String name){
        return samples.get(name).getLatest();
    }
    public long getAverage(String name){
        return samples.get(name).getAverage();
    }
    
    private SampleStatistics getOrCreateSample(String name){
        SampleStatistics ma = samples.get(name);
        if ( ma == null){
            ma = new SampleStatistics(PERIOD);
            samples.put(name,ma);
        }
        return ma;
    }
    
    private StopWatch getOrCreateStopWatch(String name){
        StopWatch sw = timers.get(name);
        if ( sw == null){
            sw = new StopWatch();
            timers.put(name,sw);
        }
        return sw;        
    }
    
    @Override
    public String toString(){
        return String.format("Timings [ms]: " + samples);
    }
}
