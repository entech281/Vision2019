package frc.test;

import org.junit.Assert;
import org.junit.Test;
import frc.robot.filters.GibberishRectangleFilter;

public class TestGibberishRectangleFilter{

    @Test
    public void testIsRatioLargerThan(){
        double RECTANGLE_HEIGHT = 10.0;
        double RECTANGLE_WIDTH = 4.0;
        boolean isRatioLarge1;
        boolean isRatioLarge2;
        boolean isRatioLarge3;

        isRatioLarge1=GibberishRectangleFilter.isRatioLargerThan(RECTANGLE_WIDTH, RECTANGLE_HEIGHT, 2);
        isRatioLarge2=GibberishRectangleFilter.isRatioLargerThan(RECTANGLE_HEIGHT, RECTANGLE_WIDTH, 2);
        isRatioLarge3=GibberishRectangleFilter.isRatioLargerThan(RECTANGLE_HEIGHT, RECTANGLE_HEIGHT, 2);

        Assert.assertEquals(true, isRatioLarge1);
        Assert.assertEquals(true, isRatioLarge2);
        Assert.assertEquals(false, isRatioLarge3);
    }
}