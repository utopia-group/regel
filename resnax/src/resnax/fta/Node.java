package resnax.fta;

import resnax.fta.Nodes;
import resnax.fta.RegexProgram;

import java.io.Serializable;
import java.util.List;

public abstract class Node implements Serializable {

    public final int id;

    // TODO: it is allowed SolverNode in SketchProgram don't have parent
    public Node parent;

    public Node(int id) {
        this(id, null);
    }

    public Node(int id, Node parent) {
        this.id = id;
        this.parent = parent;
    }

    public abstract RegexProgram getRegex(RegexProgram rp);

    public abstract RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox);

    public abstract StringBuilder toStringBuilder(StringBuilder b);

    public abstract List<String> generateSubsumePrunedProgram(int depth);

    public abstract List<String> generateSubsumeNotPrunedProgram(int depth);

    public abstract void addRepeatNodes(List<Nodes.OperatorNode> repeatNodes, List<Nodes.OperatorNode> repeatAtLeastNodes);

    @Override public int hashCode() {
        return id;
    }

    ;

    @Override public boolean equals(Object obj) {
        if (obj instanceof resnax.synthesizer.Node) {
            return this.id == ((resnax.synthesizer.Node) obj).id;
        }
        return false;
    }

    @Override
    public abstract String toString();

}
