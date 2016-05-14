import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;
import org.jblas.*;
import java.awt.event.KeyEvent;
import javax.swing.Timer;

public class GameCanvas extends Canvas implements Runnable {
    Color backColor = new Color(255, 255, 255);
    Timer timer;
    Dimension size;
    int centerX, centerY;
    boolean threadStop = false;
    
    private double[][] points = new double[][] {
            {1,1,1},
            {1,-1,1},
            {-1,1,1},
            {-1,-1,1},
            {1,1,-1},
            {1,-1,-1},
            {-1,1,-1},
            {-1,-1,-1},
            {0, -2, 0}};
            
    private double[][] points2 = new double[][] {
            {4,1,1},
            {4,-1,1},
            {2,1,1},
            {2,-1,1},
            {4,1,-1},
            {4,-1,-1},
            {2,1,-1},
            {2,-1,-1}};
            
    double scale = 200;
    double offset = 400;
    
    private World world = new World();
    private Camera cam;
    
    GameCanvas(int nbBalls, int deltaDegree) {
        super();
        setIgnoreRepaint(true);
        cam = new Camera(this);
        cam.transform.setPosition(new double[] {0, 0, -2});
        
        Chrono chrono = new Chrono(this);
        timer = new Timer(15, chrono);
        timer.start();
    }
    
    public synchronized void myRepaint() {
        BufferStrategy strategy = getBufferStrategy();
        Graphics g = strategy.getDrawGraphics();
        g.setColor(backColor);
        /*size = this.getSize();
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.BLACK);
        int mode = 0;
        for(int i=0; i<points.rows; i++)
        {
            for(int j=i+1; j<points.rows; j++)
            {
                DoubleMatrix v1 = points.getRow(i);
                DoubleMatrix v2 = points.getRow(j);
                double d2 = v1.distance2(v2);
                if(mode == 0 && d2 > 4.01) continue;
                
                g.drawLine((int)(offset + scale * v1.get(0)), (int)(offset + scale * v1.get(1)), 
                           (int)(offset + scale * v2.get(0)), (int)(offset + scale * v2.get(1)));
            }
        }*/
        cam.drawScreen(g, world);
        
        if(g != null)
            g.dispose();
        strategy.show();
        Toolkit.getDefaultToolkit().sync();
    }
    
    private double deltaTime = 0.01;
    private long lastTime = System.nanoTime();
    
    public void run() {
        double x = 0, y = 0, z = 0, rotY = 0, rotX = 0, slowRate = 0.95, moveSpeed = 0.04, rotSpeed = 0.001;
        Point lastMouse = MouseInfo.getPointerInfo().getLocation();
        GameObject cube = new GameObject();
        cube.mesh = new Mesh(points);
        world.objects.add(cube);
        cube = new GameObject();
        cube.mesh = new Mesh(points2);
        world.objects.add(cube);
        
        while(true)
        {
            deltaTime = (System.nanoTime() - lastTime) / 1000;
            lastTime = System.nanoTime();
            Point m = MouseInfo.getPointerInfo().getLocation();
            rotY = (m.x - lastMouse.x) * -rotSpeed;
            rotX = (m.y - lastMouse.y) * rotSpeed;
            moveMouse(lastMouse);
            //lastMouse = MouseInfo.getPointerInfo().getLocation();
            x *= slowRate;
            y *= slowRate;
            z *= slowRate;
            //rotY = 0;
            //rotX = 0;
            
            if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE)) y=-moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) y=moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_D)) x=moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_A)) x=-moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_W)) z=moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_S)) z=-moveSpeed;
            if(IsKeyPressed.isPressed(KeyEvent.VK_ESCAPE)) return;
            //if(IsKeyPressed.isPressed(KeyEvent.VK_F)) rotY=rotSpeed;
            //if(IsKeyPressed.isPressed(KeyEvent.VK_R)) rotY=-rotSpeed;
            cam.transform.position.put(0,0, cam.transform.position.get(0,0) + x*Math.cos(cam.rotY) - z*Math.sin(cam.rotY));
            cam.transform.position.put(1,0, cam.transform.position.get(1,0) + y*Math.cos(cam.rotX) + z*Math.sin(cam.rotX));
            cam.transform.position.put(2,0, cam.transform.position.get(2,0) + z*Math.cos(cam.rotY) + x*Math.sin(cam.rotY));
            cam.rotY += rotY;
            cam.rotX += rotX;
            //cam.rot += rot;
            /*DoubleMatrix rX = rotX(x);
            DoubleMatrix rY = rotY(y);
            DoubleMatrix rot = rX.mmul(rY);
            for(int i=0; i<points.rows; i++)
            {
                DoubleMatrix p = points.getRow(i);
                points.putRow(i, rot.mmul(p.transpose()));
            }*/
            
            if(threadStop) {
                timer.stop();
                return;
            }
            try{Thread.sleep(16);}catch(Exception e){}
        }
    }
    
    private DoubleMatrix rotZ(double t) {
        return new DoubleMatrix(new double[][]{{Math.cos(t), -Math.sin(t), 0},
                            {Math.sin(t), Math.cos(t), 0},
                            {0,0,1}});
    }
    
    private DoubleMatrix rotY(double t) {
        return new DoubleMatrix(new double[][]{{Math.cos(t), 0, Math.sin(t)},
                            {0, 1, 0},
                            {-Math.sin(t), 0, Math.cos(t)}});
    }
    
    private DoubleMatrix rotX(double t) {
        return new DoubleMatrix(new double[][]{{1, 0, 0},
                            {0, Math.cos(t), -Math.sin(t)},
                            {0, Math.sin(t), Math.cos(t)}});
    }
    
    public void moveMouse(Point p) {
        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
    
        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) { 
            GraphicsConfiguration[] configurations =
                device.getConfigurations();
            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();
                if(bounds.contains(p)) {
                    // Set point to screen coordinates.
                    Point b = bounds.getLocation(); 
                    Point s = new Point(p.x - b.x, p.y - b.y);
    
                    try {
                        Robot r = new Robot(device);
                        r.mouseMove(s.x, s.y);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
    
                    return;
                }
            }
        }
        // Couldn't move to the point, it may be off screen.
        return;
    }
}

