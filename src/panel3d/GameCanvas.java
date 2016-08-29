package panel3d;

import panel3d.Objects.GravCube;
import panel3d.Objects.Pendulum;
import panel3d.Objects.Fan;
import panel3d.Objects.TriCube;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

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
    
    public synchronized void myRepaint() {
        double rotY = 0, rotX = 0, rotSpeed = 1;
        Point lastMouse = MouseInfo.getPointerInfo().getLocation();
        
        double lastFPSResetTime = System.nanoTime() / 1000000000.0;
        int numRendered = 0;
        double averageFPS=0, fpsUpdateSeconds = 0.2;
        
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
        }
        new Thread(() -> {myRepaint();}).start();
        
        timerFixed = new Timer();
        timerFixed.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                FixedUpdateLoop();
            }
        }, 0, 16);
    }
    
    private void FixedUpdateLoop()
    {
        double x = 0, y = 0, z = 0, slowRate = 0.95, moveSpeed = 2.5, rotSpeed = 0.001, fovSpeed = 0.5, fov = 0;
        fixedDeltaTime = (System.nanoTime() - fixedLastTime) / 1000000000.0;
        fixedLastTime = System.nanoTime();
        for(GameObject obj : world.objects)
            obj.OnFixedUpdate(fixedDeltaTime);
        
        x *= slowRate;
        y *= slowRate;
        z *= slowRate;
        fov *= slowRate;

        if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE)) y=moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) y=-moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_D)) x=moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_A)) x=-moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_W)) z=moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_S)) z=-moveSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_F)) fov=fovSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_R)) fov=-fovSpeed * fixedDeltaTime;
        if(IsKeyPressed.isPressed(KeyEvent.VK_ESCAPE)) lockMouse = false;
        camera.transform.position.put(0,0, camera.transform.position.get(0,0) + x*Math.cos(camera.rotY) - z*Math.sin(camera.rotY));
        camera.transform.position.put(1,0, camera.transform.position.get(1,0) + (Math.abs(y)>0.01?y:z*Math.sin(camera.rotX)));
        camera.transform.position.put(2,0, camera.transform.position.get(2,0) + z*Math.cos(camera.rotY) + x*Math.sin(camera.rotY));
        camera.fov += fov;
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

