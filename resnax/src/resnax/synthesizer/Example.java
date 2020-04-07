package resnax.synthesizer;

public class Example {
  
  public final String input;
  public final boolean output;

  public Example(String input, boolean output) {
    this.input = input;
    this.output = output;
  }

  @Override
  public int hashCode() {
    throw new RuntimeException();
  }

  @Override
  public boolean equals(Object o) {
    throw new RuntimeException();
  }

  @Override
  public String toString() {
    return "(" + input + "," + output + ")";
  }

}
