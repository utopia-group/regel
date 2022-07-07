package resnax.synthesizer;

import java.util.List;

// represent a or a set of regex programs
public class RegexProgram {

  public StringBuilder[] programs;

  public boolean containAndorNot;

  public boolean isCompleteSymbolic;

  public boolean isComplete;

  public boolean assignedSymbolicConstant;
  public List<Nodes.SymbolicConstantTerminalNode> addedConstants;

  // mode = 0: evaluate concrete
  // mode = 1: evaluate approx
  public RegexProgram(int mode) {
    if (mode == 0) {
      this.programs = new StringBuilder[] { new StringBuilder() };
    } else if (mode == 1) {
      this.programs = new StringBuilder[] { new StringBuilder(), new StringBuilder() };
    } else {
      throw new RuntimeException();
    }

    this.containAndorNot = false;
    this.isCompleteSymbolic = false;
    this.isComplete = false;
  }

  public void assignAddedConstants(List<Nodes.SymbolicConstantTerminalNode> addedConstants) {
    this.addedConstants = addedConstants;
    this.assignedSymbolicConstant = true;
  }

  @Override public String toString() {
    if (programs.length == 1) return programs[0].toString();
    else return programs[0].toString() + "," + programs[1].toString();
  }

}
