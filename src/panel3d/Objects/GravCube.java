package panel3d.Objects;

import panel3d.GameObject;
import panel3d.GameObject;

public class GravCube extends GameObject
{
    double gravCubeVelocity = 0;
        
    public GravCube()
    {
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        super.OnFixedUpdate(deltaTime);
        gravCubeVelocity -= 1 * deltaTime;
        transform.position.put(1, transform.position.get(1) + gravCubeVelocity);
        if(transform.position.get(1) < 0)
        {
            gravCubeVelocity = 0.5;
        }
    }
}