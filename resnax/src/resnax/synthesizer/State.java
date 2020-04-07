package resnax.synthesizer;

import java.sql.Timestamp;

public class State implements Comparable<State> {
  
  public PartialProgram pp;
  public double cost = 0.0;
  public double ts = 0.0;
  
  // The (P, C) tuple
  // TODO: update when have constraint class
  public State(PartialProgram pp, double cost) {
    this.pp = pp;
    this.cost = cost;
    this.ts = new Timestamp(System.currentTimeMillis()).getTime();
  }

  public State(State oldState) {
    this.pp = new PartialProgram(oldState.pp);
    this.cost = oldState.cost;
    this.ts = new Timestamp(System.currentTimeMillis()).getTime();
  }

  public boolean checkDuplicate() {
    return this.pp.checkDuplicate();
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    throw new RuntimeException();
  }

  @Override
  public boolean equals(Object obj) {
    assert (obj instanceof State);

    return this.toString().equals(obj.toString());
  }

  @Override
  public String toString() {
    return "(" + this.pp.toString() + ": " + this.cost + ", " + "max_depth: " + this.pp.max_depth + "," + "num var node:" + this.pp.varNodes.size()
        + ", " + "num var rf node:"
        + this.pp.numRefinementSketch + ", " + "num operator sketch:" + this.pp.numOperatorSketch + ")";
  }

  public String toOutput() {
    return this.pp.toString() + ": " + this.cost;
  }

  @Override public int compareTo(State o) {

    if (this.cost > o.cost) return 1;
    else if (this.cost < o.cost) return -1;
    else return 0;

  }
}
