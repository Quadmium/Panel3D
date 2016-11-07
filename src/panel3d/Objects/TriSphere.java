package panel3d.Objects;

import java.awt.Color;
import org.jblas.*;
import panel3d.*;

public class TriSphere extends GameObject
{
    private double speed = 3;
    public Rigidbody rigidbody;
    
    public TriSphere()
    {
        this(1, 8, 0, 0, 0);
    }
    
    public TriSphere(double radius, int subDiv, double r, double g, double b)
    {
        int triIndex = 0;
        for(double p = 0; p <= Math.PI + 0.001; p += Math.PI / subDiv)
        {
            for(double t = 0; t < 2 * Math.PI + 0.001; t += 2 * Math.PI / subDiv)
            {
                if((p==0 || Math.PI - p < 0.0001) && t != 0) continue;
                DoubleMatrix addition = RMath.PTDM(new double[][] {{radius*Math.cos(t)*Math.sin(p), radius*Math.sin(t)*Math.sin(p), radius*Math.cos(p)}});
                if(mesh.points == null)
                    mesh.points = addition;
                else
                    mesh.points = DoubleMatrix.concatHorizontally(mesh.points, addition);
                
                if(triIndex > 0 && triIndex < subDiv * subDiv - subDiv - 1)
                {
                    mesh.triangles.add(triIndex);
                    mesh.triangles.add(triIndex + subDiv);
                    mesh.triangles.add(triIndex + subDiv + 1);
                    mesh.triangles.add(triIndex);
                    mesh.triangles.add(triIndex + 1);
                    mesh.triangles.add(triIndex + subDiv + 1);
                }
                
                triIndex++;
            }
        }
        
        for(int tri=0; tri < mesh.triangles.size(); tri += 3)
        {
            double a = (90 / Math.pow(radius, 0.7) * RMath.PTDM(new double[][]{{1, -1, -1}}).distance2(mesh.points.getColumn(mesh.triangles.get(tri)).add(
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