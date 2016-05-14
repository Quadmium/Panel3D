import java.awt.*;
import org.jblas.*;

public class Camera extends GameObject
{
    public double ar = 1;
    public double fov = 70 * Math.PI / 180;
    public double near = 0.01;
    public double far = 300;
    public double rotY = 0;
    public double rotX = 0;
    public double offset = 400;
    public double scale = 400;
    public Canvas parent;
    
    public Camera(Canvas parent){
        this.parent = parent;
    }
    
    public Camera(double ar, double fov, double near, double far, Canvas parent)
    {
        this.ar = ar;
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.parent = parent;
    }
    
    public void drawScreen(Graphics g, World world)
    {
        DoubleMatrix perspectiveProj = perspective(ar, fov, near, far); 
        DoubleMatrix rotMat = RMath.rotX(rotX).mmul(RMath.rotY(rotY));
        g.fillRect(0, 0, parent.getSize().width, parent.getSize().height);
        g.setColor(Color.BLACK);
        for(GameObject obj : world.objects)
        {
            DoubleMatrix points = obj.mesh.points.dup().addColumnVector(obj.transform.position);
            
            for(int i=0; i<points.rows; i++)
                for(int j=0; j<points.columns; j++)
                {
                    points.put(i,j,points.get(i,j)-transform.position.get(i,0));
                }
            points = rotMat.mmul(points);
            points = DoubleMatrix.concatVertically(points, DoubleMatrix.ones(1, points.columns));
            points = perspectiveProj.mmul(points); 
            for(int i=0; i<points.rows-1; i++)
                for(int j=0; j<points.columns; j++)
                {
                    points.put(i,j,points.get(i,j)/points.get(3,j));
                }
                
            for(int i=0; i<points.columns; i++)
            {
                for(int j=i+1; j<points.columns; j++)
                {
                    DoubleMatrix v1 = points.getColumn(i);
                    DoubleMatrix v2 = points.getColumn(j);
                    
                    if(Math.abs(v1.get(2)) > 1  || Math.abs(v2.get(2)) > 1) continue;                   
                    g.drawLine((int)(offset + scale * v1.get(0)), (int)(offset + scale * v1.get(1)), 
                               (int)(offset + scale * v2.get(0)), (int)(offset + scale * v2.get(1)));
                }
            }
        }
    }
    
    public DoubleMatrix perspective(double ar, double fov, double near, double far)
    {
        return new DoubleMatrix(new double[][] {
           {1/(ar * Math.tan(fov / 2)), 0, 0, 0},
           {0, 1/Math.tan(fov / 2), 0, 0},
           {0, 0, (-near-far)/(near-far), 2*near*far/(near-far)},
           {0, 0, 1, 0}
        });
    }
}