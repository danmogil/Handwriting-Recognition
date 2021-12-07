import ANN.Network;
import Helpers.IDXReader;
import java.util.Arrays;

public class Main {

  // Demonstration only, will be output to GUI once backprop math is fixed.
  public static void main(String[] args) {
    //    Network.train();
    int[] testImages = IDXReader.read("test-images.idx3-ubyte");

    int[] image1 = Arrays.copyOfRange(testImages, 0, 784);
    int prediction = Network.predict(testImages);

    for (int i = 0; i < image1.length; i++) {
      System.out.print(image1[i] > 0 ? "0" : " ");
      if ((i + 1) % 28 == 0) System.out.println();
    }
    System.out.println("\n\t" + prediction);
  }
}
