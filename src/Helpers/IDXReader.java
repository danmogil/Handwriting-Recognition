package Helpers;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * File reader for MNIST's training/testing data (IDX file format).
 * 
 * format details: https://www.fon.hum.uva.nl/praat/manual/IDX_file_format.html
 */

public class IDXReader {

	public static final int PIXELSPERIMAGE = 784;

	/**
	 * @param fileName: Do not include directory. Files must be located at
	 *                  "./src/data"
	 * @return int[] containing image labels (array[0] = 1st label) or the images
	 *         themselves (Arrays.copyOfRange(array, 0, PIXELSPERIMAGE) = 1st
	 *         image).
	 */

	public static int[] read(String fileName) {
		ByteBuffer buffer = loadFile(fileName);
		int magicNumber = buffer.getInt();

		switch (magicNumber) {
		case (2049):
			return getLabels(buffer);
		case (2051):
			return getImages(buffer);
		default:
			throw new RuntimeException("Invalid Magic Number");
		}
	}

	private static int[] getLabels(ByteBuffer buffer) {
		int numOfLabels = buffer.getInt();
		int[] labels = new int[numOfLabels];
		for (int i = 0; i < numOfLabels; i++)
			labels[i] = buffer.get() & 0xFF; // signed -> unsigned
		return labels;
	}

	private static int[] getImages(ByteBuffer buffer) {
		int numOfImages = buffer.getInt();
		int pixelsPerArray = numOfImages * PIXELSPERIMAGE;
		buffer.getInt(); // number of rows
		buffer.getInt(); // number of columns
		int[] pixels = new int[pixelsPerArray];
		for (int i = 0; i < pixelsPerArray; i++)
			pixels[i] = buffer.get() & 0xFF; // signed -> unsigned
		return pixels;
	}

	private static ByteBuffer loadFile(String fileName) {
		try (RandomAccessFile file = new RandomAccessFile("./src/data/" + fileName, "r");
				FileChannel channel = file.getChannel();) {
			long fileSize = channel.size();
			ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
			channel.read(buffer);
			if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
				buffer.flip(); // flip bytes on low-endian machines
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			for (int i = 0; i < fileSize; i++)
				stream.write(buffer.get());
			return ByteBuffer.wrap(stream.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
