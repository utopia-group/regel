package resnax.synthesizer.solver;

public class Productions {

  public static final class Id extends DSL.Production {

    public Id(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class And extends DSL.Production {

    public And(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException(); // TODO: should return false, but so far we don't have this value
    }
  }

  public static final class Or extends DSL.Production {

    public Or(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class Geq extends DSL.Production {

    public Geq(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class Leq extends DSL.Production {

    public Leq(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class Eq extends DSL.Production {

    public Eq(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class Neq extends DSL.Production {

    public Neq(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      throw new RuntimeException();
    }
  }

  public static final class Plus extends DSL.Production {

    public Plus(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      int res = ((DSL.IntTerminalSymbol) a1.sym).i + ((DSL.IntTerminalSymbol) a2.sym).i;
      SolverNode resNode = Expression.mkIntNode(res);
      if (resNode == null) System.out.println("null production");
      return resNode;
    }
  }

  public static final class Multiply extends DSL.Production {

    public Multiply(String operatorName, String operatorPrint, DSL.NonterminalSymbol returnSymbol, DSL.Symbol[] argumentSymbols) {
      super(operatorName, operatorPrint, returnSymbol, argumentSymbols);
    }

    @Override public SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2) {
      int res = ((DSL.IntTerminalSymbol) a1.sym).i * ((DSL.IntTerminalSymbol) a2.sym).i;
      SolverNode resNode = Expression.mkIntNode(res);
      if (resNode == null) System.out.println("null production");
      return resNode;
    }
  }

}
