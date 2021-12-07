package ANN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import Helpers.IDXReader;
import Helpers.MathHelper;

public class Network {

	private static final int HIDDENLAYERCOUNT = 3;
	private static final int NEURONSPERHIDDENLAYER = 12;
	public static final double LEARNINGRATE = .007;
	public static final double MOMENTUM = .05;
	
	/**
	 * @return nodeID for the output node with the greatest value.
	 */
	
	public static int predict(int[] image) {
		Map<String, Connection> connections = propagateForwards(image);
		int output = -1;
		double outputValue = 0;
		
		for(int i = 0; i <= 9; i++) {
			double outputNodeValue = connections.get(String.format("h%d[1]->o[%d]", HIDDENLAYERCOUNT, i))
					.getRightNode().getValue();
			if(outputNodeValue > outputValue) {
				output = i;
				outputValue = outputNodeValue;
			}
		}
		return output;
	}

	public static void train() {
		int[] labels = IDXReader.read("train-labels.idx1-ubyte");
		int[] images = IDXReader.read("train-images.idx3-ubyte");
		int trainingSize = 10;
		Map<String, Connection> connections = parseWeights();
		for (int label = 0, pixel = 0; label < trainingSize; label++, pixel += IDXReader.PIXELSPERIMAGE) {
			int[] image = Arrays.copyOfRange(images, pixel, pixel + IDXReader.PIXELSPERIMAGE);
			connections = propagateBackwards(labels[label], propagateForwards(image));

			System.out.println(String.format("training: %d/%d", label + 1, trainingSize));
		}
		System.out.println("training: success");
		writeWeights(connections);
	}

	private static Map<String, Connection> propagateBackwards(int label, Map<String, Connection> connections) {
		Set<String> keys = connections.keySet();
		for (int i = 0; i <= 9; i++) {
			String[] connectionsHtoOutput = filterConnections(keys, "o[%d]", i);
			AN outputNode = connections.get(connectionsHtoOutput[0]).getRightNode();

			double actualValue = outputNode.getValue();
			double idealValue = (i == label ? 1 : 0);
			double error = actualValue - idealValue;
			double outputNodeDelta = -error * MathHelper.dSigmoid(outputNode.getSumOfInputWeights());
			outputNode.setNodeDelta(outputNodeDelta);

			for (String s : connectionsHtoOutput) {
				Connection connectionsHtoOutputLocal = connections.get(s);
				AN localNode = connectionsHtoOutputLocal.getLeftNode();
				propagateNodeBackwards(connectionsHtoOutputLocal, localNode, outputNode);
			}
		}

		for (int j = HIDDENLAYERCOUNT; j > 0; j--) {
			for (int k = 0; k <= NEURONSPERHIDDENLAYER; k++) {
				String[] connectionsIthroughH = filterConnections(keys, "h%d[%d]", j, k);
				for (String key : connectionsIthroughH)
					if (!key.matches("\\[0\\]->")) {
						Connection connectionsIthroughHLocal = connections.get(key);
						AN localNode = connectionsIthroughHLocal.getLeftNode();
						AN outputNode = connectionsIthroughHLocal.getRightNode();
						propagateNodeBackwards(connectionsIthroughHLocal, localNode, outputNode);
					}
			}
		}
		return connections;
	}
	
	private static void propagateNodeBackwards(Connection localConnection, AN connectionInputNode, AN connectionOutputNode) {
		double connectionOutputNodeDelta = connectionOutputNode.getNodeDelta();
		double connectionInputNodeDelta = connectionInputNode.getSumOfInputWeights()
				* (connectionOutputNodeDelta * localConnection.getWeight());
		connectionInputNode.setNodeDelta(connectionInputNodeDelta);
		
		double gradient = connectionInputNode.getValue() * connectionOutputNodeDelta;
		double weightDelta = MathHelper.weightDelta(gradient, connectionInputNode.getSumOfInputWeights());
		
		localConnection.setPrevWeightChange(weightDelta);
		localConnection.setWeight(localConnection.getWeight() + weightDelta);
	}
	
	
	/**
	 * Propagate weighted values forward.
	 * @return output node value represents answer likelihood.
	 */

