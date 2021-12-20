package ANN;

public class Connection {
  private AN leftNode;
  private AN rightNode;
  private double weight;
  private String ref;

  public Connection(AN leftNode, AN rightNode, double weight) {
    this.leftNode = leftNode;
    this.rightNode = rightNode;
    this.weight = weight;
    ref = String.format("%s->%s", leftNode.getRef(), rightNode.getRef());
  }

  public AN getLeftNode() {
    return leftNode;
  }

  public AN getRightNode() {
    return rightNode;
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
    return String.format("%s %f ", ref, weight);
  }
}
