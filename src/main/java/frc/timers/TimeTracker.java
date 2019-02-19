package frc.timers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Keeps a running average of times from various operations
 * @author dcowden
 */
public class TimeTracker {
    
    public static int PERIOD=20;
    private Map<String,SampleStatistics> samples = new TreeMap<String,SampleStatistics>();
    private Map<String,StopWatch> timers = new HashMap<String,StopWatch>();
    private boolean enabled = true;
    
    public TimeTracker(){
        
    }
    public boolean isEnabled(){
        return enabled;
    }
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    public void recordTiming(String name, long value){
        if ( isEnabled() ){
            getOrCreateSample(name).add(value);
        }        
    }
    
    public void start(String name){
        if ( isEnabled() ){
            getOrCreateStopWatch(name);
        }        
    }
    
    public void end(String name){
        if ( isEnabled() ){
            long elapsed = getOrCreateStopWatch(name).elapsedMs();
            recordTiming(name,elapsed);
            timers.remove(name);
        }
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
        StringBuffer sb = new StringBuffer();
        sb.append("Timings [ms]:\n");
        if ( isEnabled() ){
            sb.append("------------------\n");
            sb.append("Key\t\tAvg\t\tLast\n");
            for ( Map.Entry<String,SampleStatistics>s: samples.entrySet()){
                SampleStatistics ss = s.getValue();
                sb.append(
                        String.format("%s\t\t%d\t\t%d\n",s.getKey(), ss.getAverage(), ss.getLatest() )
                        );

            }            
        }

        return sb.toString();
    }
}
