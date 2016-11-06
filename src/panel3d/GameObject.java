package panel3d;

import org.jblas.DoubleMatrix;

public class GameObject
{
    public Transform transform;
    public Mesh mesh;
    public Mesh rotatedMesh;
    public DoubleMatrix lastRotation;
    public int drawMode = 0;
    public boolean supportsSolid = true;
    
    public GameObject()
    {
        transform = new Transform();
        lastRotation = transform.rotation;
        this.mesh = new Mesh();
        this.rotatedMesh = new Mesh();
    }
    
    public GameObject(double[] position)
    {
        this.transform = new Transform(position);
        this.mesh = new Mesh();
        this.rotatedMesh = new Mesh();
    }
    
    public void updateRot()
    {
        transform.updateRotation();
        if(mesh.points != null && (!lastRotation.equals(transform.rotation) || rotatedMesh.points == null || mesh.points.length != rotatedMesh.points.length))
        {
            lastRotation = transform.rotation;
            if(mesh.points.rows == transform.rotation.columns)
                rotatedMesh.points = transform.rotation.mmul(mesh.points.subColumnVector(transform.pivot)).addColumnVector(transform.pivot);
            else
                rotatedMesh.points = mesh.points;
        }
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
        updateRot();
    }
}