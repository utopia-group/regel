package resnax.synthesizer;

import java.io.Serializable;
import java.util.*;

import com.microsoft.z3.Model;

import resnax.EvalResult;
import resnax.Evaluate;
import resnax.Main;
import resnax.synthesizer.DSL.CFG;
import resnax.synthesizer.DSL.OpNonterminalSymbol;
import resnax.synthesizer.DSL.Symbol;
import resnax.synthesizer.Nodes.ConstantTerminalNode;
import resnax.synthesizer.Nodes.RealConstantTerminalNode;
import resnax.synthesizer.Nodes.SymbolicConstantTerminalNode;
import resnax.synthesizer.Nodes.NullaryTerminalNode;
import resnax.synthesizer.Nodes.OperatorNode;
import resnax.synthesizer.Nodes.VariableNode;
import resnax.synthesizer.Nodes.RepSketchNode;
import resnax.synthesizer.solver.Expression;
import resnax.synthesizer.solver.SolverNode;
import resnax.synthesizer.solver.SolverNodes;

// AST for a partial program might contain variables
@SuppressWarnings("Duplicates")
public class PartialProgram implements Serializable {

  public final CFG grammar;

  public Node startNode;

  public List<Node> nodes;
  public int nodeId;

  public int max_depth;

  public List<VariableNode> varNodes;
  //  public List<SymbolicConstantTerminalNode> symbolicConstantNodes;
  public Map<String, SymbolicConstantTerminalNode> symbolicConstantNodes;

  public Map<Integer, Nodes.RepSketchNode> repSketchNodeMap;

  public int symbolicConstantId;

  public int numRefinementSketch; // number of variable node which its refinement sketch is a sketch with component
  public int numOperatorSketch;   // number of variable node which its refinement sketch starts with a operator node or a terminal node
  public int numNullaryTerminals; // number of nullary terminal nodes in this partial program, we will do pruning once it has more than zero terminal nodes

  public int numRepeat;

  public boolean containsAndOrNot = false;

  public Node selectedVar;

  private int selectVarHelp;

  public PartialProgram(CFG grammar) {
    this.nodes = new ArrayList<>();
    this.varNodes = new ArrayList<>();
    this.symbolicConstantNodes = new HashMap<>();
    this.repSketchNodeMap = new HashMap<>();
    this.grammar = grammar;
    this.nodeId = 0;
    this.symbolicConstantId = 0;
    this.numRepeat = 0;
    this.numRefinementSketch = 0;
    this.numOperatorSketch = 0;
    this.selectVarHelp = 0;
    this.numNullaryTerminals = 0;
    this.max_depth = 0;
  }

  // deep copy a PartialProgram
  public PartialProgram(PartialProgram oldPP) {

    this.nodes = new ArrayList<>();
    this.varNodes = new ArrayList<>();
    this.symbolicConstantNodes = new HashMap<>();
    this.repSketchNodeMap = new HashMap<>();

//    this.startNode = null;
    this.nodeId = 0;
    this.symbolicConstantId = 0;
    this.grammar = oldPP.grammar;

    this.startNode = copyNodes(oldPP.startNode, null);
    this.numRepeat = oldPP.numRepeat;
    this.numNullaryTerminals = oldPP.numNullaryTerminals;

    this.numRefinementSketch = oldPP.numRefinementSketch;
    this.numOperatorSketch = oldPP.numOperatorSketch;
    this.selectVarHelp = oldPP.selectVarHelp;

    this.containsAndOrNot = oldPP.containsAndOrNot;

    this.max_depth = oldPP.max_depth;

    assert (this.startNode != null);
  }

