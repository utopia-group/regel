package resnax.synthesizer.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSL {

  public static class CFG {

    public static final int INT_MAX = 110;

    // T
    public final List<TerminalSymbol> terminalSymbols;
    // N
    public final List<NonterminalSymbol> nonterminalSymbols;
    // P
    public final List<Production> productions;
    // S
    public NonterminalSymbol startSymbol;

    //
    public final Map<String, Production> nameToProduction;
    //
    public final Map<String, Symbol> nameToSymbol;

    public CFG() {
      this.terminalSymbols = new ArrayList<>();
      this.nonterminalSymbols = new ArrayList<>();
      this.productions = new ArrayList<>();

      this.nameToProduction = new HashMap<>();
      this.nameToSymbol = new HashMap<>();

      parse();
    }

    protected void parse() {

      // Nonterminals
      // formula
      {
        String name = "formula";
        NonterminalSymbol f = new NonterminalSymbol(name);

        this.nameToSymbol.put(name, f);
        this.startSymbol = f;
        this.nonterminalSymbols.add(f);
      }

      // propositionals
      {
        String name = "predicate";
        NonterminalSymbol p = new NonterminalSymbol(name);

        this.nameToSymbol.put(name, p);
        this.nonterminalSymbols.add(p);
      }

      // arithmetic
      {
        String name = "arithmetic";
        NonterminalSymbol a = new NonterminalSymbol(name);

        this.nameToSymbol.put(name, a);
        this.nonterminalSymbols.add(a);
      }

      // numelement
      {
        String name = "numelement";
        NonterminalSymbol ne = new NonterminalSymbol(name);

        this.nameToSymbol.put(name, ne);
        this.nonterminalSymbols.add(ne);
      }

      // Terminals
      // s
      {
        String name = "s";
        NodeTerminalSymbol s = new NodeTerminalSymbol(name);

        this.nameToSymbol.put(name, s);
        this.terminalSymbols.add(s);
      }

      // c
      {
        String name = "c";
        SymConstantTerminalSymbol c = new SymConstantTerminalSymbol(name);

        this.nameToSymbol.put(name, c);
        this.terminalSymbols.add(c);
      }

      // integer
      {
        for (int i = 0; i <= INT_MAX; i++) {
          String name = i + "";
          IntTerminalSymbol in = new IntTerminalSymbol(name, i);

          this.nameToSymbol.put(name, in);
          this.terminalSymbols.add(in);
        }
      }

      // TODO: Productions
      // id_predicate
      {
        String operatorName = "id_predicate";
        String operatorPrint = "";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("formula");
        Symbol[] argSym = new Symbol[1];
        argSym[0] = this.nameToSymbol.get("predicate");

        Production id_predicate = new Productions.Id(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(id_predicate);
        this.nameToProduction.put(operatorName, id_predicate);
      }

      // conjunction
      {
        String operatorName = "and";
        String operatorPrint = "∧";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("formula");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("predicate");
        argSym[1] = this.nameToSymbol.get("formula");

        Production and = new Productions.And(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(and);
        this.nameToProduction.put(operatorName, and);

      }

      // disjunction
      {
        String operatorName = "or";
        String operatorPrint = "v";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("formula");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("predicate");
        argSym[1] = this.nameToSymbol.get("formula");

        Production or = new Productions.Or(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(or);
        this.nameToProduction.put(operatorName, or);

      }

      // GEQ
      {
        String operatorName = "geq";
        String operatorPrint = ">=";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("predicate");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("s");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production geq = new Productions.Geq(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(geq);
        this.nameToProduction.put(operatorName, geq);

      }

      // LEQ
      {
        String operatorName = "leq";
        String operatorPrint = "<=";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("predicate");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("s");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production leq = new Productions.Leq(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(leq);
        this.nameToProduction.put(operatorName, leq);

      }

      // EQ
      {
        String operatorName = "eq";
        String operatorPrint = "=";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("predicate");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("s");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production eq = new Productions.Eq(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(eq);
        this.nameToProduction.put(operatorName, eq);
      }

      // NEQ
      {
        String operatorName = "neq";
        String operatorPrint = "!=";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("predicate");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("s");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production neq = new Productions.Neq(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(neq);
        this.nameToProduction.put(operatorName, neq);
      }

      // id_numelement
      {
        String operatorName = "id_numelement";
        String operatorPrint = "";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("arithmetic");
        Symbol[] argSym = new Symbol[1];
        argSym[0] = this.nameToSymbol.get("numelement");

        Production id_numelement = new Productions.Id(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(id_numelement);
        this.nameToProduction.put(operatorName, id_numelement);
      }

      // plus
      {
        String operatorName = "plus";
        String operatorPrint = "+";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("arithmetic");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("numelement");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production plus = new Productions.Plus(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(plus);
        this.nameToProduction.put(operatorName, plus);

      }

      // multiply
      {
        String operatorName = "multiply";
        String operatorPrint = "*";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("arithmetic");
        Symbol[] argSym = new Symbol[2];
        argSym[0] = this.nameToSymbol.get("numelement");
        argSym[1] = this.nameToSymbol.get("arithmetic");

        Production multiply = new Productions.Multiply(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(multiply);
        this.nameToProduction.put(operatorName, multiply);
      }

      // id_s
      {
        String operatorName = "id_s";
        String operatorPrint = "";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("numelement");
        Symbol[] argSym = new Symbol[1];
        argSym[0] = this.nameToSymbol.get("s");

        Production id_s = new Productions.Id(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(id_s);
        this.nameToProduction.put(operatorName, id_s);
      }

      // id_c
      {
        String operatorName = "id_c";
        String operatorPrint = "";
        NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("numelement");
        Symbol[] argSym = new Symbol[1];
        argSym[0] = this.nameToSymbol.get("c");

        Production id_c = new Productions.Id(operatorName, operatorPrint, retSym, argSym);
        this.productions.add(id_c);
        this.nameToProduction.put(operatorName, id_c);
      }

      // id_integer
      {

        for (int i = 0; i < INT_MAX; i++) {
          String operatorName = "id_" + i;
          String operatorPrint = "";
          NonterminalSymbol retSym = (NonterminalSymbol) this.nameToSymbol.get("numelement");
          Symbol[] argSym = new Symbol[1];
          argSym[0] = this.nameToSymbol.get(i + "");

          Production id_i = new Productions.Id(operatorName, operatorPrint, retSym, argSym);
          this.productions.add(id_i);
          this.nameToProduction.put(operatorName, id_i);
        }

      }

    }

  }

  public static abstract class Symbol {

    public final String name;

    public Symbol(String name) {
      this.name = name;
    }

    @Override public int hashCode() {
      throw new RuntimeException();
    }

    @Override public boolean equals(Object obj) {
      return this.equals(obj);
    }

    @Override public String toString() {
      return name;
    }

  }

  public static class NonterminalSymbol extends Symbol {

    public NonterminalSymbol(String name) {
      super(name);
    }

  }

  public static abstract class TerminalSymbol extends Symbol {

    public TerminalSymbol(String name) {
      super(name);
    }

  }

  // s
  public static final class NodeTerminalSymbol extends TerminalSymbol {

    public NodeTerminalSymbol(String name) {
      super(name);
    }

  }

  // c
  public static final class SymConstantTerminalSymbol extends TerminalSymbol {

    public SymConstantTerminalSymbol(String name) {
      super(name);
    }
  }

  public static final class IntTerminalSymbol extends TerminalSymbol {

    public int i;

    public IntTerminalSymbol(String name, int i) {
      super(name);
      this.i = i;
    }

    @Override public String toString() {
      return i + "";
    }
  }

  public static abstract class Production {

    // f
    public final String operatorName;
    public final String operatorPrint;

    // lhs
    public final NonterminalSymbol returnSymbol;

    // rhs
    public final Symbol[] argumentSymbols;

    public Production(String operatorName, String operatorPrint, NonterminalSymbol returnSymbol, Symbol[] argumentSymbols) {
      this.operatorName = operatorName;
      this.operatorPrint = operatorPrint;
      this.returnSymbol = returnSymbol;
      this.argumentSymbols = argumentSymbols;
    }

    public abstract SolverNode exec(SolverNodes.IntegerSolverNode a1, SolverNodes.IntegerSolverNode a2);

    public String toString() {
      return "prod:" + operatorName;
    }

  }

}
