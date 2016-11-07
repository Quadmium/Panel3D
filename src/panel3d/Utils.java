/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel3d;

/**
 *
 * @author Quadmium
 */
public class Utils
{
    public static double clamp(double num, double min, double max)
    {
        return Math.max(Math.min(num,max),min);
    }
}
