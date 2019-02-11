/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 *
 * @author dcowden
 */
public class VisionReporter {
	private NetworkTableInstance ntist = NetworkTableInstance.getDefault();
	private NetworkTableEntry vision = ntist.getEntry("team281.Vision"); 
        
        
        public void reportDistance(double distanceToTarget){
            vision.forceSetDouble(distanceToTarget);
        }
}
