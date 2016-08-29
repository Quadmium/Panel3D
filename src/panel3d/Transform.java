package panel3d;

import org.jblas.*;

public class Transform
{
    public DoubleMatrix position;
    public DoubleMatrix rotation;
    public double rotX, rotY, rotZ, lastRotX, lastRotY, lastRotZ;
    public DoubleMatrix pivot;
    
    public Transform()
    {
        position = new DoubleMatrix(new double[] {0, 0, 0});
        rotation = new DoubleMatrix(new double[] {0, 0, 0});
        pivot = new DoubleMatrix(new double[] {0, 0, 0});
    }
    
    public Transform(double[] position)
    {
        this.position = new DoubleMatrix(position);
    }
    
    public void setPosition(double[] position)
    {
        this.position = new DoubleMatrix(position);
    }
    
    public void updateRotation()
    {
        if(lastRotX != rotX || lastRotY != rotY || lastRotZ != rotZ)
        {
            rotation = RMath.rotX(rotX).mmul(
                       RMath.rotY(rotY).mmul(
                       RMath.rotZ(rotZ)));
            lastRotX = rotX;
            lastRotY = rotY;
            lastRotZ = rotZ;
        }
    }
}