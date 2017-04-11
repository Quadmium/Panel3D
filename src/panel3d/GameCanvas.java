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
            TriSphere trisphere2 = new TriSphere(18, 10, 0, 0, 30);
            trisphere2.transform.setPosition(new double[]{8,0,-10+-17});
            world.objects.add(trisphere2);
            
            TriCylinder cyl = new TriCylinder(1, 1, 8, 2, 0, 0, 0);
            cyl.transform.setPosition(new double[]{-2, 0, 0});
            world.objects.add(cyl);
            
            for(int r = 0; r < 5; r++)
            for(int rr = 0; rr < 5; rr++)
            {
                /*cube = new GameObject();
                cube.mesh = new Mesh(points2);
                cube.transform.setPosition(new double[]{r*4,-4,-25+rr*4});
                world.objects.add(cube);*/
                RectPrism rectPrism = new RectPrism(2,2,2);
                rectPrism.transform.setPosition(new double[]{r*4,-4,-10+-25+rr*4});
                rectPrism.supportsSolid = false;
                world.objects.add(rectPrism);
            }
            
            Ball ball = new Ball(1, 8, 20, 0, 0);
            ball.transform.setPosition(new double[]{2,5,-10+-15});
            ball.rigidbody.velocity.put(0, 8);
            ball.rigidbody.velocity.put(2, 9);
            world.objects.add(ball);
            
            Ball ball2 = new Ball(1.5, 8, 0, 20, 0);
            ball2.transform.setPosition(new double[]{4,5,-10+-15});
            ball2.rigidbody.velocity.put(0, 8);
            ball2.rigidbody.velocity.put(2, 6);
            world.objects.add(ball2);
            
            RectPrism rectPrism = new RectPrism(10,2,10);
            rectPrism.transform.setPosition(new double[]{-20,0,0});
            rectPrism.supportsSolid = false;
            world.objects.add(rectPrism);
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
        int camMode = 0;
        boolean pressingC = false, inJump = false;
        double groundY = -1, height=3, originalHeight = 3;
        double jumpStartTime = System.nanoTime();
        
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
            

            if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE)) y=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) y=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_D)) x=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_A)) x=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_W)) z=moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_S)) z=-moveSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_F)) fov=fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_R)) fov=-fovSpeed * deltaTime;
            if(IsKeyPressed.isPressed(KeyEvent.VK_ESCAPE)) lockMouse = false;
            if(IsKeyPressed.isPressed(KeyEvent.VK_C) && !pressingC) 
            {
                camMode = 1-camMode;
                pressingC = true;
            }
            else if(!IsKeyPressed.isPressed(KeyEvent.VK_C) && pressingC) 
                pressingC = false;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SHIFT)) height=originalHeight/2;
            else height=originalHeight;
            if(IsKeyPressed.isPressed(KeyEvent.VK_SPACE) && !inJump) 
            {
                inJump = true;
                jumpStartTime = System.nanoTime();
            }
            
            camera.rotY += rotY;
            camera.rotX += rotX;
            camera.fov += fov;
            x *= slowRate;
            y *= slowRate;
            z *= slowRate;
            fov *= slowRate;
            
            if(camMode == 0)
            {
                camera.transform.position.put(0, camera.transform.position.get(0) + x*Math.cos(camera.rotY) - z*Math.sin(camera.rotY));
                camera.transform.position.put(1, camera.transform.position.get(1) + (Math.abs(y)>0.6 * deltaTime ?y:z*Math.sin(camera.rotX)));
                camera.transform.position.put(2, camera.transform.position.get(2) + z*Math.cos(camera.rotY) + x*Math.sin(camera.rotY));
            }
            else if(camMode == 1)
            {
                camera.transform.position.put(1, groundY + height);
                camera.transform.position.put(0, camera.transform.position.get(0) + x*Math.cos(camera.rotY) - z*Math.sin(camera.rotY));
                camera.transform.position.put(2, camera.transform.position.get(2) + z*Math.cos(camera.rotY) + x*Math.sin(camera.rotY));
                
                if(inJump)
                {
                    double passedTime = (System.nanoTime() - jumpStartTime) / 1000000000.0;
                    double dy = Constants.GRAVITY_ACCEL / 2 * passedTime * passedTime + 5 * passedTime;
                    camera.transform.position.put(1, camera.transform.position.get(1) + dy);
                    if(dy < 0)
                        inJump = false;
                }
            }
            
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

