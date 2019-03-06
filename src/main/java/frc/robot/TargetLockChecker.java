package frc.robot;

import java.io.File;

public class TargetLockChecker {

    public boolean isTargetLockOn(){
        File targetLockfile = new File("tmp/targetLock");
        return targetLockfile.exists();
    }
}