package resnax.fta;

import resnax.fta.DSL;
import resnax.fta.Node;
import resnax.fta.Nodes;
import resnax.fta.RegexProgram;

import java.util.*;

public class SketchProgram {

    public boolean parseError;

    public List<Node> nodes;
    public final DSL.CFG grammar;

    public Map<Integer, Node> repSketches = new HashMap<>();

    public Node startNode;

    private int nodeId;       // TODO: i think nodeid does not matter so i will ignore this

    public SketchProgram(DSL.CFG grammar) {
        this.nodes = new ArrayList<>();
        this.grammar = grammar;
        this.nodeId = 0;
    }

    public Node mkConstantNode(int k) {
        Node ret = new Nodes.RealConstantTerminalNode(this.nodeId++, this.grammar.nameToSymbol.get("k"), k);
        this.nodes.add(ret);
        return ret;
    }

    public Node mkTerminalNode(String terminalName) {
        Node ret = new Nodes.NullaryTerminalNode(this.nodeId++, this.grammar.nameToSymbol.get(terminalName));
        this.nodes.add(ret);
        return ret;

    }

    public Node mkOperatorNode(String operatorName, Node... args) {
        return this.mkOperatorNode(operatorName, false, args);
    }

    public Node mkOperatorNode(String operatorName, boolean special, Node... args) {

        if (operatorName.equals("sep")) {

            // return concat(A,?{optional(concat(B,A)),star(concat(B,A))})
            Node a = args[0];
            Node b = args[1];
            Set<Node> components = new HashSet<>();
            components.add(mkOperatorNode("optional", mkOperatorNode("concat", b, a)));
            components.add(mkOperatorNode("star", mkOperatorNode("concat", b, a)));
            components.add(mkOperatorNode("concat", b, a));
            Node arg2 = mkSketchNode(components);

            Node ret = mkOperatorNode("concat", a, arg2);
            ((Nodes.OperatorNode) ret).special = special;

            return ret;
        } else {

            DSL.Symbol sym = this.grammar.nameToSymbol.get(operatorName);
            assert (sym instanceof DSL.OpNonterminalSymbol);

            Node ret = new Nodes.OperatorNode(this.nodeId++, operatorName, (DSL.OpNonterminalSymbol) sym, args);
            ((Nodes.OperatorNode) ret).special = special;

            this.nodes.add(ret);
            return ret;
        }

    }

    public Node mkSketchNode(Set<Node> components) {
        Node ret = new Nodes.SketchNode(this.nodeId++, components);

        return ret;
    }

    public Node mkRepSketchNode(Node sketch, int sid) {
        Node ret = new Nodes.RepSketchNode(this.nodeId++, sketch, sid);

        return ret;
    }

    public RegexProgram getRegex() {
        RegexProgram rp = new RegexProgram(0);

        startNode.getRegex(rp);

        return rp;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        return this.startNode.toString();
    }

}
