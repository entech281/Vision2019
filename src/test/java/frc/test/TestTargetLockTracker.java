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

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(false, targetLock);

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(false, targetLock);

        targetLock = lockTracker.isTargetLockOn(numRects);
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

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        targetInput.setTestTargetLock(false);
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


        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(false, targetLock);

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(false, targetLock);


        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(false, targetLock);

        targetInput.setTestTargetLock(false);
    }

    @Test
    public void testSimilarToRobotSituation(){
        TargetLockTest targetInput = new TargetLockTest();
        TargetLockTracker lockTracker = new TargetLockTracker(targetInput);
        boolean targetLock;

        int expectedNumRects = 6;
        int selectedIndex = 2;
        lockTracker.setupLock(expectedNumRects, selectedIndex);

        targetInput.setTestTargetLock(true);
        int numRects = 6;

        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        lockTracker.setupLock(expectedNumRects, selectedIndex);
        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        lockTracker.setupLock(expectedNumRects, selectedIndex);
        targetLock = lockTracker.isTargetLockOn(numRects);
        Assert.assertEquals(true, targetLock);

        targetInput.setTestTargetLock(false);
    }
}