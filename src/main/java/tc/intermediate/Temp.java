package tc.intermediate;

public class Temp {
  private final String name;

  public Temp(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
