package frc.robot.lock;


import frc.robot.lock.TargetAlign;

public class TargetLockTracker{
    

    public TargetLockTracker(TargetAlign targetAlignInput){
        this.targetAlignInput = targetAlignInput;
    }

    private TargetAlign targetAlignInput;
    private int selectedIndex = 0;
    private int expectedRect = 0;
    private boolean targetLockActive = false;

    public boolean isTargetLockOn(){
        return expectedRect > 1;
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }

    public int getExpectRect(){
        return expectedRect;
    }

    public void setupLock(int numRects, int selectedIndex){
        this.selectedIndex = selectedIndex;
        this.expectedRect = numRects;
    }

    public void checkLock(int numRects){
        if(expectedRect != numRects || targetAlignInput.isTargetLockOn() == false){
            resetLock();
        }
    }
    public void resetLock(){
        targetLockActive = false;
        expectedRect = 0;
    }

    public boolean targetInput(){
        return targetAlignInput.isTargetLockOn();
    }

    public boolean isTargetLockActivated(){
        return targetLockActive;
    }

}