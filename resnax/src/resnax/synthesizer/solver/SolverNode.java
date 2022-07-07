package resnax.synthesizer.solver;

public abstract class SolverNode {

  public String toString() {
    return toStringBuilder(new StringBuilder()).toString();
  }

  public abstract StringBuilder toStringBuilder(StringBuilder b);

}
