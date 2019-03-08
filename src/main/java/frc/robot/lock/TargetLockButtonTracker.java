package frc.robot.lock;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.lock.TargetAlign;

public class TargetLockButtonTracker implements TargetAlign{
    
    private final NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private final NetworkTableEntry targetLockTable = ntist.getEntry("team281.targetLock.buttonPressed");
    private boolean targetAlignButtonPressed;


public boolean isTargetLockOn(){
        targetAlignButtonPressed = (targetLockTable.getBoolean(false));
        return targetAlignButtonPressed;
    }



}