  private Node copyNodes(Node curr, Node parent) {

//    System.out.println("curr in pp:" + curr);

    Node retNode;

    if (curr instanceof NullaryTerminalNode) {

      retNode = mkTerminalNode(((NullaryTerminalNode) curr).sym.name, parent);
      return retNode;

    } else if (curr instanceof RealConstantTerminalNode) {

      retNode = mkRealConstantNode(((RealConstantTerminalNode) curr).k, parent);
      return retNode;

    } else if (curr instanceof SymbolicConstantTerminalNode) {

      retNode = mkSymbolicConstantNode(parent);
      return retNode;

    } else if (curr instanceof VariableNode) {

      VariableNode v = (VariableNode) curr;

      retNode = mkVarNode(v.sketch, parent, v.selected, v.freeVar, v.containNot, v.containRepeat, v.depth);

      if (v.selected == 1) {this.selectedVar = retNode;}

      return retNode;

    } else if (curr instanceof OperatorNode) {

      OperatorNode currOp = (OperatorNode) curr;
      Node[] args = new Node[currOp.args.size()];
      OperatorNode newOpNode = mkOperatorNode(currOp.opSymbol, parent, args);

      for (int i = 0; i < args.length; i++) {
        newOpNode.args.set(i, copyNodes(currOp.args.get(i), newOpNode));
      }

      return newOpNode;

    } else if (curr instanceof RepSketchNode) {

      RepSketchNode rep = (RepSketchNode) curr;

      // check if hashmap already exist
      if (repSketchNodeMap.containsKey(rep.sid)) {
        return repSketchNodeMap.get(rep.sid);
      } else {

        RepSketchNode new_rep = (RepSketchNode) mkRepSketchNode(rep.sketch, rep.sid, parent);
        Node expand_new = copyNodes(rep.expand, new_rep);

        new_rep.expand = expand_new;

        repSketchNodeMap.put(new_rep.sid, new_rep);

        return new_rep;
      }

    } else {
      throw new RuntimeException();
    }

  }

  public boolean checkDuplicate() {

    // TODO: do only a depth-1 duplicate check
    // THIS IS FOR MODE ONLY
    if (this.startNode instanceof OperatorNode) {
      OperatorNode opNode = (OperatorNode) this.startNode;
      if (opNode.operatorName.equals("or") || opNode.operatorName.equals("and") || opNode.operatorName.equals("concat")) {
        if (opNode.args.get(0).toString().equals(opNode.args.get(1).toString())) return true;
      }
    }
    return false;
  }

  public VariableNode dr_selectVar() {

    VariableNode selectedVar = this.varNodes.get(0);

    selectedVar.selected = 1;
    this.selectedVar = selectedVar;
    return selectedVar;

  }

  // make a variable node with no sketch
  public Node mkVarNode(Node parent, boolean containNot, boolean containRepeat, int depth) {
    VariableNode ret = new VariableNode(this.nodeId++, parent, null, containNot, containRepeat, depth);
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    this.varNodes.add(ret);

    if (this.max_depth < depth) this.max_depth = depth;

    return ret;
  }

  // general situation of making a variable node with sketch
  public Node mkVarNode(Node sketch, Node parent, int depth) {
    VariableNode ret = new VariableNode(this.nodeId++, parent, sketch, depth);
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    this.varNodes.add(ret);
    if (sketch != null) {
      if (sketch instanceof Nodes.SketchNode) {
        if (((Nodes.SketchNode) sketch).containsComponents()) {this.numRefinementSketch++;}     // a sketch node with components
      } else {
        this.numRefinementSketch++;   // not sketch node so it must be a oepratornode or terminal node
      }
    }
    if (sketch instanceof OperatorNode || sketch instanceof Nodes.TerminalNode) {this.numOperatorSketch++;}

    if (this.max_depth < depth) this.max_depth = depth;

    return ret;
  }

  // make a Variable node which its under a not operator
  public Node mkVarNode(Node sketch, Node parent, boolean containNot, boolean containRepeat, int depth) {
    VariableNode ret = new VariableNode(this.nodeId++, parent, sketch, containNot, containRepeat, depth);
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    this.varNodes.add(ret);
    if (sketch != null) {
      if (sketch instanceof Nodes.SketchNode) {
        if (((Nodes.SketchNode) sketch).containsComponents()) {this.numRefinementSketch++;}
      } else {
        this.numRefinementSketch++;
      }
    }
    if (sketch instanceof OperatorNode || sketch instanceof Nodes.TerminalNode) {this.numOperatorSketch++;}

    if (this.max_depth < depth) this.max_depth = depth;

    return ret;
  }

