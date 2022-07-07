package resnax.synthesizer.solver;

import com.microsoft.z3.ArithExpr;
import resnax.Main;

import java.util.HashMap;

public class SolverNodes {

  public static abstract class NonterminalSolverNode extends SolverNode {

    public final DSL.Production prod;

    public SolverNode left;
    public SolverNode right;

    public NonterminalSolverNode(DSL.Production prod, SolverNode left, SolverNode right) {
      this.left = left;
      this.right = right;
      this.prod = prod;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {

//      System.out.println("operatorname:" + prod.operatorName);

      left.toStringBuilder(b);
      b.append(" ");
      b.append(prod.operatorPrint);
      b.append(" ");
      right.toStringBuilder(b);

      return b;
    }

  }

  // TODO: Not sure if these are needed...
  public static class ConnectiveNode extends NonterminalSolverNode {

    public ConnectiveNode(DSL.Production prod, SolverNode left, SolverNode right) {
      super(prod, left, right);
    }
  }

  public static class PropositionNode extends NonterminalSolverNode {

    public PropositionNode(DSL.Production prod, SolverNode left, SolverNode right) {
      super(prod, left, right);
    }

  }

  public static class ArithmeticNode extends NonterminalSolverNode {

    public ArithmeticNode(DSL.Production prod, SolverNode left, SolverNode right) {
      super(prod, left, right);
    }

  }

  public static abstract class TerminalSolverNode extends SolverNode {

    public DSL.Symbol sym;

    public TerminalSolverNode(DSL.Symbol sym) {
      this.sym = sym;
    }
  }

  public static class LengthSolverNode extends TerminalSolverNode {

    public int id;

    public LengthSolverNode(DSL.Symbol sym, int id) {
      super(sym);
      this.id = id;
    }

    public String getName() {
      return "s" + id;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append("s");
      b.append(id);

      return b;
    }

  }

  public static abstract class ValueSolverNode extends TerminalSolverNode {

    public ValueSolverNode(DSL.Symbol sym) {
      super(sym);
    }
  }

  public static class SymbolicConstantSolverNode extends ValueSolverNode {

    public int id;
    public ArithExpr z3symbol;

    public SymbolicConstantSolverNode(DSL.Symbol sym, int id) {
      super(sym);
      this.id = id;
    }

    public String getName() {
      return "c" + id;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append("c");
      b.append(id);

      return b;
    }

  }

  public static class IntegerSolverNode extends ValueSolverNode {

    private final static HashMap<Integer, IntegerSolverNode> intnode = new HashMap<>();

    private static int kmax_old;

    static {
//      System.out.println("kmax at intergersolvernode:" + Main.K_MAX);
      kmax_old = Main.K_MAX;
      for (int i = 0; i <= Main.K_MAX; i++) {

        intnode.put(i, new IntegerSolverNode(Expression.expressionGrammar.nameToSymbol.get(i + "")));
      }
    }

    private IntegerSolverNode(DSL.Symbol sym) {
      super(sym);
    }

    public static void updateIntegerSolverNode(int new_k) {
      if (new_k == kmax_old) return;
      for (int i = kmax_old + 1; i <= new_k; i++) {
        intnode.put(i, new IntegerSolverNode(Expression.expressionGrammar.nameToSymbol.get(i + "")));
      }
    }

    public static IntegerSolverNode getInstance(int i) {
      return intnode.get(i);
    }

    public DSL.IntTerminalSymbol getSym() {
      return (DSL.IntTerminalSymbol) sym;
    }

    @Override public StringBuilder toStringBuilder(StringBuilder b) {
      b.append(this.sym.name);
      return b;
    }

  }

}
