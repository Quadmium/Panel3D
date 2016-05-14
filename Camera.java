import java.awt.*;
import org.jblas.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.event.*;

public class Camera extends GameObject
{
    public double ar = 1;
    public double fov = 70 * Math.PI / 180;
    public double near = 0.01;
    public double far = 300;
    public double rotY = 0;
    public double rotX = 0;
    public double offsetX = 400;
    public double offsetY = 400;
    public double scale = 400;
    public Canvas parent;
    private boolean antialias = false;
    
    public Camera(Canvas parent){
        this.parent = parent;
        init();
    }
    
    public Camera(double ar, double fov, double near, double far, Canvas parent)
    {
        this.ar = ar;
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.parent = parent;
        init();
    }
    
    private void init()
    {
        IsKeyPressed.addListener(KeyEvent.VK_1, (pressed) -> {
            if(pressed)
                antialias = !antialias;
        });
    }
    
    public void drawScreen(Graphics g, World world)
    {
        DoubleMatrix perspectiveProj = perspective(ar, fov, near, far); 
        DoubleMatrix rotMat = RMath.rotX(rotX).mmul(RMath.rotY(rotY));
        g.fillRect(0, 0, parent.getSize().width, parent.getSize().height);
        g.setColor(Color.BLACK);
        if(antialias)
        {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON); 
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
        }
        
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
                    g.drawLine((int)(offsetX + scale * v1.get(0)), (int)(offsetY + scale * v1.get(1)), 
                               (int)(offsetX + scale * v2.get(0)), (int)(offsetY + scale * v2.get(1)));
                }
            }
        }
    }
    
    //Pre-optimization matrix:
    /*
     * return new DoubleMatrix(new double[][] {
           {1/(ar * Math.tan(fov / 2)), 0, 0, 0},
           {0, 1/Math.tan(fov / 2), 0, 0},
           {0, 0, (-near-far)/(near-far), 2*near*far/(near-far)},
           {0, 0, 1, 0}
        });
     */
    public DoubleMatrix perspective(double ar, double fov, double near, double far)
    {
        return new DoubleMatrix(new double[][] {
           {1/(Math.tan(fov / 2)), 0, 0, 0},
           {0, 1/Math.tan(fov / 2), 0, 0},
           {0, 0, (-near-far)/(near-far), 2*near*far/(near-far)},
           {0, 0, 1, 0}
        });
    }
}