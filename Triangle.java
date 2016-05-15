import org.jblas.*;

public class Triangle
{
    public DoubleMatrix triangles;
    public double[] data;
    
    public Triangle(DoubleMatrix triangles, double[] data)
    {
        this.triangles = triangles;
        this.data = data;
    }
}