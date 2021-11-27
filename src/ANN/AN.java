package ANN;

public class AN {

	private double value;
	private String layer;
	private int id;

	public AN(String ref) {
		String[] s = ref.split("\\[|]");
		layer = s[0];
		id = Integer.parseInt(s[1]);
		if (id == 0)
			value = 1;
	}

	public String getLayer() {
		return layer;
	}

	public int getID() {
		return id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
