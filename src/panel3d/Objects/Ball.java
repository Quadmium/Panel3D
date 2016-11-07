package panel3d.Objects;

import java.awt.Color;
import org.jblas.*;
import panel3d.*;

public class Ball extends TriSphere
{
    private double speed = 3;
    public Rigidbody rigidbody;
    
    public Ball()
    {
        this(1, 8, 0, 0, 0);
    }
    
    public Ball(double radius, int subDiv, double r, double g, double b)
    {
        super(radius, subDiv, r, g, b);
        rigidbody = new Rigidbody(this);
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
        
        rigidbody.OnFixedUpdate(deltaTime);
        
        double lowerBarrier = -3, xB = -1, xB2 = 17, zB = -10+-26, zB2 = -10+-8, radius = 1;
        
        if(transform.position.get(0) < xB + radius || transform.position.get(0) > xB2 - radius)
        {
            rigidbody.velocity.put(0, rigidbody.velocity.get(0) * -1);
            transform.position.put(0, transform.position.get(0) < xB + radius ? xB + radius : xB2 - radius);
        }
        
        if(transform.position.get(1) < lowerBarrier + radius)
        {
            rigidbody.velocity.put(1, rigidbody.velocity.get(1) * -1);
            transform.position.put(1, lowerBarrier + radius);
        }
        
        if(transform.position.get(2) < zB + radius || transform.position.get(2) > zB2 - radius)
        {
            rigidbody.velocity.put(2, rigidbody.velocity.get(2) * -1);
            transform.position.put(2, transform.position.get(2) < zB + radius ? zB + radius : zB2 - radius);
        }
    }
}