import org.jblas.*;
import java.util.List;
import java.util.Arrays;

public class TriCube extends GameObject
{
    private DoubleMatrix points = RMath.PTDM(new double[][]{
            {1,1,1},
            {1,-1,1},
            {-1,1,1},
            {-1,-1,1},
            {1,1,-1},
            {1,-1,-1},
            {-1,1,-1},
            {-1,-1,-1}
    });
    private List<Integer> triangles = Arrays.asList(0,1,2,1,2,3,4,5,6,5,6,7,0,4,5,0,1,5,2,6,7,2,3,7,0,2,6,0,4,6,1,3,7,1,5,7);
    
    private double speed = 3;
    
    public TriCube()
    {
        mesh.points = points.dup();
        mesh.triangles = triangles;
        drawMode = 1;
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        
    }
}