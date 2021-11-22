
public class MathHelper {

	public static double weightInitXavier(int numNodeInputs) { // weight = U [-(1/sqrt(n)), 1/sqrt(n)] where n is the
																// number of inputs to the node, and U is a uniform
																// probability distribution.
		double lower = -(1 / Math.sqrt(numNodeInputs));
		double upper = (1 / Math.sqrt(numNodeInputs));
		return lower + Math.random() * (upper - lower);
	}
}
