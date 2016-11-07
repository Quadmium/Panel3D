package panel3d;

import panel3d.Objects.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import org.jblas.DoubleMatrix;

public class GameCanvas extends Canvas implements Runnable {
    private Color backColor = new Color(255, 255, 255);
    private Timer timerFixed;
    private Dimension size;
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
            {0, 2, 0}};
            
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
    
    private volatile World world = new World();
    private Camera camera;
    
    private double deltaTime = 0.01;
    private long lastTime = System.nanoTime();
    
    private double fixedDeltaTime = 0.01;
    private long fixedLastTime = System.nanoTime();
    
    private int firstRuns = 0;
    
    GameCanvas() {
        super();
        setIgnoreRepaint(true);
        camera = new Camera(this);
        camera.transform.setPosition(new double[] {0, 0, -2});
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lockMouse = true;
            }
        });
        
        /*Chrono chrono = new Chrono(this);
        timer = new Timer(15, chrono);
        timer.start();*/
    }
    
    public void run() {
        synchronized(world)
        {
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
            gravCube.transform.setPosition(new double[]{4,20,4});
            world.objects.add(gravCube);
            Pendulum pendulum = new Pendulum();
            pendulum.transform.setPosition(new double[]{8,10,0});
            world.objects.add(pendulum);
            Fan fan = new Fan();
            fan.transform.setPosition(new double[]{16,10,0});
            world.objects.add(fan);
            TriCube tricube = new TriCube();
            tricube.transform.setPosition(new double[]{10,0,10});
            world.objects.add(tricube);
            TriSphere trisphere = new TriSphere();
            trisphere.transform.setPosition(new double[]{15,0,10});
            world.objects.add(trisphere);
            
            for(int r = 0; r < 5; r++)
            for(int rr = 0; rr < 5; rr++)
            {
                cube = new GameObject();
                cube.mesh = new Mesh(points2);
                cube.transform.setPosition(new double[]{r*4,-4,-25+rr*4});
                world.objects.add(cube);
                /*RectPrism rectPrism = new RectPrism(4,4,4);
                rectPrism.transform.setPosition(new double[]{r*4,-4,-25+rr*4});
                rectPrism.supportsSolid = false;
                world.objects.add(rectPrism);*/
            }
            
            Ball ball = new Ball();
            ball.transform.setPosition(new double[]{2,2,-15});
            ball.rigidbody.velocity.put(0, 8);
            ball.rigidbody.velocity.put(2, 9);
            world.objects.add(ball);
        }
        new Thread(() -> {myRepaint();}).start();
        
        timerFixed = new Timer();
        timerFixed.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                FixedUpdateLoop();
            }
        }, 0, 16);
    }
    
    public synchronized void myRepaint() {
        double rotY = 0, rotX = 0, rotSpeed = 1;
        Point lastMouse = MouseInfo.getPointerInfo().getLocation();
        
        double lastFPSResetTime = System.nanoTime() / 1000000000.0;
        int numRendered = 0;
        double averageFPS=0, fpsUpdateSeconds = 0.2;
        
        double x = 0, y = 0, z = 0, slowRate = 0.95, moveSpeed = 5, fovSpeed = 0.5, fov = 0;
        
        while(true)
        {
            deltaTime = (System.nanoTime() - lastTime) / 1000000000.0;
            lastTime = System.nanoTime();
            numRendered++;
            
            BufferStrategy strategy = getBufferStrategy();
            Graphics g = strategy.getDrawGraphics();
            g.setColor(backColor);
            size = this.getSize();
            camera.ar = size.getWidth() / size.getHeight();
            camera.offsetX = size.getWidth() / 2;
            camera.offsetY = size.getHeight() / 2;
            camera.scale = size.getHeight() / 2;
            camera.drawScreen(g, world);

            Point m = MouseInfo.getPointerInfo().getLocation();
            rotY = (m.x - lastMouse.x) * -rotSpeed * deltaTime;
            rotX = (m.y - lastMouse.y) * -rotSpeed * deltaTime;
            if(lockMouse)
                moveMouse(lastMouse);
            else
                lastMouse = m;
            
            camera.rotY += rotY;
            camera.rotX += rotX;
            x *= slowRate;
            y *= slowRate;
            z *= slowRate;
            fov *= slowRate;

            if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE)) y=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) y=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_D)) x=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_A)) x=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_W)) z=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_S)) z=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_F)) fov=fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_R)) fov=-fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_ESCAPE)) lockMouse = false;
            camera.transform.position.put(0,0, camera.transform.position.get(0,0) + x*Math.cos(camera.rotY) - z*Math.sin(camera.rotY));
            camera.transform.position.put(1,0, camera.transform.position.get(1,0) + (Math.abs(y)>0.01?y:z*Math.sin(camera.rotX)));
            camera.transform.position.put(2,0, camera.transform.position.get(2,0) + z*Math.cos(camera.rotY) + x*Math.sin(camera.rotY));
            camera.fov += fov;
            
            if(System.nanoTime() / 1000000000.0 - lastFPSResetTime > fpsUpdateSeconds)
            {
                averageFPS = numRendered / fpsUpdateSeconds;
                lastFPSResetTime = System.nanoTime() / 1000000000.0;
                numRendered = 0;
            }
            
            g.drawString("FPS: " + String.format("%.2f", averageFPS), 2, (int)size.getHeight() - 20);
            g.drawString("Tick: " + String.format("%.2f", 1/fixedDeltaTime), 2, (int)size.getHeight() - 5);
            if(g != null)
                g.dispose();
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
        }
    }
    
    private void FixedUpdateLoop()
    {
        fixedDeltaTime = (System.nanoTime() - fixedLastTime) / 1000000000.0;
        fixedLastTime = System.nanoTime();
        
        // Find out later why this happens...
        // At first few updates, I think JBLAS is loading and causes hangtime, which leads to 0.4 deltaTime
        // For first 10 or so updates if cycle is running way too slow, set it to some really tiny deltaTime.
        // Hopefully JBLAS loads when that is over!
        
        if(fixedDeltaTime > 0.1 && firstRuns < 10)
        {
            fixedDeltaTime = 0.0001;
            firstRuns++;
        }
        
        synchronized(world)
        {
            for(GameObject obj : world.objects)
                obj.OnFixedUpdate(fixedDeltaTime);
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

