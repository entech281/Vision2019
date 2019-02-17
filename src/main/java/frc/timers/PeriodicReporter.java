/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.timers;

import java.io.OutputStream;
import java.util.stream.Stream;

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
    
    public void reportIfNeeded(Object obj){
        long currentTime = System.currentTimeMillis();
        if ( (currentTime - lastReportTime) > reportingIntervalMillis){
            System.out.println(obj + "");
            lastReportTime = currentTime;
        }
    }
}
