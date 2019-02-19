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
public class PeriodicReporter {
    
    private long reportingIntervalMillis = 0;
    private long lastReportTime = 0;

    public PeriodicReporter(long reportingIntervalMillis){
        this.reportingIntervalMillis = reportingIntervalMillis;
    }
    
    public void reportIfNeeded(Object ... obj){
        long currentTime = System.currentTimeMillis();
        if ( (currentTime - lastReportTime) > reportingIntervalMillis){
            for ( Object o: obj){
                System.out.println(o + "");
            }            
            lastReportTime = currentTime;
        }
    }
}
