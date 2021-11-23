import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ANN {

	private static final int HIDDENLAYERCOUNT = 3;
	private static final int NEURONSPERHIDDENLAYER = 12;
	private static final boolean ISBIASTERM = true;

	private static Connection[] readWeights() {
		try (Scanner scanner = new Scanner(new File("./src/data/weights.txt"))) {
			return scanner.findAll("\\w\\d?\\[\\w\\d{0,3}\\]->\\w\\d?\\[\\w\\d{0,3}\\] -?[0-9]+\\.[0-9]+")
					.map(x -> x.group().split("(->)| "))
					.map(x -> new Connection(new AN(x[0]), new AN(x[1]), Double.parseDouble(x[2])))
					.toArray(x -> new Connection[x]);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static void initWeights() {
		try (FileWriter file = new FileWriter("./src/data/weights.txt", false)) {
			for (int i = 1; i <= (ISBIASTERM ? IDXReader.PIXELSPERIMAGE + 1 : IDXReader.PIXELSPERIMAGE); i++) {
				for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++) {
					double initialWeight = MathHelper.weightInitXavier(IDXReader.PIXELSPERIMAGE);
					if (i == IDXReader.PIXELSPERIMAGE + 1)
						file.write(String.format("i[b]->h1[%d] %f ", j, initialWeight));
					else
						file.write(String.format("i[%d]->h1[%d] %f ", i, j, initialWeight));
				}
			}
			file.write("\n\n");
			for (int i = 1; i < HIDDENLAYERCOUNT; i++) {
				for (int j = 1; j <= (ISBIASTERM ? NEURONSPERHIDDENLAYER + 1 : NEURONSPERHIDDENLAYER); j++) {
					for (int k = 1; k <= NEURONSPERHIDDENLAYER; k++) {
						double initialWeight = MathHelper.weightInitXavier(NEURONSPERHIDDENLAYER);
						if (j == NEURONSPERHIDDENLAYER + 1)
							file.write(String.format("h%d[b]->h%d[%d] %f ", i, i + 1, k, initialWeight));
						else
							file.write(String.format("h%d[%d]->h%d[%d] %f ", i, j, i + 1, k, initialWeight));
					}
				}
				file.write("\n\n");
			}
			for (int i = 1; i <= (ISBIASTERM ? NEURONSPERHIDDENLAYER + 1 : NEURONSPERHIDDENLAYER); i++) {
				for (int j = 0; j <= 9; j++) {
					double initialWeight = MathHelper.weightInitXavier(NEURONSPERHIDDENLAYER);
					if (i == NEURONSPERHIDDENLAYER + 1)
						file.write(String.format("h%d[b]->o[%d] %f ", HIDDENLAYERCOUNT, j, initialWeight));
					else
						file.write(String.format("h%d[%d]->o[%d] %f ", HIDDENLAYERCOUNT, i, j, initialWeight));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {

	}
}
