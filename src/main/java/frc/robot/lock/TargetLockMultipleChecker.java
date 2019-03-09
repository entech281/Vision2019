package frc.robot.lock;

public class TargetLockMultipleChecker implements TargetAlign{
    public TargetLockMultipleChecker(TargetAlign button, TargetAlign file){
        this.button = button;
        this.file = file;
    }
    TargetAlign button;
    TargetAlign file;

    public boolean isTargetLockOn(){
        if(button.isTargetLockOn() || file.isTargetLockOn()){
            return true;
        }
        else{
            return false;
        }
    }
}