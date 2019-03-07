/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.TargetLockChecker;

/**
 *
 * @author dcowden
 */
public class VisionReporter {

    private final NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private final NetworkTableEntry distance = ntist.getEntry("team281.Vision.distance");
    private final NetworkTableEntry lateral = ntist.getEntry("team281.Vision.lateral");
    private final NetworkTableEntry frameCount = ntist.getEntry("team281.frameCount");
    private final NetworkTableEntry foundTarget = ntist.getEntry("team281.Vision.foundTarget");


    boolean targetAlignButtonPressed = false;

    public void reportDistance(double distanceToTarget, double lateralDistance, long count, boolean foundTargetBoolean) {
        distance.forceSetDouble(distanceToTarget);
        lateral.forceSetDouble(lateralDistance);
        frameCount.forceSetDouble(count);
        foundTarget.forceSetBoolean(foundTargetBoolean);
    }

    
}
