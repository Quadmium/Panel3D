package panel3d;

import java.awt.Color;
import org.jblas.*;
import java.util.List;
import java.util.ArrayList;

public class Mesh
{
    public DoubleMatrix points = null;
    public List<Integer> triangles = new ArrayList<Integer>();
    public List<Color> triColor = new ArrayList<Color>();
    
    public Mesh()
    {
    }
    
    public Mesh(double[][] points)
    {
        this.points = new DoubleMatrix(points).transpose();
    }
    
    public void setPoints(double[][] points)
    {
        this.points = new DoubleMatrix(points).transpose();
    }
}