import org.jblas.*;

public class Transform
{
    public DoubleMatrix position;
    public DoubleMatrix rotation;
    
    public Transform()
    {
        position = new DoubleMatrix(new double[] {0, 0, 0, 0});
        rotation = new DoubleMatrix(new double[] {0, 0, 0});
    }
    
    public Transform(double[] position)
    {
        this.position = new DoubleMatrix(position);
    }
    
    public void setPosition(double[] position)
    {
        this.position = new DoubleMatrix(position);
    }
}