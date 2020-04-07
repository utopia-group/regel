package resnax.synthesizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import resnax.Main;
import resnax.synthesizer.DSL.OpNonterminalSymbol;
import resnax.synthesizer.DSL.Symbol;
import resnax.synthesizer.solver.Expression;
import resnax.synthesizer.solver.SolverNode;
import resnax.synthesizer.solver.SolverNodes;

@SuppressWarnings("Duplicates") public class Nodes {

  // only exist in PartialProgram
  public static class VariableNode extends Node {

    public Node sketch;
    public int selected;
    public boolean freeVar;
    public boolean containNot;
    public boolean containRepeat;   // repeat here means repeatexact, repeatatleast and repeatrange
    public int depth;

    public VariableNode(int id, Node parent, Node sketch, int depth) {
      this(id, parent, sketch, false, false, depth);
    }

    public VariableNode(int id, Node parent, Node sketch, boolean containNot, boolean containRepeat, int depth) {
      super(id, parent);
      if (sketch == null) {
        this.sketch = new SketchNode();
        this.freeVar = true;
      } else {
        this.sketch = sketch;
        this.freeVar = false;
      }
      this.selected = 0;
      this.containNot = containNot;
      this.containRepeat = containRepeat;
      this.depth = depth;
    }

    public VariableNode(int id, Node parent, Node sketch, int selected, boolean freeVar, boolean containNot, boolean containRepeat, int depth) {
      super(id, parent);
      this.sketch = sketch;
      this.selected = selected;
      this.freeVar = freeVar;
      this.containNot = containNot;
      this.containRepeat = containRepeat;
      this.depth = depth;
    }

    @Override public boolean equals(Object obj) {
      return this.id == ((Node) obj).id;
    }

    @Override public String toString() {
//      System.out.println("this.sketch:" + this.sketch);
      return "v_" + this.id + ":" + this.sketch;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.toString());
      return b;
    }

    @Override public List<String> generateSubsumePrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();

      ret.add(this.toString());

      return ret;
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();

      ret.add(this.toString());

      return ret;
    }

    @Override public SolverNode generateExpression(Expression e) {
      throw new RuntimeException();
    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      throw new RuntimeException();
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      throw new RuntimeException();
    }

    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {

      if (sketch instanceof TerminalNode) {

        assert (sketch instanceof NullaryTerminalNode);

        ((NullaryTerminalNode) sketch).getApproximate(rp, onlyOverApprox, onlyUnderApprox);

//        // TODO: assume Variable node does not include Constant SolverNode
//        TerminalNode tn = (TerminalNode) sketch;
//        assert (tn.sym instanceof DSL.NullaryTerminalSymbol);
//
//        if (onlyOverApprox) {
//          if (tn.sym.name.equals("<num1-9>")) {
//            rp.programs[0].append("[0-9]");
//          }
////        else if (tn.sym.name.equals("<0>")) {
////          rp.programs[0].append("[0-9]");
////        }
//          else {
//            rp.programs[0].append(((DSL.NullaryTerminalSymbol) tn.sym).pattern);
//          }
//
//        } else if (onlyUnderApprox) {
//          rp.programs[1].append(((DSL.NullaryTerminalSymbol) tn.sym).pattern);
//
//        } else {
//          if (tn.sym.name.equals("<num1-9>")) {
//            rp.programs[0].append("[0-9]");
//          }
////        else if (tn.sym.name.equals("<0>")) {
////          rp.programs[0].append("[0-9]");
////        }
//          else {
//            rp.programs[0].append(((DSL.NullaryTerminalSymbol) tn.sym).pattern);
//          }
//
//          rp.programs[1].append(((DSL.NullaryTerminalSymbol) tn.sym).pattern);
//
//        }

      } else if (sketch instanceof OperatorNode) {

        OperatorNode on = (OperatorNode) sketch;
        on.getApproximate(rp, onlyOverApprox, onlyUnderApprox);

      } else if (sketch instanceof SketchNode) {

        SketchNode sk = (SketchNode) sketch;

        if (onlyOverApprox) {
          rp.programs[0].append("(");

          int i = 1;
          for (Node n : sk.components) {
            n.getApproximate(rp, onlyOverApprox, false);
            if (i < sk.components.size()) {
              rp.programs[0].append("|");
            }
            i++;
          }

          rp.programs[0].append(")");
        } else if (onlyUnderApprox) {
          rp.programs[1].append("(#)");
        } else {
          rp.programs[0].append("(");

          int i = 1;
          for (Node n : sk.components) {
            n.getApproximate(rp, onlyOverApprox, false);
            if (i < sk.components.size()) {
              rp.programs[0].append("|");
            }
            i++;
          }

          rp.programs[0].append(")");
          rp.programs[1].append("(#)");

        }

//        if (onlyOverApprox) {
//          rp.programs[0].append("(.*)");
//        } else if (onlyUnderApprox) {
//          rp.programs[1].append("(#)");
//        } else {
//          rp.programs[0].append("(.*)");
//          rp.programs[1].append("(#)");
//        }
      } else if (sketch instanceof RepSketchNode) {

        if (onlyOverApprox) {
          rp.programs[0].append("(.*)");
        } else if (onlyUnderApprox) {
          rp.programs[1].append("(#)");
        } else {
          rp.programs[0].append("(.*)");
          rp.programs[1].append("(#)");
        }
      }

      return rp;
    }

    @Override public RegexProgram getRegex(RegexProgram rp) {
      throw new RuntimeException();
    }

  }

  public static class OperatorNode extends Node {

    public boolean special = false;   // if this is a special repeatatleast node, should be false unless modified

    public final String operatorName;
    public final OpNonterminalSymbol opSymbol;
    public List<Node> args; // TODO: this field is modifiable in PartialProgram, not in SketchProgram

    public OperatorNode(int id, String operatorName, OpNonterminalSymbol opSymbol, Node[] args) {
      super(id, Expression.mkLengthSolverNode(id));
      this.operatorName = operatorName;
      this.opSymbol = opSymbol;
      this.args = new ArrayList<>(Arrays.asList(args));
    }

    public OperatorNode(int id, Node parent, String operatorName, OpNonterminalSymbol opSymbol, Node[] args) {
      super(id, parent, Expression.mkLengthSolverNode(id));
      this.operatorName = operatorName;
      this.opSymbol = opSymbol;
      this.args = new ArrayList<>(Arrays.asList(args));
    }

    @Override public String toString() {
      return toStringBuilder(new StringBuilder(Main.STRINGBUILDER_CAPACITY)).toString();
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(operatorName);
      b.append("(");
      for (int i = 0; i < args.size(); i++) {
        if (i > 0) b.append(",");
        args.get(i).toStringBuilder(b);
      }
      b.append(")");

      return b;
    }

    //
    // TODO: subsumption has not considered anything related to the symbolic constant

    @Override public List<String> generateSubsumePrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();
      ret.add(this.toString());

      if (depth > Main.SUBSUMPTION_DEPTH) {
        return ret;
      }

      // TODO: startwith(<let>) rejected then concat(<let>,....) could be pruned, similar with endwith
      switch (this.operatorName) {

      case "startwith": {

        List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("concat(" + s + "," + "v:?)");
          ret.add("startwith(" + s + ")");
        }

        // TODO: maybe enable this???
//        for (DSL.TerminalSymbol sym : this.grammar.terminalSymbols) {
//          if (sym instanceof DSL.NullaryTerminalSymbol) ret.add("concat(" + arg + "," + ((DSL.NullaryTerminalSymbol) sym).pattern + ")");
//        }

        break;
      }
      case "endwith": {

        List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("concat(v:?," + s + ")");
          ret.add("endwith(" + s + ")");
        }

        // TODO: maybe enable this???
//        for (DSL.TerminalSymbol sym : this.grammar.terminalSymbols) {
//          if (sym instanceof DSL.NullaryTerminalSymbol) ret.add("concat(" + ((DSL.NullaryTerminalSymbol) sym).pattern + "," + arg + ")");
//        }

        break;
      }
      case "contain": {
        // contain(...) rejected then sw(...) and ew(...) also could be rejected
        List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("contain(" + s + ")");
          ret.add("startwith(" + s + ")");
          ret.add("endwith(" + s + ")");
          ret.add("concat(" + "v:?," + s + ")");
          ret.add("concat(" + s + ",v:?)");
        }

        // since subsume program contains startwith/endwith, should also trigger pruning program subsume by them
