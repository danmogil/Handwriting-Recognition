package ANN;
public class Connection {

	private AN left;
	private AN right; // forward propagates rightward
	private double weight;
	private String ref;

	public Connection(AN left, AN right, double weight) {
		this.left = left;
		this.right = right;
		this.weight = weight;
		ref = String.format("%s[%d]->%s[%d]", left.getLayer(), left.getID(), right.getLayer(), right.getID());
	}

	public AN getLeft() {
		return left;
	}

	public AN getRight() {
		return right;
	}

	public String getRef() {
		return ref;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return String.format("%s[%d]->%s[%d] %f", left.getLayer(), left.getID(), right.getLayer(), right.getID(),
				weight);
	}
}
