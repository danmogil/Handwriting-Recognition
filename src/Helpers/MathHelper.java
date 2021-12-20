package Helpers;

public class MathHelper {

  /**
   * Weight initialization while maintaining equal variance across all layers.
   *
   * @param numNodesIn, numNodesOut: The number of in/out connections of a given
   *                    node.
   * @return A uniformly random double value between bounds +-sqrt(6/fan-in +
   *         fan-out).
   */

  public static double XavierWeightInit(int numNodesIn, int numNodesOut) {
    int sumFanInOut = numNodesIn + numNodesOut;
    double lower = (-Math.sqrt(6) / Math.sqrt(sumFanInOut));
    double upper = (Math.sqrt(6) / Math.sqrt(sumFanInOut));
    return lower + Math.random() * (upper - lower);
  }

  public static double sigmoid(double input) {
    return 1 / (1 + Math.exp(-input));
  }
}
