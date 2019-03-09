package frc.robot.lock;

import java.io.File;
import frc.robot.lock.TargetAlign;

public class TargetLockFileChecker implements TargetAlign{

    public boolean isTargetLockOn(){
        File targetLockfile = new File("/tmp/targetLock");
        return targetLockfile.exists();
    }
}