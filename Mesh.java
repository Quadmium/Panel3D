import org.jblas.*;

public class Mesh
{
    public DoubleMatrix points = new DoubleMatrix(new double[][]{{}});
    
    public Mesh()
    {
        this.points = DoubleMatrix.zeros(0);
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