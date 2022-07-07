package resnax.synthesizer;

import resnax.synthesizer.solver.Expression;
import resnax.synthesizer.solver.SolverNodes;
import resnax.synthesizer.solver.SolverNode;

import java.io.Serializable;
import java.util.List;

public abstract class Node implements Serializable {

  public final int id;

  // TODO: it is allowed SolverNode in SketchProgram don't have parent
  public Node parent;
  public SolverNodes.LengthSolverNode s;   // Expression node

  public Node(int id) {
    this(id, null, null);
  }

  public Node(int id, SolverNodes.LengthSolverNode s) {
    this(id, null, s);
  }

  public Node(int id, Node parent) {
    this(id, parent, null);
  }

  public Node(int id, Node parent, SolverNodes.LengthSolverNode s) {
    this.id = id;
    this.parent = parent;
    this.s = s;
  }

  public abstract RegexProgram getRegex(RegexProgram rp);

  public abstract RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox);

  public abstract StringBuilder toStringBuilder(StringBuilder b);

  public abstract List<String> generateSubsumePrunedProgram(int depth);

  public abstract List<String> generateSubsumeNotPrunedProgram(int depth);

  public abstract SolverNode generateExpression(Expression e);

  public abstract SolverNodes.LengthSolverNode getsNode();

  public abstract void addRepeatNodes(List<Nodes.OperatorNode> repeatNodes, List<Nodes.OperatorNode> repeatAtLeastNodes);

  @Override public int hashCode() {
    return id;
  }

  ;

  @Override public boolean equals(Object obj) {
    if (obj instanceof Node) {
      return this.id == ((Node) obj).id;
    }
    return false;
  }

  @Override
  public abstract String toString();

}
