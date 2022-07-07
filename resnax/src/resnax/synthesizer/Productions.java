package resnax.synthesizer;

import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;

import resnax.Main;
import resnax.synthesizer.DSL.NonterminalSymbol;
import resnax.synthesizer.DSL.Production;
import resnax.synthesizer.DSL.Symbol;
import resnax.synthesizer.Values.RegexValue;
import resnax.synthesizer.solver.Expression;
import resnax.synthesizer.solver.SolverNodes;
import resnax.synthesizer.solver.SolverNode;

@SuppressWarnings("Duplicates") public class Productions {

  public static final class Id extends Production {

    public Id(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }


    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {
      throw new RuntimeException();
    }

  }

  // startwith(s, r)
  public static final class Startwith extends Production {

    public Startwith(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkPropositionNode("geq", s, args[0]);
    }

    protected static Value exec1(Example e, RegexValue r0) {

      String input = e.input;

      Set<Pair<Integer, Integer>> value = new HashSet<>();

      for (Pair<Integer, Integer> span0 : r0.value) {
        int left0 = span0.getValue0();
        int right0 = span0.getValue1();
        for (int i = right0; i <= input.length(); i++) {
          Pair<Integer, Integer> span = new Pair<>(left0, i);
          value.add(span);
        }
      }

      RegexValue ret = new RegexValue(value);

      return ret;

    }

  }

  public static final class Endwith extends Production {

    public Endwith(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkPropositionNode("geq", s, args[0]);
    }

    protected static Value exec1(Example e, RegexValue r0) {

      Set<Pair<Integer, Integer>> value = new HashSet<>();

      for (Pair<Integer, Integer> span0 : r0.value) {
        int left0 = span0.getValue0();
        int right0 = span0.getValue1();
        for (int i = 0; i <= left0; i++) {
          Pair<Integer, Integer> span = new Pair<>(i, right0);
          value.add(span);
        }
      }

      RegexValue ret = new RegexValue(value);

      return ret;
    }

  }

  public static final class Contain extends Production {

    public Contain(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkPropositionNode("geq", s, args[0]);
    }

    protected static Value exec1(Example e, RegexValue r0) {

      String input = e.input;

      Set<Pair<Integer, Integer>> value = new HashSet<>();

      for (Pair<Integer, Integer> span0 : r0.value) {
        int left0 = span0.getValue0();
        int right0 = span0.getValue1();
        for (int i = 0; i <= left0; i++) {
          for (int j = right0; j <= input.length(); j++) {
            Pair<Integer, Integer> span = new Pair<>(i, j);
            value.add(span);
          }
        }
      }

      RegexValue ret = new RegexValue(value);

      return ret;
    }

  }

  public static final class Repeat extends Production {

    public Repeat(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (args[1] instanceof SolverNodes.ValueSolverNode);

      if (args[0] instanceof SolverNodes.IntegerSolverNode) {
        if (((SolverNodes.IntegerSolverNode) args[0]).getSym().i == 1) {
          return args[1];
        }
      }

      return Expression.mkArithmeticNode("multiply", args[0], args[1]);
    }
  }

  public static final class RepeatAtLeast extends Production {

    public RepeatAtLeast(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (args[1] instanceof SolverNodes.ValueSolverNode);

      if (args[0] instanceof SolverNodes.IntegerSolverNode) {
        if (((SolverNodes.IntegerSolverNode) args[0]).getSym().i == 1) {
          return Expression.mkPropositionNode("geq", s, args[1]);
        }
      }

      return Expression.mkPropositionNode("geq", s, Expression.mkArithmeticNode("multiply", args[0], args[1]));

    }

  }

  public static final class RepeatRange extends Production {

