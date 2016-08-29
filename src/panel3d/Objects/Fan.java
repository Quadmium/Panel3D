package panel3d.Objects;

import org.jblas.*;
import panel3d.GameObject;
import panel3d.RMath;

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
        mesh.points = points;
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
        transform.rotX += speed * deltaTime;
        //mesh.points = RMath.rotX(speed * deltaTime).mmul(mesh.points);
    }
}