  // this is used only when COPY NODE
  public Node mkVarNode(Node sketch, Node parent, int selected, boolean freeVar, boolean containNot, boolean containRepeat, int depth) {
    VariableNode ret = new VariableNode(this.nodeId++, parent, sketch, selected, freeVar, containNot, containRepeat, depth);
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    this.varNodes.add(ret);
    return ret;
  }

  public Node mkRepSketchNode(Node sketch, int sid, Node parent) {
    Node ret = new RepSketchNode(this.nodeId++, sketch, sid, parent);
    this.nodes.add(ret);
    return ret;

  }

  public Node mkRealConstantNode(int k, Node parent) {

    Node ret = new RealConstantTerminalNode(this.nodeId++, parent, this.grammar.nameToSymbol.get("k"), k);
    assert (startNode != null);
    this.nodes.add(ret);
    return ret;
  }

  public Node mkSymbolicConstantNode(Node parent) {

    SymbolicConstantTerminalNode ret = new SymbolicConstantTerminalNode(this.nodeId++, this.symbolicConstantId++, parent,
        this.grammar.symbolicConstantSym);
    assert (startNode != null);
    this.nodes.add(ret);
    this.symbolicConstantNodes.put(ret.getName(), ret);
    return ret;

  }

  public Node mkTerminalNode(String terminalName, Node parent) {
    Node ret = new NullaryTerminalNode(this.nodeId++, parent, this.grammar.nameToSymbol.get(terminalName));
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    return ret;

  }

  public OperatorNode mkOperatorNode(OpNonterminalSymbol sym, Node parent, Node... args) {

    if (sym.name.equals("and") || sym.name.equals("not")) this.containsAndOrNot = true;

    OperatorNode ret = new OperatorNode(this.nodeId++, parent, sym.prod.operatorName, sym, args);
    if (startNode == null) this.startNode = ret;
    this.nodes.add(ret);
    return ret;
  }

  public void substituteVar(Node var, Node add) {
    substituteVar(var, add, false);
  }

  // substitute the previous variable node v with a new created node add
  public void substituteVar(Node var, Node add, boolean minusOperatorSketch) {

    assert (var instanceof VariableNode);

    VariableNode v = (VariableNode) var;

    // check if the var node has any parent

    if (var.parent == null) {

      this.nodes.remove(var);
      this.varNodes.remove(var);
      this.startNode = add;

      // update some field parameter
      updateNumRefinementSketch(v.sketch);
      updateNumTerminalNode(add);
      if (minusOperatorSketch) {this.numOperatorSketch--;}

    } else if (var.parent instanceof OperatorNode) {

      assert (var.parent instanceof OperatorNode);
      OperatorNode parentNode = (OperatorNode) var.parent;

      int addIndex = parentNode.args.indexOf(var);
//      System.out.println("addIndex:" + addIndex);
      assert (addIndex != -1);
      Symbol check = parentNode.opSymbol.prod.argumentSymbols[addIndex];

      if (check.name.equals("r")) {
        assert (add instanceof OperatorNode | add instanceof NullaryTerminalNode | add instanceof RepSketchNode);
      } else if (check.name.equals("k")) {
        assert (add instanceof ConstantTerminalNode);
      } else if (this.grammar.terminalSymbols.contains(check)) {
        assert (add instanceof NullaryTerminalNode);
      }

      parentNode.args.set(addIndex, add);
      this.nodes.remove(v);
      this.varNodes.remove(v);

      // update some field parameter
      updateNumRefinementSketch(v.sketch);
      updateNumTerminalNode(add);
      if (minusOperatorSketch) {this.numOperatorSketch--;}

      if (add instanceof OperatorNode) {
        OperatorNode addOp = (OperatorNode) add;
        if (addOp.operatorName.equals("not") || addOp.operatorName.equals("and")) {
          this.containsAndOrNot = true;
        }
      }
    } else if (var.parent instanceof RepSketchNode) {

      this.nodes.remove(var);
      this.varNodes.remove(var);
      // this.startNode = add;
      ((RepSketchNode) var.parent).expand = add;

      // update some field parameter
      updateNumRefinementSketch(v.sketch);
      updateNumTerminalNode(add);
      if (minusOperatorSketch) {
        this.numOperatorSketch--;
      }

      if (add instanceof OperatorNode) {
        OperatorNode addOp = (OperatorNode) add;
        if (addOp.operatorName.equals("not") || addOp.operatorName.equals("and")) {
          this.containsAndOrNot = true;
        }
      }

    }

  }

