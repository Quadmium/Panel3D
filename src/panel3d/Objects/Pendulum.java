package panel3d.Objects;

import org.jblas.*;
import panel3d.GameObject;
import panel3d.RMath;

public class Pendulum extends GameObject
{
    private DoubleMatrix points = RMath.PTDM(new double[][] {
        {1,-1,1},
        {1,1,1},
        {-1,-1,1},
        {-1,1,1},
        {1,-1,-1},
        {1,1,-1},
        {-1,-1,-1},
        {-1,1,-1},
        {0.25,8,0.25},
        {0.25,1,0.25},
        {-0.25,8,0.25},
        {-0.25,1,0.25},
        {0.25,8,-0.25},
        {0.25,1,-0.25},
        {-0.25,8,-0.25},
        {-0.25,1,-0.25}
    });
    private DoubleMatrix center = new DoubleMatrix(new double[]{0,8,0});
    
    private double theta = 0;
    private double time = 0;
    private double period = 4;
    
    public Pendulum()
    {
        mesh.points = points;
        transform.pivot = center;
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
        time += deltaTime;
        if(time > period)
            time -= period;
            
        theta = Math.sin(2*Math.PI/period*time);
        transform.rotX = theta;
        transform.rotZ = time;
        //mesh.points = RMath.rotX(theta).mmul(points.subColumnVector(center)).addColumnVector(center);
    }
}