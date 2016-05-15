import org.jblas.*;
import java.util.List;
import java.util.ArrayList;

public class Mesh
{
    public DoubleMatrix points = new DoubleMatrix(new double[][]{{}});
    public List<Integer> triangles = new ArrayList<Integer>();
    
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