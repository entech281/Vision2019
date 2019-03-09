package frc.test;

import org.junit.Assert;
import org.junit.Test;
import frc.robot.filters.GibberishRectangleFilter;

import frc.robot.lock.*;
public class TestTargetLockTracker{

    @Test
    public void testIsTargetLockOnNumRectsChange(){
        TargetLockTest targetInput = new TargetLockTest();
        TargetLockTracker lockTracker = new TargetLockTracker(targetInput);
        boolean targetLock;

        int expectedNumRects = 3;
        int selectedIndex = 2;
        lockTracker.setupLock(expectedNumRects, selectedIndex);

        targetInput.setTestTargetLock(true);
        int numRects = 4;
        
        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);
    }

    @Test
    public void testIsTargetLockOnTargetLockInitiates(){
        TargetLockTest targetInput = new TargetLockTest();
        TargetLockTracker lockTracker = new TargetLockTracker(targetInput);
        boolean targetLock;

        int expectedNumRects = 3;
        int selectedIndex = 2;
        lockTracker.setupLock(expectedNumRects, selectedIndex);

        targetInput.setTestTargetLock(true);
        int numRects = 3;

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(true, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(true, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(true, targetLock);
    }

    @Test
    public void testIsTargetLockOnButtonNotPushed(){
        TargetLockTest targetInput = new TargetLockTest();
        TargetLockTracker lockTracker = new TargetLockTracker(targetInput);
        boolean targetLock;

        int expectedNumRects = 3;
        int selectedIndex = 2;
        lockTracker.setupLock(expectedNumRects, selectedIndex);

        targetInput.setTestTargetLock(false);
        int numRects = 3;

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);

        lockTracker.checkLock(numRects);
        targetLock = lockTracker.isTargetLockOn();
        Assert.assertEquals(false, targetLock);
    }
}