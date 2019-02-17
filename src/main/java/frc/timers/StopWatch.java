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
public class StopWatch {
    public static final long NANOS_TO_MILLIS=1000000;
    private long created = System.nanoTime();
    
    public long elapsedMs(){
        long current = System.nanoTime();
        return (current-created) / NANOS_TO_MILLIS;
    }
}
