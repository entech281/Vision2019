package frc.test;

import org.junit.Assert;
import org.junit.Test;
import frc.robot.filters.GibberishRectangleFilter;

public class TestGibberishRectangleFilter{

    @Test
    public void testIsRatioLargerThan(){
        double ar1 = 3;
        double ar2 = 1;
        double ar3 = 0.333;
        boolean isRatio1;
        boolean isRatio2;
        boolean isRatio3;

        isRatio1 = GibberishRectangleFilter.getAspectRatioConditionMet(ar1);
        isRatio2 = GibberishRectangleFilter.getAspectRatioConditionMet(ar2);
        isRatio3 = GibberishRectangleFilter.getAspectRatioConditionMet(ar3);

        Assert.assertEquals(true, isRatio1);
        Assert.assertEquals(false, isRatio2);
        Assert.assertEquals(true, isRatio3);

    }
}