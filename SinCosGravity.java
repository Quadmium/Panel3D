// for speed purpose I wont call the sin and cos math fuction every time
// (these functions are a little time consuming)
// as I work with degrees (from 0 to 359 as int) I build a static table of sin and cos
// for these 360 values then we just have to fetch these values 
// also no need to convert from degrees to radian each time
public class SinCosGravity {

	// for how much I increment for every 30 degrees it is a "gravity"
	// this the number by which I will mutiply the speed of the balls (in degree) according
	// to 30 degrees slices
	// so from 0 to 30 degrees for 30 to 60 degrees fro 60 to 90 degrees.....
	static final int[] weight = {2, 2, 1, 1, 2, 2, 3, 3, 4, 4, 3, 3};
	// static arrays shered by all instances
	static double[] sinValue = new double[360];
	static double[] cosValue = new double[360];
	static int[] gravityValue = new int[360];
	
	// all instances share this array build in a static initializer
	static {
		for(int i = 0; i < 360; i++) {
			// have to convert in radian (and negate them if I go counter clockwise)
			double radiant = -Math.toRadians((double) i);
			// because Java sin/cos method use radian
			sinValue[i] = Math.sin(radiant);
			cosValue[i] = Math.cos(radiant);
			gravityValue[i] = weight[i / 30];
		}
	}
	// ok just a constructor
	SinCosGravity() {	
	}
	
	// retunrs sin for an int degree passed as parameter
	double sin(int degree) {
		return sinValue[degree % 360];
	}
	// retunrs cos for an int degree passed as parameter
	double cos(int degree) {
		return cosValue[degree % 360];
	}
	// retuns gravity for an int degree passed as parameter
	int gravity(int degree) {
		return gravityValue[degree % 360];
	}
}