//        for (DSL.TerminalSymbol sym : this.grammar.terminalSymbols) {
//          if (sym instanceof DSL.NullaryTerminalSymbol) {
//            ret.add("concat(" + ((DSL.NullaryTerminalSymbol) sym).pattern + "," + arg + ")");
//            ret.add("concat(" + arg + "," + ((DSL.NullaryTerminalSymbol) sym).pattern + ")");
//          }
//        }
        break;
      }
      case "not": {
        break;
      }
      case "notcc": {
        break;
      }

      case "optional": {

        List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("repeat(" + s + ",1)");
          ret.add("optional(" + s + ")");
        }
        break;
      }
      case "star": {
        List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);

        // get k range
        for (String s : arg) {
          ret.add("star(" + s + ")");
//          for (int k1 = Main.K_MIN; k1 <= Main.K_MAX; k1++) {
//            ret.add("repeat(" + s + "," + k1 + ")");
//            ret.add("repeatatleast(" + s + "," + k1 + ")");
//            for (int k2 = k1 + 1; k2 <= Main.K_MAX; k2++) {
//              if (k1 == k2) continue;
//              ret.add("repeatrange(" + s + "," + k1 + "," + k2 + ")");
//            }
//          }
        }
        break;
      }
      case "repeat": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {
          List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
          int k = ((RealConstantTerminalNode) arg1).k;

          for (String s : arg) {
            ret.add("repeat(" + s + "," + k + ")");
          }

        }

        break;
      }
      case "repeatatleast": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {

          List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
          int k = ((RealConstantTerminalNode) arg1).k;

          if (k >= Main.K_MIN & k <= Main.K_MAX) {
//            for (String s : arg) {
//              for (int k1 = k; k1 <= Main.K_MAX; k1++) {
//                ret.add("repeat(" + s + "," + k1 + ")");
//                ret.add("repeatatleast(" + s + "," + k1 + ")");
            ret.add("repeatatleast(" + s + "," + k + ")");

//                for (int k2 = k1 + 1; k2 <= Main.K_MAX; k2++) {
//                  if (k2 == k1) {continue;}
//                  ret.add("repeatrange(" + s + "," + k1 + "," + k2 + ")");
//                }
//              }
//            }
          }
        }

        break;
      }

      case "repeatrange": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);
        ConstantTerminalNode arg2 = (ConstantTerminalNode) this.args.get(2);

        // assumption: arg1 and arg2 should both be real constant value or both not
        if (arg1 instanceof RealConstantTerminalNode && arg2 instanceof RealConstantTerminalNode) {

          List<String> arg = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
          int k1 = ((RealConstantTerminalNode) arg1).k;
          int k2 = ((RealConstantTerminalNode) arg2).k;

          if (k1 >= Main.K_MIN & k2 <= Main.K_MAX) {
            for (String s : arg) {
//              for (int kn1 = k1; kn1 < k2; kn1++) {
//                ret.add("repeat(" + s + "," + kn1 + ")");

//                for (int kn2 = kn1 + 1; kn2 <= k2; kn2++) {
//                  if (kn1 == kn2) continue;
//                  ret.add("repeatrange(" + s + "," + kn1 + "," + kn2 + ")");
              ret.add("repeatrange(" + s + "," + k1 + "," + k2 + ")");
//                }
//              }
            }
          }

        }

        break;
      }

      case "concat": {

        List<String> arg1 = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
        List<String> arg2 = this.args.get(1).generateSubsumePrunedProgram(depth + 1);

        for (String s1 : arg1) {

          for (String s2 : arg2) {

            ret.add("concat(" + s1 + "," + s2 + ")");
          }
        }

        break;
      }
      case "and": {
//        List<String> arg1 = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
//        List<String> arg2 = this.args.get(1).generateSubsumePrunedProgram(depth + 1);

//        for (String s1 : arg1) {
//          for (String s2 : arg2) {
//            ret.add("and(" + s1 + "," + s2 + ")");
//            ret.add("and(" + s2 + "," + s1 + ")");
//          }
//        }

        break;
      }
      case "or": {
        List<String> arg1 = this.args.get(0).generateSubsumePrunedProgram(depth + 1);
        List<String> arg2 = this.args.get(1).generateSubsumePrunedProgram(depth + 1);

        for (String s1 : arg1) {
          for (String s2 : arg2) {
            ret.add("or(" + s1 + "," + s2 + ")");
            ret.add("or(" + s2 + "," + s1 + ")");
//            ret.add("and(" + s1 + "," + s2 + ")");
//            ret.add("and(" + s2 + "," + s1 + ")");
          }
        }
        break;
      }
      default:
        throw new RuntimeException();
      }

      return ret;
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();
      ret.add(this.toString());

      if (depth > Main.SUBSUMPTION_DEPTH) {
        return ret;
      }

      switch (this.operatorName) {

      case "startwith": {

        List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("contain(" + s + ")");
          ret.add("startwith(" + s + ")");
        }

        break;
      }
      case "endwith": {

        List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("contain(" + s + ")");
          ret.add("endwith(" + s + ")");
        }

        break;
      }
      case "contain": {

        List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("contain(" + s + ")");
        }

        break;
      }
      case "not": {
        break;
      }
      case "notcc": {
        break;
      }
      case "optional": {

        List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("optional(" + s + ")");
          ret.add("star(" + s + ")");
        }
        break;
      }
      case "star": {
        List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s : arg) {
          ret.add("star(" + s + ")");
        }
        break;
      }
      case "repeat": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {

          List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
          int k = ((RealConstantTerminalNode) arg1).k;

          if (k >= Main.K_MIN && k <= Main.K_MAX) {
            for (String s : arg) {
              ret.add("repeat(" + s + ")");
//              for (int k1 = Main.K_MIN; k1 <= k; k1++) {
//                ret.add("repeatatleast(" + s + "," + k1 + ")");

//              for (int k2 = k; k2 <= Main.K_MAX; k2++) {
//                if (k1 == k2) continue;
//                ret.add("repeatrange(" +  s + "," + k1 + "," + k2 + ")");
//              }
//              }
            }
          }
        }

        break;
      }
      case "repeatatleast": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {

          List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
          int k = ((RealConstantTerminalNode) arg1).k;

          for (String s : arg) {
            ret.add("star(" + s + ")");
            ret.add("repeatatleast(" + s + "," + k + ")");
          }
        }

        break;
      }

      case "repeatrange": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);
        ConstantTerminalNode arg2 = (ConstantTerminalNode) this.args.get(2);

        if (arg1 instanceof RealConstantTerminalNode && arg2 instanceof RealConstantTerminalNode) {

          List<String> arg = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
          int k1 = ((RealConstantTerminalNode) arg1).k;
          int k2 = ((RealConstantTerminalNode) arg2).k;

          if (k1 >= Main.K_MIN & k2 <= Main.K_MAX) {
            for (String s : arg) {

//              for (int kn1 = Main.K_MIN; kn1 <= k1; kn1++) {
//                ret.add("repeatatleast(" + s + "," + k1 + ")");
//                ret.add("star(" + s + ")");
//                for (int kn2 = k2; kn2 <= Main.K_MAX; kn2++) {
//                  if (kn1 == kn2) continue;
//
//                  ret.add("repeatrange(" + s + "," + kn1 + "," + kn2 + ")");
              ret.add("repeatrange(" + s + "," + k1 + "," + k2 + ")");
//                }
//              }

            }
          }

        }
        break;
      }

      case "concat": {
        List<String> arg1 = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
        List<String> arg2 = this.args.get(1).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s1 : arg1) {
          for (String s2 : arg2) {
            ret.add("concat(" + s1 + "," + s2 + ")");
            ret.add("startwith(" + s1 + ")");
            ret.add("endwith(" + s2 + ")");
          }
        }

        break;
      }
      case "and": {
        List<String> arg1 = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
        List<String> arg2 = this.args.get(1).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s1 : arg1) {
          for (String s2 : arg2) {
            ret.add("and(" + s1 + "," + s2 + ")");
            ret.add("and(" + s2 + "," + s1 + ")");
            ret.add("or(" + s1 + "," + s2 + ")");
            ret.add("or(" + s2 + "," + s1 + ")");
          }
        }

        break;
      }
      case "or": {
        List<String> arg1 = this.args.get(0).generateSubsumeNotPrunedProgram(depth + 1);
        List<String> arg2 = this.args.get(1).generateSubsumeNotPrunedProgram(depth + 1);

        for (String s1 : arg1) {
          for (String s2 : arg2) {
            ret.add("or(" + s1 + "," + s2 + ")");
            ret.add("or(" + s2 + "," + s1 + ")");
          }
        }

        break;
      }
      default:
        throw new RuntimeException();
      }

      return ret;
    }

    @Override public SolverNode generateExpression(Expression e) {

//      System.out.println("opname:" + operatorName);

      // not is special
      if (operatorName.equals("not")) {
        return this.opSymbol.prod.exec_f(s);
      }

      SolverNode arg1Expression = this.args.get(0).generateExpression(e);

//      System.out.println("arg1exp:" + arg1Expression);

      if (arg1Expression == null) return null;

      SolverNode c;
      SolverNode p;

      if (opSymbol.prod.argumentSymbols.length == 1) {

        // propogate upwards does not happen with any of the one argument operators.
        // so c could only be ConnectiveNode

        if (arg1Expression instanceof SolverNodes.IntegerSolverNode || arg1Expression instanceof SolverNodes.ArithmeticNode
            || arg1Expression instanceof SolverNodes.SymbolicConstantSolverNode) {
//          System.out.println("1");
          p = this.opSymbol.prod.exec_f(s, arg1Expression);
          if (p == null) return null;
          c = p;
        } else if (arg1Expression instanceof SolverNodes.ConnectiveNode) {
//          System.out.println("2");
          if (((SolverNodes.ConnectiveNode) arg1Expression).left instanceof SolverNodes.ArithmeticNode) {
            p = this.opSymbol.prod.exec_f(s, ((SolverNodes.ConnectiveNode) arg1Expression).left);
            if (p == null) return null;
            c = Expression.mkConnectiveNode("and", p, ((SolverNodes.ConnectiveNode) arg1Expression).right);
          } else {
            p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode());
            if (p == null) return null;
            c = Expression.mkConnectiveNode("and", p, arg1Expression);
          }
        } else {
//          System.out.println("3");

          p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode());
          if (p == null) return null;
//          System.out.println("ag1expression:" + arg1Expression);
          c = Expression.mkConnectiveNode("and", p, arg1Expression);
        }

      } else if (opSymbol.prod.argumentSymbols.length == 2) {

        SolverNode arg2Expression = this.args.get(1).generateExpression(e);

        if (arg2Expression == null) return null;

        if (operatorName.contains("repeat")) {

          if (arg1Expression instanceof SolverNodes.IntegerSolverNode || arg1Expression instanceof SolverNodes.ArithmeticNode
              || arg1Expression instanceof SolverNodes.SymbolicConstantSolverNode) {

            p = this.opSymbol.prod.exec_f(s, arg1Expression, arg2Expression);
            if (p == null) return null;
            c = p;

            if (!(arg1Expression instanceof SolverNodes.IntegerSolverNode)) {
              e.Nonlinear = true;
            }

          } else {
            p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), arg2Expression);
            if (p == null) return null;
            c = Expression.mkConnectiveNode("and", p, arg1Expression);
            e.Nonlinear = true;
          }

        } else {

          // arg1: int-related
          if (arg1Expression instanceof SolverNodes.IntegerSolverNode || arg1Expression instanceof SolverNodes.ArithmeticNode
              || arg1Expression instanceof SolverNodes.SymbolicConstantSolverNode) {

            // arg2: int related
            if (arg2Expression instanceof SolverNodes.IntegerSolverNode || arg2Expression instanceof SolverNodes.ArithmeticNode
                || arg2Expression instanceof SolverNodes.SymbolicConstantSolverNode) {

              // arg1, arg2: int related
              p = this.opSymbol.prod.exec_f(s, arg1Expression, arg2Expression);
              if (p == null) return null;
              c = p;

            } else if (arg2Expression instanceof SolverNodes.ConnectiveNode) {

              // arg1: int-related, arg2: arithmetic
              if (((SolverNodes.ConnectiveNode) arg2Expression).left instanceof SolverNodes.ArithmeticNode) {

                p = this.opSymbol.prod.exec_f(s, arg1Expression, ((SolverNodes.ConnectiveNode) arg2Expression).left);
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, ((SolverNodes.ConnectiveNode) arg2Expression).right);

              } else {
                // arg1: int-related arg2: connective
                p = this.opSymbol.prod.exec_f(s, arg1Expression, this.args.get(1).getsNode());
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, arg2Expression);

              }
            } else {
              // arg1: int-related, arg2: all-other
              p = this.opSymbol.prod.exec_f(s, arg1Expression, this.args.get(1).getsNode());
              if (p == null) return null;
              c = Expression.mkConnectiveNode("and", p, arg2Expression);
            }

          } else if (arg1Expression instanceof SolverNodes.ConnectiveNode) {
            // arg1: arithmetic
            if (((SolverNodes.ConnectiveNode) arg1Expression).left instanceof SolverNodes.ArithmeticNode) {

              if (arg2Expression instanceof SolverNodes.ConnectiveNode) {
                // arg2: arithmetic
                if (((SolverNodes.ConnectiveNode) arg2Expression).left instanceof SolverNodes.ArithmeticNode) {
                  // arg1, arg2: arithmetic
                  p = this.opSymbol.prod
                      .exec_f(s, ((SolverNodes.ConnectiveNode) arg1Expression).left, ((SolverNodes.ConnectiveNode) arg2Expression).left);
                  if (p == null) return null;
                  c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", ((SolverNodes.ConnectiveNode) arg1Expression).right,
                      ((SolverNodes.ConnectiveNode) arg2Expression).right));
                } else {
                  // arg1: arithmetic, arg2: connective
                  p = this.opSymbol.prod.exec_f(s, ((SolverNodes.ConnectiveNode) arg1Expression).left, this.args.get(1).getsNode());
                  if (p == null) return null;
                  c = Expression.mkConnectiveNode("and", p,
                      Expression.mkConnectiveNode("and", ((SolverNodes.ConnectiveNode) arg1Expression).right, arg2Expression));
                }
              } else if (arg2Expression instanceof SolverNodes.IntegerSolverNode || arg2Expression instanceof SolverNodes.ArithmeticNode
                  || arg2Expression instanceof SolverNodes.SymbolicConstantSolverNode) {

                p = this.opSymbol.prod.exec_f(s, ((SolverNodes.ConnectiveNode) arg1Expression).left, arg2Expression);
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, ((SolverNodes.ConnectiveNode) arg1Expression).right);

              } else {
                // arg1: arithmetic, arg2: all other
//                System.out.println("arg2:" + arg2Expression);
                p = this.opSymbol.prod.exec_f(s, ((SolverNodes.ConnectiveNode) arg1Expression).left, this.args.get(1).getsNode());
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p,
                    Expression.mkConnectiveNode("and", ((SolverNodes.ConnectiveNode) arg1Expression).right, arg2Expression));
              }
            } else {
              if (arg2Expression instanceof SolverNodes.ConnectiveNode) {
                // arg2: arithmetic
                if (((SolverNodes.ConnectiveNode) arg2Expression).left instanceof SolverNodes.ArithmeticNode) {
                  // arg1 conn, arg2: arithmetic
                  p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), ((SolverNodes.ConnectiveNode) arg2Expression).left);
                  if (p == null) return null;
                  c = Expression.mkConnectiveNode("and", p,
                      Expression.mkConnectiveNode("and", arg1Expression, ((SolverNodes.ConnectiveNode) arg2Expression).right));
                } else {
                  // arg1: conn, arg2: connective
                  p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), this.args.get(1).getsNode());
                  if (p == null) return null;
                  c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", arg1Expression, arg2Expression));
                }
              } else if (arg2Expression instanceof SolverNodes.IntegerSolverNode || arg2Expression instanceof SolverNodes.ArithmeticNode
                  || arg2Expression instanceof SolverNodes.SymbolicConstantSolverNode) {
                // arg1: connn, arg2: int
                p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), arg2Expression);
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, arg1Expression);

              } else {
                // arg1: conn, arg2: all other
                p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), this.args.get(1).getsNode());
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", arg1Expression, arg2Expression));
              }

            }
          } else {
            if (arg2Expression instanceof SolverNodes.IntegerSolverNode || arg2Expression instanceof SolverNodes.ArithmeticNode
                || arg2Expression instanceof SolverNodes.SymbolicConstantSolverNode) {
              p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), arg2Expression);
              if (p == null) return null;
              c = Expression.mkConnectiveNode("and", p, arg1Expression);
            } else if (arg2Expression instanceof SolverNodes.ConnectiveNode) {

              if (((SolverNodes.ConnectiveNode) arg2Expression).left instanceof SolverNodes.ArithmeticNode) {
                // arg1, arg2: arithmetic
                p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), ((SolverNodes.ConnectiveNode) arg2Expression).left);
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p,
                    Expression.mkConnectiveNode("and", arg1Expression, ((SolverNodes.ConnectiveNode) arg2Expression).right));

              } else {
                // arg1: propositional, arg2: connective
                p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), this.args.get(1).getsNode());
                if (p == null) return null;
                c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", arg1Expression, arg2Expression));
              }
            } else {
              p = this.opSymbol.prod.exec_f(s, this.args.get(0).getsNode(), this.args.get(1).getsNode());
              if (p == null) return null;
              c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", arg1Expression, arg2Expression));
            }
          }
        }

      } else if (opSymbol.prod.argumentSymbols.length == 3) {

        assert (operatorName.equals("repeatrange"));

        if (arg1Expression instanceof SolverNodes.IntegerSolverNode || arg1Expression instanceof SolverNodes.ArithmeticNode
            || arg1Expression instanceof SolverNodes.SymbolicConstantSolverNode) {
          p = this.opSymbol.prod.exec_f(s, arg1Expression, this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e));
          if (p == null) return null;
//          c = p;
          if (!(arg1Expression instanceof SolverNodes.IntegerSolverNode)) {
            e.Nonlinear = true;
          }

          c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and",
              Expression.mkPropositionNode("neq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e)),
              Expression.mkPropositionNode("leq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e))));
//          c = Expression.mkConnectiveNode("and", p,
//              Expression.mkPropositionNode("neq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e)));
        } else {
          p = this.opSymbol.prod
              .exec_f(s, this.args.get(0).getsNode(), this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e));
          if (p == null) return null;
          c = Expression.mkConnectiveNode("and", Expression.mkConnectiveNode("and", p, arg1Expression), Expression.mkConnectiveNode("and",
              Expression.mkPropositionNode("neq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e)),
              Expression.mkPropositionNode("leq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e))));
//          c = Expression.mkConnectiveNode("and", p, Expression.mkConnectiveNode("and", arg1Expression,
//              Expression.mkPropositionNode("neq", this.args.get(1).generateExpression(e), this.args.get(2).generateExpression(e))));
          e.Nonlinear = true;
        }

      } else {
        throw new RuntimeException();
      }

      return c;
    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      return this.s;
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {

      // assumption here: if the node is repeatrange and one of the constant argument in symbolic, then the other one should also be symbolic
      if (this.operatorName.contains("repeat") && (this.args.get(1) instanceof SymbolicConstantTerminalNode)) {
        this.args.get(0).addRepeatNodes(repeatNodes, repeatAtLeastNodes);
        if (this.operatorName.equals("repeatatleast")) repeatAtLeastNodes.add(this);
        repeatNodes.add(this);
      } else {
        for (Node arg : this.args) {
          arg.addRepeatNodes(repeatNodes, repeatAtLeastNodes);
        }
      }

    }

    @Override public RegexProgram getRegex(RegexProgram rp) {

      switch (operatorName) {

      case "notcc": {

        assert (this.args.get(0) instanceof NullaryTerminalNode);

        NullaryTerminalNode ntn = (NullaryTerminalNode) this.args.get(0);

        rp.programs[0].append("[^");
        rp.programs[0].append(((DSL.NullaryTerminalSymbol) ntn.sym).pattern);
        rp.programs[0].append("]");

        break;
      }
      case "not": {
        rp.containAndorNot = true;
        rp.programs[0].append("~(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")");

        break;
      }
      case "startwith": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(").*");

        break;
      }
      case "endwith": {
        rp.programs[0].append(".*(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")");

        break;
      }
      case "contain": {
        rp.programs[0].append(".*(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(").*");

        break;
      }
      case "optional": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")?");

        break;
      }
      case "star": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")*");

        break;
      }
      case "concat": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")(");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append(")");

        break;
      }
      case "and": {
        rp.containAndorNot = true;
        rp.programs[0].append("((");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")&(");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append("))");

        break;
      }
      case "or": {
        rp.programs[0].append("((");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append(")|(");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append("))");

        break;
      }
      case "repeat": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append("){");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append("}");

        break;

      }
      case "repeatatleast": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append("){");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append(",}");

        break;
      }
      case "repeatrange": {
        rp.programs[0].append("(");
        this.args.get(0).getRegex(rp);
        rp.programs[0].append("){");
        this.args.get(1).getRegex(rp);
        rp.programs[0].append(",");
        this.args.get(2).getRegex(rp);
        rp.programs[0].append("}");

        break;
      }
      default:
        throw new RuntimeException();
      }

      return rp;
    }

    // if onlyOverApprox is true, only compute the over-approximation but not under
    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {

//      System.out.println("operatorname:" + operatorName);

      switch (operatorName) {
      case "notcc": {

        // if its children not a nullaryterminal node then directly return

        Node arg1 = this.args.get(0);
        int mode = 0;   // mode 0 = use concrete, mode 1 = use .*
        String pattern = "";

        if (arg1 instanceof NullaryTerminalNode) {
          pattern = ((DSL.NullaryTerminalSymbol) ((NullaryTerminalNode) arg1).sym).pattern;
        } else if (arg1 instanceof VariableNode) {
          if (((VariableNode) arg1).sketch instanceof TerminalNode) {

            NullaryTerminalNode ntn = (NullaryTerminalNode) ((VariableNode) arg1).sketch;
            pattern = ((DSL.NullaryTerminalSymbol) ntn.sym).pattern;

          } else {
            mode = 1;
          }
        } else {
          mode = 1;
        }

        if (mode == 0) {

          // concrete regex
          if (onlyOverApprox) {

            rp.programs[0].append("[^");
            rp.programs[0].append(pattern);
            rp.programs[0].append("]");

          } else if (onlyUnderApprox) {

            rp.programs[1].append("[^");
            rp.programs[1].append(pattern);
            rp.programs[1].append("]");

          } else {

            rp.programs[0].append("[^");
            rp.programs[1].append("[^");
            rp.programs[0].append(pattern);
            rp.programs[1].append(pattern);
            rp.programs[0].append("]");
            rp.programs[1].append("]");
          }

        } else if (mode == 1) {

          // .* and #
          if (onlyOverApprox) {

            rp.programs[0].append("(.*)");

          } else if (onlyUnderApprox) {

            rp.programs[1].append("(#)");

          } else {

            rp.programs[0].append("(.*)");
            rp.programs[1].append("(#)");

          }

        } else {
          throw new RuntimeException();
        }
        break;
      }
      case "not": {

        rp.containAndorNot = true;

        if (onlyOverApprox || onlyUnderApprox) {

          RegexProgram rp_new = new RegexProgram(1);

          rp_new.programs[0].append("~(");
          rp_new.programs[1].append("~(");

          Collections.swap(Arrays.asList(rp_new.programs), 0, 1);

          if (onlyOverApprox) {

            this.args.get(0).getApproximate(rp_new, false, true);
            rp_new.programs[0].append(")");
            rp_new.programs[1].append(")");

            Collections.swap(Arrays.asList(rp_new.programs), 0, 1);

            rp.programs[0].append(rp_new.programs[0]);

          } else {

            this.args.get(0).getApproximate(rp_new, true, false);
            rp_new.programs[0].append(")");
            rp_new.programs[1].append(")");

            Collections.swap(Arrays.asList(rp_new.programs), 0, 1);

            rp.programs[1].append(rp_new.programs[1]);

          }
        } else {

          rp.programs[0].append("~(");
          rp.programs[1].append("~(");

          Collections.swap(Arrays.asList(rp.programs), 0, 1);

          this.args.get(0).getApproximate(rp, false, false);
          rp.programs[0].append(")");
          rp.programs[1].append(")");

          Collections.swap(Arrays.asList(rp.programs), 0, 1);
        }

        break;
      }
      case "startwith": {

        if (onlyOverApprox) {

          rp.programs[0].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(").*");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(").*");

        } else {

          rp.programs[0].append("(");
          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(").*");
          rp.programs[1].append(").*");

        }

        break;
      }
      case "endwith": {

        if (onlyOverApprox) {

          rp.programs[0].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")");

        } else if (onlyUnderApprox) {

          rp.programs[1].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(")");

        } else {
          rp.programs[0].append(".*(");
          rp.programs[1].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")");
          rp.programs[1].append(")");
        }

        break;
      }
      case "contain": {

        if (onlyOverApprox) {

          rp.programs[0].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(").*");

        } else if (onlyUnderApprox) {

          rp.programs[1].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(").*");

        } else {

          rp.programs[0].append(".*(");
          rp.programs[1].append(".*(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(").*");
          rp.programs[1].append(").*");

        }

        break;
      }
      case "optional": {

        if (onlyOverApprox) {

          rp.programs[0].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")?");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(")?");

        } else {

          rp.programs[0].append("(");
          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")?");
          rp.programs[1].append(")?");

        }

        break;
      }
      case "star": {

        if (onlyOverApprox) {

          rp.programs[0].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")*");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(")*");

        } else {

          rp.programs[0].append("(");
          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")*");
          rp.programs[1].append(")*");
        }

        break;
      }
      case "concat": {

        if (onlyOverApprox) {

          rp.programs[0].append("(");
          this.args.get(0).getApproximate(rp, true, false);
          rp.programs[0].append(")(");
          this.args.get(1).getApproximate(rp, true, false);
          rp.programs[0].append(")");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, false, true);
          rp.programs[1].append(")(");
          this.args.get(1).getApproximate(rp, false, true);
          rp.programs[1].append(")");

        } else {

          rp.programs[0].append("(");
          rp.programs[1].append("(");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")(");
          rp.programs[1].append(")(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")");
          rp.programs[1].append(")");

        }

        break;

      }
      case "and": {

        rp.containAndorNot = true;

        if (onlyOverApprox) {
          rp.programs[0].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")&(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append("))");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(")&(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append("))");

        } else {

          rp.programs[0].append("((");
          rp.programs[1].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")&(");
          rp.programs[1].append(")&(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append("))");
          rp.programs[1].append("))");
        }

        break;
      }
      case "or": {

        if (onlyOverApprox) {

          rp.programs[0].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")|(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append("))");

        } else if (onlyUnderApprox) {

          rp.programs[1].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append(")|(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[1].append("))");

        } else {

          rp.programs[0].append("((");
          rp.programs[1].append("((");
          this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append(")|(");
          rp.programs[1].append(")|(");
          this.args.get(1).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
          rp.programs[0].append("))");
          rp.programs[1].append("))");

        }

        break;
      }
      case "repeat": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {

          if (onlyOverApprox) {

            rp.programs[0].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("}");

          } else if (onlyUnderApprox) {

            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append("}");

          } else {
            rp.programs[0].append("(");
            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("}");
            rp.programs[1].append("}");
          }

        } else if (arg1 instanceof SymbolicConstantTerminalNode) {
          // over-approx: repeatrange(R+, min, max) if it's a complete symbolic
          // under-approx: #

          Node arg0 = this.args.get(0);

          if (arg0 instanceof VariableNode) {
            rp.programs[0].append("(.*)");
          } else {

            rp.programs[0].append("(");
            arg0.getApproximate(rp, true, false);
            rp.programs[0].append("){");
            if (rp.assignedSymbolicConstant && rp.addedConstants.contains(arg1)) {

              rp.programs[0].append(((SymbolicConstantTerminalNode) arg1).value);

            } else {
              rp.programs[0].append(Main.K_MIN);
            }
            rp.programs[0].append(",}");

          }

          rp.programs[1].append("(#)");

        }

        break;
      }
      case "repeatatleast": {
        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);

        if (arg1 instanceof RealConstantTerminalNode) {

          if (onlyOverApprox) {

            rp.programs[0].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append(",}");

          } else if (onlyUnderApprox) {

            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append(",}");

          } else {
            rp.programs[0].append("(");
            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append(",}");
            rp.programs[1].append(",}");
          }

        } else if (arg1 instanceof SymbolicConstantTerminalNode) {
          // over-approx: or(contains(R+), \eps)
          // under-approx: #

          Node arg0 = this.args.get(0);

          if (arg0 instanceof VariableNode) {
            rp.programs[0].append("(.*)");
          } else {
            rp.programs[0].append("(");
            arg0.getApproximate(rp, true, false);
            rp.programs[0].append("){");
            if (rp.assignedSymbolicConstant && rp.addedConstants.contains(arg1)) {

              rp.programs[0].append(((SymbolicConstantTerminalNode) arg1).value);

            } else {
              rp.programs[0].append(Main.K_MIN);
            }
            rp.programs[0].append(",}");
          }

          rp.programs[1].append("(#)");

        }

        break;
      }
      case "repeatrange": {

        ConstantTerminalNode arg1 = (ConstantTerminalNode) this.args.get(1);
        ConstantTerminalNode arg2 = (ConstantTerminalNode) this.args.get(2);

        if (arg1 instanceof RealConstantTerminalNode && arg2 instanceof RealConstantTerminalNode) {

          if (onlyOverApprox) {

            rp.programs[0].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append(",");
            arg2.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("}");

          } else if (onlyUnderApprox) {

            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append(",");
            arg2.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[1].append("}");

          } else {
            rp.programs[0].append("(");
            rp.programs[1].append("(");
            this.args.get(0).getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("){");
            rp.programs[1].append("){");
            arg1.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append(",");
            rp.programs[1].append(",");
            arg2.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
            rp.programs[0].append("}");
            rp.programs[1].append("}");
          }

        } else if (arg1 instanceof SymbolicConstantTerminalNode) {
          // over-approx: or(contains(R+), \eps)
          // under-approx: #
          Node arg0 = this.args.get(0);

          if (arg0 instanceof VariableNode) {
            rp.programs[0].append("(.*)");
          } else {

            if (rp.assignedSymbolicConstant && rp.addedConstants.contains(arg1)) {

              rp.programs[0].append("(");
              arg0.getApproximate(rp, true, false);
              rp.programs[0].append("){");
              rp.programs[0].append(((SymbolicConstantTerminalNode) arg1).value);
              rp.programs[0].append(",");
              rp.programs[0].append(((SymbolicConstantTerminalNode) arg2).value);
              rp.programs[0].append("}");

            } else {
              rp.programs[0].append("(");
              arg0.getApproximate(rp, true, false);
              rp.programs[0].append("){");
              rp.programs[0].append(Main.K_MIN);
              rp.programs[0].append(",}");
            }

          }

          rp.programs[1].append("(#)");
        }
//        }

        break;
      }
      default:
        throw new RuntimeException();
      }

      return rp;

    }

  }

  public abstract static class TerminalNode extends Node {

    public final Symbol sym;

    public TerminalNode(int id, Symbol sym, SolverNodes.LengthSolverNode s) {
      super(id, s);
      this.sym = sym;
    }

    public TerminalNode(int id, Node parent, Symbol sym, SolverNodes.LengthSolverNode s) {
      super(id, parent, s);
      this.sym = sym;
    }
  }

  public static class NullaryTerminalNode extends TerminalNode {

    public NullaryTerminalNode(int id, Symbol sym) {
      super(id, sym, Expression.mkLengthSolverNode(id));
    }

    public NullaryTerminalNode(int id, Node parent, Symbol sym) {
      super(id, parent, sym, Expression.mkLengthSolverNode(id));
    }

    @Override public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      throw new RuntimeException();
    }

    @Override public String toString() {
      return this.sym.name;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.toString());
      return b;
    }

    @Override public List<String> generateSubsumePrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();

      if (this.sym.name.equals("<num>")) {
        ret.add("<0>");
//        ret.add("<1>");
//        ret.add("<2>");
//        ret.add("<3>");
//        ret.add("<4>");
//        ret.add("<5>");
//        ret.add("<6>");
//        ret.add("<7>");
//        ret.add("<8>");
//        ret.add("<9>");

      }

      ret.add(this.toString());

      return ret;
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();
      ret.add(this.toString());

      return ret;
    }

    @Override public SolverNode generateExpression(Expression e) {

      return Expression.mkIntNode(1);

    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      return this.s;
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      return;
    }

    @Override public RegexProgram getRegex(RegexProgram rp) {

      if (this.sym.name.equals("<any>")) {
        rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
      } else if (this.sym.name.equals("<\\>")) {
        rp.programs[0].append("(");
        rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
        rp.programs[0].append(")");
      } else {
        rp.programs[0].append("[");
        rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
        rp.programs[0].append("]");
      }
      return rp;
    }

    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {

//      System.out.println("sym.name:" + sym.name);

      if (onlyOverApprox) {

        if (this.sym.name.equals("<any>")) {
          rp.programs[0].append(".");
        } else if (this.sym.name.equals("<\\>")) {
          rp.programs[0].append("(");
          rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          rp.programs[0].append(")");
        } else {
          rp.programs[0].append("[");

          if (this.sym.name.equals("<num1-9>")) {
            rp.programs[0].append("1-9");
          } else {
            rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          }

          rp.programs[0].append("]");
        }

      } else if (onlyUnderApprox) {

        if (this.sym.name.equals("<any>")) {
          rp.programs[1].append(".");
        } else if (this.sym.name.equals("<\\>")) {
          rp.programs[1].append("(");
          rp.programs[1].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          rp.programs[1].append(")");
        } else {
          rp.programs[1].append("[");
          rp.programs[1].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          rp.programs[1].append("]");
        }

      } else {

        if (this.sym.name.equals("<any>")) {
          rp.programs[0].append(".");
          rp.programs[1].append(".");
        } else if (this.sym.name.equals("<\\>")) {
          rp.programs[0].append("(");
          rp.programs[1].append("(");
          rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          rp.programs[1].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          rp.programs[0].append(")");
          rp.programs[1].append(")");
        } else {
          rp.programs[0].append("[");
          rp.programs[1].append("[");

          if (this.sym.name.equals("<num1-9>")) {
            rp.programs[0].append("1-9");
          } else {
            rp.programs[0].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);
          }

          rp.programs[1].append(((DSL.NullaryTerminalSymbol) this.sym).pattern);

          rp.programs[0].append("]");
          rp.programs[1].append("]");
        }

      }

      return rp;
    }
  }

  // TODO: not sure if this is necessary
  public abstract static class ConstantTerminalNode extends TerminalNode {

    public ConstantTerminalNode(int id, Symbol sym, SolverNodes.LengthSolverNode s) {
      super(id, sym, s);
    }

    public ConstantTerminalNode(int id, Node parent, Symbol sym, SolverNodes.LengthSolverNode s) {
      super(id, parent, sym, s);
    }
  }

  // node contains a terminal
  public static class RealConstantTerminalNode extends ConstantTerminalNode {

      public int k;

    public RealConstantTerminalNode(int id, Symbol sym, int k) {
      super(id, sym, null);
      this.k = k;
    }

    public RealConstantTerminalNode(int id, Node parent, Symbol sym, int k) {
      super(id, parent, sym, null);
      this.k = k;
    }

    @Override public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      throw new RuntimeException();
    }

    @Override public String toString() {
      return k + "";
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.toString());
      return b;
    }

    @Override public List<String> generateSubsumePrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();
      ret.add(this.toString());

      return ret;
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {

      List<String> ret = new ArrayList<>();
      ret.add(this.toString());

      return ret;
    }

    @Override public SolverNode generateExpression(Expression e) {
      return Expression.mkIntNode(this.k);
    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      throw new RuntimeException();
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      return;
    }

    @Override public RegexProgram getRegex(RegexProgram rp) {
      rp.programs[0].append(k);
      return rp;
    }

    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {

      if (onlyOverApprox) {
        rp.programs[0].append(k);
      } else if (onlyUnderApprox) {
        rp.programs[1].append(k);
      } else {
        rp.programs[0].append(k);
        rp.programs[1].append(k);
      }

      return rp;
    }

  }

  public static class SymbolicConstantTerminalNode extends ConstantTerminalNode {

      private static resnax.synthesizer.solver.DSL.Symbol sym = Expression.expressionGrammar.nameToSymbol.get("c");

    public final int constant_id;
    public SolverNodes.SymbolicConstantSolverNode scNode;

    public int value = -1;

    public SymbolicConstantTerminalNode(int id, Symbol sym, int constant_id) {
      super(id, sym, null);
      this.constant_id = constant_id;
      this.scNode = Expression.mkSCNode(this.constant_id);
    }

    public SymbolicConstantTerminalNode(int id, int constant_id, Node parent, Symbol sym) {
      super(id, parent, sym, null);
      this.constant_id = constant_id;
      this.scNode = Expression.mkSCNode(this.constant_id);
    }

    public SolverNode generateMinMaxLengthConstraint() {
      return Expression.mkConnectiveNode("and", Expression.mkPropositionNode("geq", scNode, Expression.mkIntNode(Main.K_MIN)),
          Expression.mkPropositionNode("leq", scNode, Expression.mkIntNode(Main.K_MAX)));
    }

    public String getName() {
      return "c" + constant_id;
    }

    @Override public RegexProgram getRegex(RegexProgram rp) {
      rp.programs[0].append(value);
      return rp;
    }

    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {
      throw new RuntimeException();
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.toString());
      return b;
    }

    @Override public List<String> generateSubsumePrunedProgram(int depth) {
      throw new RuntimeException();
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {
      throw new RuntimeException();
    }

    @Override public SolverNode generateExpression(Expression e) {

      return Expression.mkSCNode(this.constant_id);
    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      throw new RuntimeException();
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      return;
    }

    @Override public String toString() {
      if (value == -1) {
        return "c";
      } else {
        return value + "";
      }

    }
  }

  //
  //

  public static class SketchNode extends Node {

    public final Set<Node> components;

    // TODO: a hack by assinging id of this sketchnode to -1 if its empty
    // this constructor is called only when create new variable node with no sketch components
    public SketchNode() {
      super(-1);
      this.components = new HashSet<>();
    }

    public SketchNode(int id, Set<Node> components) {
      super(id);
      this.components = components;
    }

    public boolean containsComponents() {
      return !components.isEmpty();
    }

    @Override public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      throw new RuntimeException();
    }

    @Override public String toString() {
      return toStringBuilder(new StringBuilder(Main.STRINGBUILDER_CAPACITY)).toString();
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {

      if (this.components.isEmpty()) {
        b.append("?");
        return b;
      }

      Object[] componentList = this.components.toArray();

      b.append("?{");
      for (int i = 0; i < components.size(); i++) {
        if (i > 0) b.append(",");
        ((Node) componentList[i]).toStringBuilder(b);
      }
      b.append("}");

      return b;
    }

    @Override public List<String> generateSubsumePrunedProgram(int depth) {
      throw new RuntimeException();
    }

    @Override public List<String> generateSubsumeNotPrunedProgram(int depth) {
      throw new RuntimeException();
    }

    @Override public SolverNode generateExpression(Expression e) {
      throw new RuntimeException();
    }

    @Override public SolverNodes.LengthSolverNode getsNode() {
      throw new RuntimeException();
    }

    @Override public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      throw new RuntimeException();
    }

    @Override public RegexProgram getRegex(RegexProgram rp) {
      throw new RuntimeException();
    }

    @Override public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {

      if (onlyOverApprox) {
        rp.programs[0].append("(.*)");
      } else if (onlyUnderApprox) {
        rp.programs[1].append("(#)");
      } else {
        rp.programs[0].append("(.*)");
        rp.programs[1].append("(#)");
      }

      return rp;
    }

  }

  // wrapper node for sketchnode that have a repeat sketch
  public static class RepSketchNode extends Node {

    public Node sketch;
    public int sid;

    public Node expand = null;

    public RepSketchNode(int id, Node sketch, int sid) {
      super(id);
      this.sketch = sketch;
      this.sid = sid;
    }

    public RepSketchNode(int id, Node sketch, int sid, Node parent) {
      super(id, parent, null);
      this.sketch = sketch;
      this.sid = sid;
    }

    @Override
    public RegexProgram getRegex(RegexProgram rp) {
      return this.expand.getRegex(rp);
    }

    @Override
    public RegexProgram getApproximate(RegexProgram rp, boolean onlyOverApprox, boolean onlyUnderApprox) {
      return this.expand.getApproximate(rp, onlyOverApprox, onlyUnderApprox);
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.toString());
      return b;
    }

    @Override
    public List<String> generateSubsumePrunedProgram(int depth) {
      return this.expand.generateSubsumePrunedProgram(depth);
    }

    @Override
    public List<String> generateSubsumeNotPrunedProgram(int depth) {
      return this.expand.generateSubsumeNotPrunedProgram(depth);
    }

    @Override
    public SolverNode generateExpression(Expression e) {
      return this.expand.generateExpression(e);
    }

    @Override
    public SolverNodes.LengthSolverNode getsNode() {
      return this.expand.getsNode();
    }

    @Override
    public void addRepeatNodes(List<OperatorNode> repeatNodes, List<OperatorNode> repeatAtLeastNodes) {
      this.expand.addRepeatNodes(repeatNodes, repeatAtLeastNodes);
    }

    @Override
    public String toString() {
      if (expand == null) {
        return this.sketch + "_" + sid;
      } else {
        return this.expand + "_" + sid;
      }
    }
  }

}
