import java.io.FileWriter;
import java.io.IOException;

public class ANN {

	private static final int HIDDENLAYERCOUNT = 3;
	private static final int NEURONSPERHIDDENLAYER = 12;
	private static final boolean ISBIASTERM = true;

	private void initFile() {
		try (FileWriter file = new FileWriter("./src/data/weights.txt", false)) {
			for (int i = 1; i <= IDXReader.PIXELSPERIMAGE; i++) // rethink
				file.write(String.format("(%c->%c) %f")); // i -> h1
			file.write("\n");
			for (int i = 1; i < HIDDENLAYERCOUNT; i++) {
				for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++)
					file.write(String.format("(%c->%c) %f")); // h1 -> h2, h2 -> h3
				file.write("\n");
			}
			for (int i = 1; i <= 9; i++)
				file.write(String.format("(%c->%c) %f")); // h3 -> o
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
