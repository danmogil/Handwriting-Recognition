
public class MathHelper {

	public static double weightInitXavier(int numNodeInputs) {
		// weight = U [-(1/sqrt(n)), 1/sqrt(n)]
		// U = a uniform probability distribution.
		// n = number of inputs to the node.
		double lower = -(1 / Math.sqrt(numNodeInputs));
		double upper = (1 / Math.sqrt(numNodeInputs));
		return lower + Math.random() * (upper - lower);
	}

	private static double sigmoid(double input) {
		return 1 / (1 + Math.exp(-input));
	}
}
