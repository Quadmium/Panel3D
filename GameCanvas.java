import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;
import org.jblas.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.event.*;

public class GameCanvas extends Canvas implements Runnable {
    Color backColor = new Color(255, 255, 255);
    Timer timer;
    Dimension size;
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
            
    private boolean lockMouse = false;
    
    private World world = new World();
    private Camera cam;
    
    GameCanvas() {
        super();
        setIgnoreRepaint(true);
        cam = new Camera(this);
        cam.transform.setPosition(new double[] {0, 0, -2});
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lockMouse = true;
            }
        });
        
        Chrono chrono = new Chrono(this);
        timer = new Timer(15, chrono);
        timer.start();
    }
    
    public synchronized void myRepaint() {
        BufferStrategy strategy = getBufferStrategy();
        Graphics g = strategy.getDrawGraphics();
        g.setColor(backColor);
        size = this.getSize();
        cam.ar = size.getWidth() / size.getHeight();
        cam.offsetX = size.getWidth() / 2;
        cam.offsetY = size.getHeight() / 2;
        cam.scale = size.getHeight() / 2;
        cam.drawScreen(g, world);
        
        if(g != null)
            g.dispose();
        strategy.show();
        Toolkit.getDefaultToolkit().sync();
    }
    
    private double deltaTime = 0.01;
    private long lastTime = System.nanoTime();
    
    public void run() {
        double x = 0, y = 0, z = 0, rotY = 0, rotX = 0, slowRate = 0.95, moveSpeed = 2.5, rotSpeed = 0.001, fovSpeed = 0.5, fov = 0;
        Point lastMouse = MouseInfo.getPointerInfo().getLocation();
        GameObject cube = new GameObject();
        cube.mesh = new Mesh(points);
        world.objects.add(cube);
        cube = new GameObject();
        cube.mesh = new Mesh(points2);
        world.objects.add(cube);
        cube = new GameObject();
        cube.mesh = new Mesh(points2);
        cube.transform.setPosition(new double[]{4,0,0});
        world.objects.add(cube);
        cube = new GameObject();
        cube.mesh = new Mesh(points2);
        cube.transform.setPosition(new double[]{8,0,0});
        world.objects.add(cube);
        cube = new GameObject();
        cube.mesh = new Mesh(points2);
        cube.transform.setPosition(new double[]{12,0,0});
        world.objects.add(cube);
        GravCube gravCube = new GravCube();
        gravCube.mesh = new Mesh(points2);
        gravCube.transform.setPosition(new double[]{4,-20,4});
        world.objects.add(gravCube);
        Pendulum pendulum = new Pendulum();
        pendulum.transform.setPosition(new double[]{8,-10,0});
        world.objects.add(pendulum);
        Fan fan = new Fan();
        fan.transform.setPosition(new double[]{16,-10,0});
        world.objects.add(fan);
        
        while(true)
        {
            deltaTime = (System.nanoTime() - lastTime) / 1000000000.0;
            lastTime = System.nanoTime();
            for(GameObject obj : world.objects)
                obj.OnFixedUpdate(deltaTime);
            
            Point m = MouseInfo.getPointerInfo().getLocation();
            rotY = (m.x - lastMouse.x) * -rotSpeed;
            rotX = (m.y - lastMouse.y) * rotSpeed;
            if(lockMouse)
                moveMouse(lastMouse);
            else
                lastMouse = m;
            x *= slowRate;
            y *= slowRate;
            z *= slowRate;
            fov *= slowRate;
            
            if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE)) y=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) y=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_D)) x=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_A)) x=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_W)) z=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_S)) z=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_F)) fov=fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_R)) fov=-fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_ESCAPE)) lockMouse = false;
            cam.transform.position.put(0,0, cam.transform.position.get(0,0) + x*Math.cos(cam.rotY) - z*Math.sin(cam.rotY));
            cam.transform.position.put(1,0, cam.transform.position.get(1,0) + (Math.abs(y)>0.01?y:z*Math.sin(cam.rotX)));
            cam.transform.position.put(2,0, cam.transform.position.get(2,0) + z*Math.cos(cam.rotY) + x*Math.sin(cam.rotY));
            cam.rotY += rotY;
            cam.rotX += rotX;
            cam.fov += fov;
            
            if(threadStop) {
                timer.stop();
                return;
            }
            try{Thread.sleep(16);}catch(Exception e){}
        }
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