    public RepeatRange(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (args[1] instanceof SolverNodes.ValueSolverNode && args[2] instanceof SolverNodes.ValueSolverNode);

      if (args[0] instanceof SolverNodes.IntegerSolverNode) {
        if (((SolverNodes.IntegerSolverNode) args[0]).getSym().i == 1) {
          return Expression.mkConnectiveNode("and", Expression.mkPropositionNode("geq", s, args[1]), Expression.mkPropositionNode("leq", s, args[2]));
        }
      }

      return Expression.mkConnectiveNode("and", Expression.mkPropositionNode("geq", s, Expression.mkArithmeticNode("multiply", args[0], args[1])),
          Expression.mkPropositionNode("leq", s, Expression.mkArithmeticNode("multiply", args[0], args[2])));

    }

  }

  // concat( r0, r1 )
  public static final class Concat extends Production {

    public Concat(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (!(args[1] instanceof SolverNodes.PropositionNode));

      if (args[0] instanceof SolverNodes.IntegerSolverNode && args[1] instanceof SolverNodes.IntegerSolverNode) {

          return ((resnax.synthesizer.solver.Productions.Plus) Expression.getProd("plus"))
            .exec((SolverNodes.IntegerSolverNode) args[0], (SolverNodes.IntegerSolverNode) args[1]);
      } else {
        return Expression.mkPropositionNode("eq", s, Expression.mkArithmeticNode("plus", args[0], args[1]));
      }

    }

  }


  public static final class Not extends Production {

    public Not(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {
//      assert (!(args[0] instanceof SolverNodes.PropositionNode));

//      return Expression
//          .mkConnectiveNode("and", Expression.mkPropositionNode("neq", s, args[0]), Expression.mkPropositionNode("neq", s, Expression.mkIntNode(0)));

      return Expression.mkConnectiveNode("and", Expression.mkPropositionNode("geq", s, Expression.mkIntNode(1)),
          Expression.mkPropositionNode("leq", s, Expression.mkIntNode(Main.K_MAX - 1)));

    }

  }

  public static final class NotCC extends Production {

    public NotCC(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {
      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkIntNode(1); // TODO: need to come back and check if this is correct
    }

  }

  public static final class And extends Production {

    public And(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (!(args[1] instanceof SolverNodes.PropositionNode));

      if (args[0] instanceof SolverNodes.IntegerSolverNode && args[1] instanceof SolverNodes.IntegerSolverNode) {
        if (((SolverNodes.IntegerSolverNode) args[0]).getSym().i == ((SolverNodes.IntegerSolverNode) args[1]).getSym().i) {
          return args[0];
        }
      }

      return Expression.mkConnectiveNode("and", Expression.mkPropositionNode("eq", s, args[0]), Expression.mkPropositionNode("eq", s, args[1]));
    }

  }

  public static final class Or extends Production {

    public Or(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {

      assert (!(args[0] instanceof SolverNodes.PropositionNode));
      assert (!(args[1] instanceof SolverNodes.PropositionNode));

      if (args[0] instanceof SolverNodes.IntegerSolverNode && args[1] instanceof SolverNodes.IntegerSolverNode) {
        if (((SolverNodes.IntegerSolverNode) args[0]).getSym().i == ((SolverNodes.IntegerSolverNode) args[1]).getSym().i) {
          return args[0];
        }
      }

      return Expression.mkConnectiveNode("or", Expression.mkPropositionNode("eq", s, args[0]), Expression.mkPropositionNode("eq", s, args[1]));

    }

  }

  public static final class Optional extends Production {

    static SolverNode zero = Expression.mkIntNode(0);

    public Optional(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {
      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkConnectiveNode("or", Expression.mkPropositionNode("eq", s, args[0]), Expression.mkPropositionNode("eq", s, zero));
    }

  }

  // TODO: bug here that the constraint for star should actually be v0 >= s \or V0 = 0
  public static final class Star extends Production {

    static SolverNode zero = Expression.mkIntNode(0);

    public Star(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      super(returnSymbol, operatorName, argumentSymbols, cost);
    }

    @Override public SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args) {
      assert (!(args[0] instanceof SolverNodes.PropositionNode));

      return Expression.mkConnectiveNode("or", Expression.mkPropositionNode("geq", s, args[0]), Expression.mkPropositionNode("eq", s, zero));
    }

  }

}
