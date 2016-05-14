import org.jblas.*;

public class Fan extends GameObject
{
    private DoubleMatrix points = RMath.PTDM(new double[][] {
        {0.25,8,0.25},
        {0.25,-8,0.25},
        {-0.25,8,0.25},
        {-0.25,-8,0.25},
        {0.25,8,-0.25},
        {0.25,-8,-0.25},
        {-0.25,8,-0.25},
        {-0.25,-8,-0.25},
        {0.25,0.25,8},
        {0.25,0.25,-8},
        {-0.25,0.25,8},
        {-0.25,0.25,-8},
        {0.25,-0.25,8},
        {0.25,-0.25,-8},
        {-0.25,-0.25,8},
        {-0.25,-0.25,-8}
    });
    //private DoubleMatrix center = new DoubleMatrix(new double[]{0,-8,0});
    
    private double speed = 3;
    
    public Fan()
    {
        mesh.points = points.dup();
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        mesh.points = RMath.rotX(speed * deltaTime).mmul(mesh.points);
    }
}