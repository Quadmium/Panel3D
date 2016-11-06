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
        int subDiv = 10;
        int triIndex = 0;
        for(double p = 0; p <= Math.PI + 0.001; p += Math.PI / subDiv)
        {
            for(double t = 0; t < 2 * Math.PI + 0.001; t += 2 * Math.PI / subDiv)
            {
                if((p==0 || Math.PI - p < 0.0001) && t != 0) continue;
                DoubleMatrix addition = RMath.PTDM(new double[][] {{Math.cos(t)*Math.sin(p), Math.sin(t)*Math.sin(p), Math.cos(p)}});
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
            int a = (int) (90 * DoubleMatrix.ones(3).distance2(mesh.points.getColumn(mesh.triangles.get(tri)).add(
                                                               mesh.points.getColumn(mesh.triangles.get(tri+1)).add(
                                                               mesh.points.getColumn(mesh.triangles.get(tri+2)))).div(3)));
            mesh.triColor.add(new Color(a,a,a));
        }
        
        transform.rotX = Math.PI / 180 * -90;
        drawMode = 1;
        
        rigidbody = new Rigidbody(this);
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
        
        //rigidbody.OnFixedUpdate(deltaTime);
    }
}