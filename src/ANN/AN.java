package ANN;

public class AN {
  private double value;
  private String ref;
  private double sumOfInputWeights;
  private double error;

  public AN(String connectionRef) {
    String[] s = connectionRef.split("\\[|]");
    int id = Integer.parseInt(s[1]);

    ref = String.format("%s[%d]", s[0], id);
    if (id == 0) value = 1;
  }

  public String getRef() {
    return ref;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public double getSumOfInputWeights() {
    return sumOfInputWeights;
  }

  public void setSumOfInputWeights(double sumOfInputWeights) {
    this.sumOfInputWeights = sumOfInputWeights;
  }

  public double getError() {
    return error;
  }

  public void setError(double error) {
    this.error = error;
  }
}
