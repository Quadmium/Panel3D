import org.jblas.*;

public class RMath
{
    public static DoubleMatrix rotZ(double t) {
    	return new DoubleMatrix(new double[][]{{Math.cos(t), -Math.sin(t), 0},
    						{Math.sin(t), Math.cos(t), 0},
    						{0,0,1}});
    }
    
    public static DoubleMatrix rotY(double t) {
    	return new DoubleMatrix(new double[][]{{Math.cos(t), 0, Math.sin(t)},
    						{0, 1, 0},
    						{-Math.sin(t), 0, Math.cos(t)}});
    }
    
    public static DoubleMatrix rotX(double t) {
    	return new DoubleMatrix(new double[][]{{1, 0, 0},
    						{0, Math.cos(t), -Math.sin(t)},
    						{0, Math.sin(t), Math.cos(t)}});
    }
}