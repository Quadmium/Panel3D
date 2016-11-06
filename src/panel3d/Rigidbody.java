/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel3d;

import org.jblas.*;

/**
 *
 * @author Quadmium
 */
public class Rigidbody
{
    public GameObject parent;
    public DoubleMatrix velocity = RMath.PTDM(new double[][] {{0, 0, 0}});
    public DoubleMatrix acceleration = RMath.PTDM(new double[][] {{0, 0, 0}});
    public boolean gravity = true;
    
    public Rigidbody(GameObject parent)
    {
        this.parent = parent;
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        System.out.println(deltaTime);
        velocity = velocity.add(acceleration.mul(deltaTime));
        parent.transform.position = parent.transform.position.add(velocity.mul(deltaTime));
        
        if(gravity)
            acceleration.put(1, Constants.GRAVITY_ACCEL);
    }
}
