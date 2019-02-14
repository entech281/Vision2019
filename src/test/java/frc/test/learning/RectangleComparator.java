/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.test.learning;

import java.util.Comparator;

/**
 *
 * @author dcowden
 */
public class RectangleComparator implements Comparator<Rectangle>{

    @Override
    public int compare(Rectangle arg0, Rectangle arg1) {        
        return new Integer(arg0.x).compareTo(new Integer(arg1.x));
    }
    
}
