package Helpers;
import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

//https://www.fon.hum.uva.nl/praat/manual/IDX_file_format.html

public class IDXReader {

	public static final int PIXELSPERIMAGE = 784;

	public static int[] read(String fileName) {
		ByteBuffer buffer = loadFile(fileName);
		switch (buffer.getInt()) {
		case (2049):
			return getLabels(buffer);
		case (2051):
			return getImages(buffer);
		default:
			throw new RuntimeException("Invalid magic number!");
		}
	}

	private static int[] getLabels(ByteBuffer buffer) {
		int size = buffer.getInt();
		int[] labels = new int[size];
		for (int i = 0; i < size; i++)
			labels[i] = buffer.get() & 0xFF; // signed -> unsigned
		return labels;
	}

	private static int[] getImages(ByteBuffer buffer) {
		int pixelCount = buffer.getInt() * PIXELSPERIMAGE, rowCount = buffer.getInt(), columnCount = buffer.getInt();
		int[] pixels = new int[pixelCount];
		for (int i = 0; i < pixelCount; i++)
			pixels[i] = buffer.get() & 0xFF;
		return pixels;
	}

	private static ByteBuffer loadFile(String fileName) {
		try (RandomAccessFile file = new RandomAccessFile("./src/data/" + fileName, "r");
				FileChannel channel = file.getChannel();) {
			long fileSize = channel.size();
			ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
			channel.read(buffer);
			if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
				buffer.flip();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			for (int i = 0; i < fileSize; i++)
				stream.write(buffer.get());
			return ByteBuffer.wrap(stream.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
