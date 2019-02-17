/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.timers;

/**
 *
 * @author dcowden
 */
import java.util.ArrayDeque;
import java.util.Queue;

public class SampleStatistics {

    private final Queue<Long> window = new ArrayDeque<Long>();
    private final int period;
    private long totalSamples = 0;
    private long sum = 0L;
    private long lastValue = 0L;

    public SampleStatistics(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public void add(long num) {
        
        lastValue = num;
        totalSamples++;
        sum = sum + num;
        window.add(num);
        
        if (window.size() > period) {
            long t = window.remove();
            //System.out.println("removing" + t);
            sum = sum - t;
        }
        //System.out.println("added:" + num + ", sum=" + sum + ",avg=" + getAverage() );
        //System.out.println("" + window);
    }

    public long getTotalSamples() {
        return totalSamples;
    }
    
    public long getLatest(){
        return lastValue;
    }
    public long getAverage() {
        if (window.isEmpty()) return 0L; // technically the average is undefined
        return sum / window.size();
    }
    
    @Override
    public String toString(){
        return String.format("(Last:%d, Avg:%d, Count: %d)", lastValue, getAverage(),getTotalSamples() );
    }
}
