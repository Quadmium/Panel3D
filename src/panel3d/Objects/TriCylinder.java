package panel3d.Objects;

import java.awt.Color;
import org.jblas.*;
import panel3d.*;

public class TriCylinder extends GameObject
{
    private double speed = 3;
    public Rigidbody rigidbody;
    
    public TriCylinder()
    {
        this(1, 1, 8, 0, 0, 0);
    }
    
    public TriCylinder(double radius, double height, int subDiv, double r, double g, double b)
    {
        int numInCircle = 0;
        for(int z = 0; z <= height; z += height)
        {
            for(double t = 0; t < 2 * Math.PI + 0.001; t += 2 * Math.PI / subDiv)
            {
                DoubleMatrix addition = RMath.PTDM(new double[][] {{radius*Math.cos(t), radius*Math.sin(t), z}});
                if(mesh.points == null)
                    mesh.points = addition;
                else
                    mesh.points = DoubleMatrix.concatHorizontally(mesh.points, addition);
                
                if(z==0)
                    numInCircle++;
            }
        }
        
        DoubleMatrix addition = RMath.PTDM(new double[][] {{0,0,0}});
        mesh.points = DoubleMatrix.concatHorizontally(mesh.points, addition);
        addition = RMath.PTDM(new double[][] {{0,0,height}});
        mesh.points = DoubleMatrix.concatHorizontally(mesh.points, addition);
                
        int bottom = mesh.points.columns - 2;
        int top = mesh.points.columns - 1;
        for(int i=0; i<numInCircle-1; i++)
        {
            // i-> i+n -> i+n+1
            mesh.triangles.add(i);
            mesh.triangles.add(i+numInCircle);
            mesh.triangles.add(i+numInCircle+1);
            // i-> i+n+1 -> i+1
            mesh.triangles.add(i);
            mesh.triangles.add(i+numInCircle+1);
            mesh.triangles.add(i+1);
            // i-> b -> i+1
            mesh.triangles.add(i);
            mesh.triangles.add(bottom);
            mesh.triangles.add(i+1);
            // i-> t -> i+1
            mesh.triangles.add(i+numInCircle);
            mesh.triangles.add(top);
            mesh.triangles.add(i+numInCircle+1);
        }
        
        for(int tri=0; tri < mesh.triangles.size(); tri += 3)
        {
            double a = (90 / Math.pow(height + radius, 0.7) * RMath.PTDM(new double[][]{{1.1*radius, 1.1*radius, 1.1*height}}).distance2(mesh.points.getColumn(mesh.triangles.get(tri)).add(
                                                               mesh.points.getColumn(mesh.triangles.get(tri+1)).add(
                                                               mesh.points.getColumn(mesh.triangles.get(tri+2)))).div(3)));
            mesh.triColor.add(new Color((int)RMath.clamp(a + r, 0, 255),
                                        (int)RMath.clamp(a + g, 0, 255),
                                        (int)RMath.clamp(a + b, 0, 255)));
        }
        
        transform.rotX = Math.PI / 180 * -90;
        drawMode = 1;
        
        rigidbody = new Rigidbody(this);
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
    }
}