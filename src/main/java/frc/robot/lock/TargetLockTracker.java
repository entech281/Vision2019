package frc.robot.lock;


import frc.robot.lock.TargetAlign;

public class TargetLockTracker{
    

    public TargetLockTracker(TargetAlign targetAlignInput){
        this.targetAlignInput = targetAlignInput;
    }

    private TargetAlign targetAlignInput;
    private int selectedIndex = 0;
    private int expectedRect = 0;

    public boolean isTargetLockOn(){
        return selectedIndex != 0;
    }

    public int getSelectedIndex(){
        return selectedIndex;
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
        selectedIndex = 0;
        expectedRect = 0;
    }

}