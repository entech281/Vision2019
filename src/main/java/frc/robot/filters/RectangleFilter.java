/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot.filters;

import java.util.ArrayList;
import org.opencv.core.RotatedRect;

/**
 *
 * @author dcowden
 */
public interface RectangleFilter {
    public ArrayList<RotatedRect> filter(ArrayList<RotatedRect> toFilter, int index);
}
