
public class Connection {

	private AN left;
	private AN right;
	private double weight;

	public Connection(AN left, AN right, double weight) {
		this.left = left;
		this.right = right;
		this.weight = weight;
	}

	public AN getLeft() {
		return left;
	}

	public void setLeft(AN left) {
		this.left = left;
	}

	public AN getRight() {
		return right;
	}

	public void setRight(AN right) {
		this.right = right;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}
