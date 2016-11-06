package panel3d;

import java.awt.Color;
import org.jblas.*;

public class Triangle
{
    public DoubleMatrix points;
    public Color color;
    public double[] data;
    
    public Triangle(DoubleMatrix triangles, Color color, double[] data)
    {
        this.points = triangles;
        this.color = color;
        this.data = data;
    }
}