  private void updateNumRefinementSketch(Node sk) {
    if (sk != null) {
      if (sk instanceof Nodes.SketchNode) {
        if (((Nodes.SketchNode) sk).containsComponents()) this.numRefinementSketch--;
      } else {
        this.numRefinementSketch--;
      }
    }
  }

  private void updateNumTerminalNode(Node add) {

    if (add instanceof NullaryTerminalNode) {
      this.numNullaryTerminals++;
    }
  }

  public VariableNode selectVar() {

    int selected = 0;

    int ns = this.varNodes.size();
    if (this.selectVarHelp >= ns) {
      this.selectVarHelp = 0;
      selected = 0;
    } else {
      selected = this.selectVarHelp;
      this.selectVarHelp++;
    }

    VariableNode selectedVar = this.varNodes.get(selected);
    selectedVar.selected = 1;
    this.selectedVar = selectedVar;
    return selectedVar;
  }

  public void deselectVar() {
    ((VariableNode) this.selectedVar).selected = 0;
  }

  public VariableNode findSelectedVar() {
    return (VariableNode) this.selectedVar;
  }

  public RegexProgram getApproximate(List<SymbolicConstantTerminalNode> addedConstants) {
    RegexProgram rp = new RegexProgram(1);
    rp.assignAddedConstants(addedConstants);

    if (this.isCompleteSymbolic()) {rp.isCompleteSymbolic = true;}

    startNode.getApproximate(rp, false, false);

    return rp;

  }

  public RegexProgram getApproximate() {

    RegexProgram rp = new RegexProgram(1);

    if (this.isCompleteSymbolic()) {rp.isCompleteSymbolic = true;}

    startNode.getApproximate(rp, false, false);

    return rp;
  }

  public RegexProgram getRegex() {

    RegexProgram rp = new RegexProgram(0);

    startNode.getRegex(rp);

    return rp;
  }

  // workflow of this: given a solved model, assign the value iteratively from leaf to root
  // each time assigning the value, call the approx evaluate function
  public boolean evaluateSolvedProgram(Expression ex, Model m, Evaluate e, List<OperatorNode> repeatQueue) {

//    // Queue to add the repeat nodes
//    // make sure: the first one is the one closest to the leaf of the tree
//    // TODO: need to check this...
//    List<OperatorNode> repeatQueue = new ArrayList<>();
//    this.startNode.addRepeatNodes(repeatQueue);

//    if (Main.SOLVER_DEBUG == 1) System.out.println(repeatQueue);

//    int[] addedConstants = new int[this.symbolicConstantId];
    List<SymbolicConstantTerminalNode> addedConstants = new ArrayList<>();

    for (int i = 0; i < repeatQueue.size(); i++) {

      OperatorNode curr = repeatQueue.get(i);

      assert curr.operatorName.contains("repeat");

      // assign the current one with model's output value
      for (int j = 1; j < curr.args.size(); j++) {
        SymbolicConstantTerminalNode sc = (SymbolicConstantTerminalNode) curr.args.get(j);
        sc.value = Integer.parseInt(m.getConstInterp(sc.scNode.z3symbol).toString());
//        addedConstants[sc.id] = 1;
        addedConstants.add(sc);
      }

      if (i == (repeatQueue.size() - 1)) {

        RegexProgram p = this.getRegex();
        if (Main.SOLVER_DEBUG == 1) System.out.println("evalaute solved 2: " + p);
        EvalResult evalRes = e.evaluate(p);

        if (evalRes.result) {
          if (Main.SOLVER_DEBUG == 1) System.out.println("evaluate solved match");
          return true;
        } else {
          if (Main.SOLVER_DEBUG == 1) System.out.println("evaluate solved not match");
          ex.addConstantConstraint(evalRes.reason, addedConstants);
//          if (Main.SOLVER_DEBUG == 1) System.out.println("boolean expression added constraint:" + ex.boolExpression.toString());
          if (Main.SOLVER_DEBUG == 1) System.out.println("boolean expression added constraint:" + ex.currExpression.toString());
          if (Main.SOLVER_DEBUG == 1) System.out.println("evaluate solved not match 2");
          return false;
        }

      } else {

        RegexProgram p = this.getApproximate(addedConstants);
        if (Main.SOLVER_DEBUG == 1) System.out.println("evalaute approx solved 2: " + p);
        EvalResult evalRes = e.evaluateApprox(p);

//        assert false;

        if (evalRes.result) {
          continue;
        } else {
          if (Main.SOLVER_DEBUG == 1) System.out.println("evaluate approx solved not match");
          ex.addConstantConstraint(evalRes.reason, addedConstants);
//          if (Main.SOLVER_DEBUG == 1) System.out.println("boolean expression added partial constraint" + ex.boolExpression.toString());
          if (Main.SOLVER_DEBUG == 1) System.out.println("boolean expression added partial constraint" + ex.currExpression.toString());
          return false;
        }
      }

    }

    // should return before this ...
    throw new RuntimeException();

  }

