public class GameObject
{
    public Transform transform;
    public Mesh mesh;
    public int drawMode = 0;
    
    public GameObject()
    {
        transform = new Transform();
        this.mesh = new Mesh();
    }
    
    public GameObject(double[] position)
    {
        this.transform = new Transform(position);
        this.mesh = new Mesh();
    }
    
    public void OnFixedUpdate(double deltaTime)
    {
    }
}