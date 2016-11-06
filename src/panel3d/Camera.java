package panel3d;

import java.awt.*;
import org.jblas.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.event.*;
import java.util.PriorityQueue;

public class Camera extends GameObject
{
    public double ar = 1;
    public double fov = 70 * Math.PI / 180;
    public double near = 0.01;
    public double far = 50;
    public double rotY = 0;
    public double rotX = 0;
    public double offsetX = 400;
    public double offsetY = 400;
    public double scale = 400;
    public Canvas parent;
    private boolean antialias = false;
    private boolean drawColor = false;
    
    private DoubleMatrix perspectiveProj;
    private DoubleMatrix rotMat;
    
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
        IsKeyPressed.addListener(KeyEvent.VK_2, (pressed) -> {
            if(pressed)
                drawColor = !drawColor;
        });
    }
    
    public void drawScreen(Graphics g, World world)
    {
        perspectiveProj = perspective(ar, fov, near, far); 
        rotMat = RMath.rotX(rotX).mmul(RMath.rotY(rotY));
        g.fillRect(0, 0, parent.getSize().width, parent.getSize().height);
        g.setColor(Color.BLACK);
        if(antialias)
        {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON); 
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
        }
        
        PriorityQueue<Triangle> drawOrder = new PriorityQueue<Triangle>(12, 
                    (x,y) -> (y.data[0] > x.data[0] ? 1 : y.data[0] < x.data[0] ? -1 : 0));
        synchronized(world) 
        {
            for(GameObject obj : world.objects)
            {
                if(obj.rotatedMesh.points.length == 0)
                    continue;
                    
                if(obj.drawMode == 0)
                {
                    DoubleMatrix points = obj.rotatedMesh.points.addColumnVector(obj.transform.position);
                    
                    points = project(points);
                        
                    for(int i=0; i<points.columns; i++)
                    {
                        for(int j=i+1; j<points.columns; j++)
                        {
                            DoubleMatrix v1 = points.getColumn(i);
                            DoubleMatrix v2 = points.getColumn(j);
                            
                            if(Math.abs(v1.get(2)) > 1  || Math.abs(v2.get(2)) > 1) continue;                   
                            g.drawLine((int)(offsetX + scale * v1.get(0)), (int)(offsetY - scale * v1.get(1)), 
                                       (int)(offsetX + scale * v2.get(0)), (int)(offsetY - scale * v2.get(1)));
                        }
                    }
                }
                else if(obj.drawMode == 1)
                {
                    for(int i=0; i<obj.mesh.triangles.size(); i+=3)
                    {
                        DoubleMatrix points = DoubleMatrix.concatHorizontally(DoubleMatrix.concatHorizontally(
                                              obj.rotatedMesh.points.getColumn(obj.mesh.triangles.get(i)),
                                              obj.rotatedMesh.points.getColumn(obj.mesh.triangles.get(i+1))),
                                              obj.rotatedMesh.points.getColumn(obj.mesh.triangles.get(i+2)));
                        points = points.addColumnVector(obj.transform.position);
                        DoubleMatrix projected = project(points);
                        int foundoneoff = -1;
                        boolean doCorrect = false;
                        
                        for(int pt=0; pt<3; pt++)
                        {
                            if(projected.getColumn(pt).get(2) > 1)
                            {
                                doCorrect = foundoneoff == -1;
                                foundoneoff = pt;
                            }
                        }
                        
                        if(doCorrect)
                        {
                            points = points.subColumnVector(transform.position);
                            points = rotMat.mmul(points);
                            points = DoubleMatrix.concatVertically(points, DoubleMatrix.ones(1, points.columns));
                            points.put(2, foundoneoff, near);
                            points = perspectiveProj.mmul(points); 
                            for(int ii=0; ii<points.rows-1; ii++)
                                for(int jj=0; jj<points.columns; jj++)
                                    points.put(ii,jj,points.get(ii,jj)/points.get(3,jj));
                        }
                            
                        drawOrder.add(new Triangle(doCorrect ? points : projected, obj.mesh.triColor.size() > i/3 ? obj.mesh.triColor.get(i/3) : Color.magenta, 
                                      new double[] {(doCorrect ? points : projected).getRow(3).norm2(), obj.supportsSolid ? 1 : 0}));
                    }
                }
            }
            
            while(drawOrder.size() != 0)
            {
                Triangle t = drawOrder.poll();
                DoubleMatrix points = t.points;
                DoubleMatrix v1 = points.getColumn(0);
                DoubleMatrix v2 = points.getColumn(1);
                DoubleMatrix v3 = points.getColumn(2);

                int score = 0;
                score += Math.abs(v1.get(2)) > 1 ? 0 : 1;
                score += Math.abs(v2.get(2)) > 1 ? 0 : 1;
                score += Math.abs(v3.get(2)) > 1 ? 0 : 1;
                if(score < 2) continue;

                if(drawColor && t.data[1] == 1)
                {
                    g.setColor(t.color);
                    g.fillPolygon(new int[]{(int)(offsetX + scale * v1.get(0)), (int)(offsetX + scale * v2.get(0)), (int)(offsetX + scale * v3.get(0))}, 
                                  new int[]{(int)(offsetY - scale * v1.get(1)), (int)(offsetY - scale * v2.get(1)), (int)(offsetY - scale * v3.get(1))}, 3);
                }
                else
                {
                    g.setColor(Color.BLACK);
                    g.drawPolygon(new int[]{(int)(offsetX + scale * v1.get(0)), (int)(offsetX + scale * v2.get(0)), (int)(offsetX + scale * v3.get(0))}, 
                                  new int[]{(int)(offsetY - scale * v1.get(1)), (int)(offsetY - scale * v2.get(1)), (int)(offsetY - scale * v3.get(1))}, 3);
                }
            }
            
            g.setColor(Color.BLACK);
        }
    }
    
    public DoubleMatrix project(DoubleMatrix points)
    {
        points = points.subColumnVector(transform.position);
        points = rotMat.mmul(points);
        points = DoubleMatrix.concatVertically(points, DoubleMatrix.ones(1, points.columns));
        points = perspectiveProj.mmul(points); 
        for(int i=0; i<points.rows-1; i++)
            for(int j=0; j<points.columns; j++)
                points.put(i,j,points.get(i,j)/points.get(3,j));
        
        return points;
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