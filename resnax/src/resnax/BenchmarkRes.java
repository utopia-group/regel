package resnax;

import resnax.synthesizer.State;

public class BenchmarkRes {

  public boolean succ;
  public State program;
  public double cost;
  public double time;
  public boolean matchGt;

  public String regex;

  public BenchmarkRes(boolean succ, State program, double cost, double time, boolean matchGt) {
    this.succ = succ;
    this.program = program;
    this.cost = cost;
    this.time = time;
    this.matchGt = matchGt;
  }

  public BenchmarkRes(boolean succ, State p, double cost, double synthesizeTime, boolean matchGT, String resRegex) {

    this(succ, p, cost, synthesizeTime, matchGT);

    this.regex = resRegex;
  }
}