	private static Map<String, Connection> propagateForwards(int[] image) {
		Map<String, Connection> connections = parseWeights();
		Set<String> keys = connections.keySet();
		// set input values
		String[] connectionsInputToH1 = filterConnections(keys, "h1[1]");
		for (int i = 1; i < connectionsInputToH1.length; i++) { // exclude bias term
			AN inputNode = connections.get(connectionsInputToH1[i]).getLeftNode();
			inputNode.setValue(image[i - 1]);
		}
		// propagate
		for (int i = 1; i <= HIDDENLAYERCOUNT; i++) {
			for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++) {
				String[] connectionsIthroughH = filterConnections(keys, "h%d[%d]", i, j);
				propagateNodeForward(connectionsIthroughH, connections);
			}
		}
		for (int i = 0; i <= 9; i++) {
			int iCopy = i;
			String[] connectionsHtoOutput = filterConnections(keys, "o[%d]", iCopy);
			propagateNodeForward(connectionsHtoOutput, connections);
		}
		return connections;
	}
	
	private static void propagateNodeForward(String[] filteredKeys, Map<String, Connection> connections) {
		double weightedSum = 0; // sum(i1 * w1, i2 * w2...)
		double sumOfWeights = 0; // sum(w1, w2...)
		for (String key : filteredKeys) {
			Connection localC = connections.get(key);
			double localCWeight = localC.getWeight();
			double input = localC.getLeftNode().getValue();
			weightedSum += (input * localCWeight);
			sumOfWeights += localCWeight;
		}
		AN outputNode = connections.get(filteredKeys[0]).getRightNode();
		outputNode.setValue(MathHelper.sigmoid(weightedSum));
		outputNode.setSumOfInputWeights(sumOfWeights);
	}

	/**
	 * @return keys for all connections linking TO a specified AN (format).
	 */

	private static String[] filterConnections(Set<String> keySet, String format, int... args) {
		Comparator<String> c = Comparator
				.comparingInt(x -> Integer.parseInt(x.substring(x.indexOf("[") + 1, x.indexOf("]")))); // sort by AN's ID
		Predicate<String> filterParam = x -> x.endsWith(format);
		switch (args.length) {
		case 0:
			break;
		case 1:
			filterParam = x -> x.endsWith(String.format(format, args[0]));
			break;
		case 2:
			filterParam = x -> x.endsWith(String.format(format, args[0], args[1]));
			break;
		default:
			throw new IllegalArgumentException();
		}
		return keySet.stream().filter(filterParam).sorted(c).toArray(x -> new String[x]);
	}
	
	/**
	 * @return Map containing connections, AN objects retained across differing connections (
	 * map.get("h1[1]->h2[1]").getLeft() = map.get("h1[1]->h2[2]").getLeft()).
	 */

	private static Map<String, Connection> parseWeights() {
		try (Scanner scanner = new Scanner(new File("./src/data/weights.txt"))) {
			Map<String, AN> map = new HashMap<>();
			String query = "\\w\\d?\\[\\d{0,3}\\]->\\w\\d?\\[\\d{1,3}\\] -?[0-9]+\\.[0-9]+"; // find all connections
			return scanner.findAll(query).map(x -> x.group().split("(->)| ")) // split into ANref, ANnextLayerRef, weight
					.map(x -> {
						map.putIfAbsent(x[0], new AN(x[0])); // map: key = ANref, value = new AN(ANref)
						map.putIfAbsent(x[1], new AN(x[1]));
						return new Connection(map.get(x[0]), map.get(x[1]), Double.parseDouble(x[2])); //init connections
					}).collect(Collectors.toUnmodifiableMap(x -> x.getRef(), x -> x));
		} catch (FileNotFoundException e) {
			writeWeights(); // if file not found, init file.
			return parseWeights();
		}
	}

	/**
	 * Write updated weights to existing file.
	 * 
	 * @param connections: Connections map with updated weights.
	 */

	private static void writeWeights(Map<String, Connection> connections) { // [0] = bias term
		try (FileWriter file = new FileWriter("./src/data/weights.txt", false)) {
			for (int i = 0; i <= IDXReader.PIXELSPERIMAGE; i++) {
				for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++)
					file.write(connections.get(String.format("i[%d]->h1[%d]", i, j)).toString());
			}
			file.write("\n\n");
			for (int i = 1; i < HIDDENLAYERCOUNT; i++) {
				for (int j = 0; j <= NEURONSPERHIDDENLAYER; j++) {
					for (int k = 1; k <= NEURONSPERHIDDENLAYER; k++)
						file.write(connections.get(String.format("h%d[%d]->h%d[%d]", i, j, i + 1, k)).toString());
				}
				file.write("\n\n");
			}
			for (int i = 0; i <= NEURONSPERHIDDENLAYER; i++) {
				for (int j = 0; j <= 9; j++)
					file.write(connections.get(String.format("h%d[%d]->o[%d]", HIDDENLAYERCOUNT, i, j)).toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialize weights and write to a new file.
	 * 
	 * format: AN->ANnextLayer connectionWeight
	 * 
	 * layers divided by new lines.
	 */

	private static void writeWeights() { // [0] = bias term
		try (FileWriter file = new FileWriter("./src/data/weights.txt", false)) {
			for (int i = 0; i <= IDXReader.PIXELSPERIMAGE; i++) {
				for (int j = 1; j <= NEURONSPERHIDDENLAYER; j++) {
					file.write(String.format("i[%d]->h1[%d] %.6f ", i, j, i == 0 ? 0
							: MathHelper.XavierWeightInit(IDXReader.PIXELSPERIMAGE + 1, NEURONSPERHIDDENLAYER)));
				}
			}
			file.write("\n\n");
			for (int i = 1; i < HIDDENLAYERCOUNT; i++) {
				for (int j = 0; j <= NEURONSPERHIDDENLAYER; j++) {
					for (int k = 1; k <= NEURONSPERHIDDENLAYER; k++) {
						file.write(String.format("h%d[%d]->h%d[%d] %.6f ", i, j, i + 1, k, j == 0 ? 0
								: MathHelper.XavierWeightInit(NEURONSPERHIDDENLAYER + 1, NEURONSPERHIDDENLAYER)));
					}
				}
				file.write("\n\n");
			}
			for (int i = 0; i <= NEURONSPERHIDDENLAYER; i++) {
				for (int j = 0; j <= 9; j++) {
					file.write(String.format("h%d[%d]->o[%d] %.6f ", HIDDENLAYERCOUNT, i, j,
							i == 0 ? 0 : MathHelper.XavierWeightInit(NEURONSPERHIDDENLAYER + 1, 10)));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
