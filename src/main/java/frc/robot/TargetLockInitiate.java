package frc.robot;

import java.util.ArrayList;

import org.opencv.core.RotatedRect;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class TargetLockInitiate {
    
    private final NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private final NetworkTableEntry targetLockTable = ntist.getEntry("team281.targetLock.buttonPressed");

    private boolean targetLock;
    private boolean targetAlignButtonPressed;
    public boolean InitiateTargetLock(ArrayList<RotatedRect> toFilter, int prevNumRects, VisionReporter reporter){
        if(toFilter.size() == prevNumRects && getTargetAlignButtonPressed()){
            targetLock = true;
        }else{
            targetLock = false;
            prevNumRects = toFilter.size();
        }
        return targetLock; 
    }

public boolean getTargetAlignButtonPressed(){
        boolean targetManual = new TargetLockChecker().isTargetLockOn();
        targetAlignButtonPressed = (targetLockTable.getBoolean(false) || targetManual);
        return targetAlignButtonPressed;
    }

}