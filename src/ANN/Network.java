package ANN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import Helpers.IDXReader;
import Helpers.MathHelper;

public class Network {

	private static final int HIDDENLAYERCOUNT = 3;
	private static final int NEURONSPERHIDDENLAYER = 12;
	private static final double LEARNINGRATE = .01;

	private static Map<String, Connection> propagateBackwards(int label, Map<String, Connection> connections) {
		Set<String> keys = connections.keySet();
		for (int i = 0; i <= 9; i++) {
			String[] filtered = filterConnections(keys, "o[%d]", i);
			for (int j = 0; j < filtered.length; j++) {
				Connection c = connections.get(filtered[j]);
				double output = c.getRight().getValue();
				double outputError = (output - (i == label ? 1 : 0)) * MathHelper.transferDerivative(output);
				c.setWeight(c.getWeight() - LEARNINGRATE * outputError * c.getLeft().getValue());
			}
		}
		return connections;
	}

	private static Map<String, Connection> propagateForwards(int[] input, Map<String, Connection> connections) {
		Set<String> keys = connections.keySet();
		// set inputs
		String[] filtered = filterConnections(keys, "h1[1]");
		for (int i = 1; i < filtered.length; i++)
			connections.get(filtered[i]).getLeft().setValue(input[i - 1]);
		// forward propagate
		for (int i = 1; i <= HIDDENLAYERCOUNT; i++) {
			for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++) {
				filtered = filterConnections(keys, "h%d[%d]", i, j);
				double weightedSum = 0;
				for (int k = 0; k <= NEURONSPERHIDDENLAYER; k++) {
					Connection curr = connections.get(filtered[k]);
					weightedSum += (curr.getLeft().getValue() * curr.getWeight());
				}
				connections.get(filtered[0]).getRight().setValue(MathHelper.sigmoid(weightedSum));
			}
		}
		for (int i = 0; i <= 9; i++) {
			final int iCopy = i;
			filtered = filterConnections(keys, "o[%d]", iCopy);
			double weightedSum = 0;
			for (int k = 0; k <= NEURONSPERHIDDENLAYER; k++) {
				Connection curr = connections.get(filtered[k]);
				weightedSum += (curr.getLeft().getValue() * curr.getWeight());
			}
			connections.get(filtered[0]).getRight().setValue(MathHelper.sigmoid(weightedSum));
		}
		return connections;
	}

	private static String[] filterConnections(Set<String> keySet, String format, int... args) {
		Comparator<String> c = Comparator
				.comparingInt(x -> Integer.parseInt(x.substring(x.indexOf("[") + 1, x.indexOf("]"))));
		switch (args.length) {
		case 0:
			return keySet.stream().filter(x -> x.endsWith(format)).sorted(c).toArray(x -> new String[x]);
		case 1:
			return keySet.stream().filter(x -> x.endsWith(String.format(format, args[0]))).sorted(c)
					.toArray(x -> new String[x]);
		case 2:
			return keySet.stream().filter(x -> x.endsWith(String.format(format, args[0], args[1]))).sorted(c)
					.toArray(x -> new String[x]);
		default:
			throw new IllegalArgumentException();
		}
	}

	private static Map<String, Connection> parseWeights() {
		try (Scanner scanner = new Scanner(new File("./src/data/weights.txt"))) {
			Map<String, AN> map = new HashMap<>();
			return scanner.findAll("\\w\\d?\\[\\d{0,3}\\]->\\w\\d?\\[\\d{1,3}\\] -?[0-9]+\\.[0-9]+")
					.map(x -> x.group().split("(->)| ")).map(x -> {
						map.putIfAbsent(x[0], new AN(x[0]));
						map.putIfAbsent(x[1], new AN(x[1]));
						return new Connection(map.get(x[0]), map.get(x[1]), Double.parseDouble(x[2]));
					}).collect(Collectors.toUnmodifiableMap(x -> x.getRef(), x -> x));
		} catch (FileNotFoundException e) {
			initWeights();
			return parseWeights();
		}
	}

	private static void initWeights() { // [0] = bias term
		try (FileWriter file = new FileWriter("./src/data/weights.txt", false)) {
			for (int i = 0; i <= IDXReader.PIXELSPERIMAGE; i++) {
				for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++) {
					file.write(String.format("i[%d]->h1[%d] %f ", i, j,
							MathHelper.weightInitXavier(IDXReader.PIXELSPERIMAGE)));
				}
			}
			file.write("\n\n");
			for (int i = 1; i < HIDDENLAYERCOUNT; i++) {
				for (int j = 0; j <= NEURONSPERHIDDENLAYER; j++) {
					for (int k = 1; k <= NEURONSPERHIDDENLAYER; k++) {
						file.write(String.format("h%d[%d]->h%d[%d] %f ", i, j, i + 1, k,
								MathHelper.weightInitXavier(NEURONSPERHIDDENLAYER)));
					}
				}
				file.write("\n\n");
			}
			for (int i = 0; i <= NEURONSPERHIDDENLAYER; i++) {
				for (int j = 0; j <= 9; j++) {
					file.write(String.format("h%d[%d]->o[%d] %f ", HIDDENLAYERCOUNT, i, j,
							MathHelper.weightInitXavier(NEURONSPERHIDDENLAYER)));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		propagateBackwards(1, propagateForwards(IDXReader.read("train-images.idx3-ubyte"), parseWeights()));
	}
}
