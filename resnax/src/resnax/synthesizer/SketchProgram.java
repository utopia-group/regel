package resnax.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import resnax.synthesizer.DSL.CFG;
import resnax.synthesizer.DSL.Symbol;
import resnax.synthesizer.DSL.OpNonterminalSymbol;
import resnax.synthesizer.Nodes.RealConstantTerminalNode;
import resnax.synthesizer.Nodes.NullaryTerminalNode;
import resnax.synthesizer.Nodes.OperatorNode;
import resnax.synthesizer.Nodes.SketchNode;
import resnax.synthesizer.Nodes.RepSketchNode;

// AST for a refinement sketch program
public class SketchProgram {

  public boolean parseError;

  public List<Node> nodes;
  public final CFG grammar;

  public Map<Integer, Node> repSketches = new HashMap<>();

  public Node startNode;

  private int nodeId;       // TODO: i think nodeid does not matter so i will ignore this

  public SketchProgram(CFG grammar) {
    this.nodes = new ArrayList<>();
    this.grammar = grammar;
    this.nodeId = 0;
  }

  public Node mkConstantNode(int k) {
    Node ret = new RealConstantTerminalNode(this.nodeId++, this.grammar.nameToSymbol.get("k"), k);
    this.nodes.add(ret);
    return ret;
  }

  public Node mkTerminalNode(String terminalName) {
    Node ret = new NullaryTerminalNode(this.nodeId++, this.grammar.nameToSymbol.get(terminalName));
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
      ((OperatorNode) ret).special = special;

      return ret;
    } else {

      Symbol sym = this.grammar.nameToSymbol.get(operatorName);
      assert (sym instanceof OpNonterminalSymbol);

      Node ret = new OperatorNode(this.nodeId++, operatorName, (OpNonterminalSymbol) sym, args);
      ((OperatorNode) ret).special = special;

      this.nodes.add(ret);
      return ret;
    }

  }

  public Node mkSketchNode(Set<Node> components) {
    Node ret = new SketchNode(this.nodeId++, components);

    return ret;
  }

  public Node mkRepSketchNode(Node sketch, int sid) {
    Node ret = new RepSketchNode(this.nodeId++, sketch, sid);

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
