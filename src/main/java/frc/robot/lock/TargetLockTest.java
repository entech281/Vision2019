package frc.robot.lock;

import frc.robot.lock.TargetAlign;

public class TargetLockTest implements TargetAlign{

    boolean testTargetLock;
    public boolean isTargetLockOn(){
        return testTargetLock;
    }

    public void setTestTargetLock(boolean input){
        testTargetLock = input;
    }
}