  public List<String> generateNotPrunedSubsumeProgram() {

    // TODO: bug here since numOperatorSketch may calculate the wrong info, does not affect accuracy
    if (this.varNodes.isEmpty()) {
      System.out.println("WARNING: " + this.numOperatorSketch + " " + this.toString());
      return new ArrayList<>();
    }
//      assert (!this.varNodes.isEmpty()) : this.numOperatorSketch + " " + this.toString();
    List<String> res = this.startNode.generateSubsumeNotPrunedProgram(0);
    return res;
  }

  public List<String> generatePrunedSubsumeProgram() {

    // TODO: bug here since numOperatorSketch may calculate the wrong info, does not affect accuracy
    if (this.varNodes.isEmpty()) {
      System.out.println("WARNING: " + this.numOperatorSketch + " " + this.toString());
      return new ArrayList<>();
    }

//    assert (!this.varNodes.isEmpty()) : this.numOperatorSketch + " " + this.toString();

    List<String> res = this.startNode.generateSubsumePrunedProgram(0);
//    if (Main.DEBUG == 1) System.out.println("added subsume program: " + res);
    return res;

  }

  public Expression generateExpression() {

    Expression e = new Expression(this.nodeId, this.startNode.getsNode(), symbolicConstantNodes);

    SolverNode s = this.startNode.generateExpression(e);

//    System.out.println("s:" + s);

    if (s == null) return null;

    // the root might not have the
    if (s instanceof SolverNodes.ArithmeticNode || s instanceof SolverNodes.IntegerSolverNode
        || s instanceof SolverNodes.SymbolicConstantSolverNode) {
      s = new SolverNodes.PropositionNode(Expression.expressionGrammar.nameToProduction.get("eq"), this.startNode.getsNode(), s);
    }
    if (s instanceof SolverNodes.ConnectiveNode) {
      SolverNode l = ((SolverNodes.ConnectiveNode) s).left;
//      System.out.println(l);
      if (l instanceof SolverNodes.ArithmeticNode || l instanceof SolverNodes.IntegerSolverNode
          || l instanceof SolverNodes.SymbolicConstantSolverNode) {
        SolverNode p = new SolverNodes.PropositionNode(Expression.expressionGrammar.nameToProduction.get("eq"), this.startNode.getsNode(), l);
        ((SolverNodes.ConnectiveNode) s).left = p;
      }
    }

    return e.init_expression(s);
  }

  public boolean isCompleteSymbolic() {
    return varNodes.isEmpty() && !symbolicConstantNodes.isEmpty();
  }

  @Override public int hashCode() {
    throw new RuntimeException();
  }

  @Override public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    throw new RuntimeException();
  }

  @Override public String toString() {
    return startNode.toString();
  }

}
