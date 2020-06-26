package resnax.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;

import resnax.Main;
import resnax.synthesizer.Productions.And;
import resnax.synthesizer.Productions.Concat;
import resnax.synthesizer.Productions.Contain;
import resnax.synthesizer.Productions.Endwith;
import resnax.synthesizer.Productions.Optional;
import resnax.synthesizer.Productions.Not;
import resnax.synthesizer.Productions.Or;
import resnax.synthesizer.Productions.Repeat;
import resnax.synthesizer.Productions.RepeatAtLeast;
import resnax.synthesizer.Productions.RepeatRange;
import resnax.synthesizer.Productions.Startwith;
import resnax.synthesizer.Values.IntValue;
import resnax.synthesizer.Values.RegexValue;
import resnax.synthesizer.solver.SolverNodes;
import resnax.synthesizer.solver.SolverNode;

@SuppressWarnings("Duplicates") public class DSL {

  // The context-free grammar G = (T, N, P, S) 
  // T: terminal symbols 
  // N: non-terminal symbols 
  // P: productions 
  // S: one unique start (non-terminal) symbol 
  public static class CFG {

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

    public List<TerminalSymbol> appliedTerminalSymbols;

    public final Symbol symbolicConstantSym = new SymbolicConstantTerminalSymbol();

    public CFG(String dslFolderPath) {
      this.terminalSymbols = new ArrayList<>();   // TODO: both r and k are not part of this list
      this.nonterminalSymbols = new ArrayList<>();
      this.productions = new ArrayList<>();
      this.nameToProduction = new HashMap<>();
      this.nameToSymbol = new HashMap<>();

      if (Main.MODE == 0) parse_so(dslFolderPath);
      else parse_dr(dslFolderPath);

      this.appliedTerminalSymbols = this.terminalSymbols;
    }

    public CFG(String dslFolderPath, List<String> appliedTerminals) {
      this(dslFolderPath);
      this.appliedTerminalSymbols = new ArrayList<>();

      for (String s : appliedTerminals) {
        Symbol sym = nameToSymbol.get(s);
        if (sym != null) {
          this.appliedTerminalSymbols.add((TerminalSymbol) sym);
        }
      }
    }

    public CFG(String dslFolderPath, List<String> appliedTerminalsNoCost, List<String> appliedTerminalCost) {

      this(dslFolderPath);
      this.appliedTerminalSymbols = new ArrayList<>();

      // TODO: only select the terminals in appliedTerminalSymbols as the terminal symbols used in this benchmark
      for (String s : appliedTerminalsNoCost) {
        Symbol sym = nameToSymbol.get(s);
        if (sym != null) {
          NullaryTerminalSymbol tSym = (NullaryTerminalSymbol) sym;
          tSym.cost = 0;
          this.appliedTerminalSymbols.add((TerminalSymbol) sym);
        } else {
          System.out.println("symbol not found: " + s);
        }
      }

      for (String s : appliedTerminalCost) {
        Symbol sym = nameToSymbol.get(s);
        if (sym != null) {
          // modified the cost
          NullaryTerminalSymbol tSym = (NullaryTerminalSymbol) sym;
          tSym.cost = Main.APPLIED_TERMINAL_COST;
          this.appliedTerminalSymbols.add(tSym);
        } else {
          System.out.println("symbol not found: " + s);
        }
      }

    }

    //     TODO: there is where the grammar is created
    protected void parse_so(String dslFolderPath) {

      // non-terminal symbols 
      {
        // r 
        {
          String name = "r";
          NonterminalSymbol r = new NonterminalSymbol(name, 3);

          // TODO: exclude r in the nonterminalSymbol list
//          this.nonterminalSymbols.add(r);
          this.nameToSymbol.put(name, r);
          this.startSymbol = r;
        }
      }

      // terminal symbols  
      {

        // <num> 
        {
          String name = "<num>";
          String pattern = "0-9";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol num = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(num);
          this.nameToSymbol.put(name, num);
        }

        // <num1-9>
        {
          String name = "<num1-9>";
          String pattern = "1-9";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol num19 = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(num19);
          this.nameToSymbol.put(name, num19);
        }

        // <let> 
        {
          String name = "<let>";
          String pattern = "a-zA-Z";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol let = new NullaryTerminalSymbol(name, pattern, cost);
          this.terminalSymbols.add(let);
          this.nameToSymbol.put(name, let);
        }

        // <cap>
        {
          String name = "<cap>";
          double cost = Main.TERMINAL_COST;
          String pattern = "A-Z";
          NullaryTerminalSymbol cap = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(cap);
          this.nameToSymbol.put(name, cap);
        }

        // <low>
        {
          String name = "<low>";
          String pattern = "a-z";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol low = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(low);
          this.nameToSymbol.put(name, low);
        }
        // <vow>
        {
          String name = "<vow>";
          String pattern = "AEIOUaeiou";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol low = new NullaryTerminalSymbol(name, pattern, cost);

//          this.terminalSymbols.add(low);
          this.nameToSymbol.put(name, low);
        }

        // <hex>
        {
          String name = "<hex>";
          String pattern = "0-9A-Fa-f";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol hex = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(hex);
          this.nameToSymbol.put(name, hex);
        }

        // <alphanum>
        {
          String name = "<alphanum>";
          String pattern = "0-9A-Za-z";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol alphanum = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(alphanum);
          this.nameToSymbol.put(name, alphanum);
        }

        // <spec>
        {
          String name = "<spec>";
          String pattern = "-!@#$%^&*()_.";   // TODO: define this way to fit the pldi #72
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);

//          this.terminalSymbols.add(any);
          this.nameToSymbol.put(name, any);
        }

        // <any>
        {
          String name = "<any>";
          String pattern = ".";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(any);
          this.nameToSymbol.put(name, any);
        }

        // <slash>
        {
          String name = "</>";
          String pattern = "/";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol slash = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(slash);
          this.nameToSymbol.put(name, slash);
        }

        // <backslash>
        {
          String name = "<\\>";
//          String pattern = "\\";
          String pattern = "\"\\\"";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol backslash = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(backslash);
          this.nameToSymbol.put(name, backslash);
        }

        // <lvert>
        {
          String name = "<|>";
          String pattern = "|";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol lvert = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(lvert);
          this.nameToSymbol.put(name, lvert);
        }

        // <and>
        {
          String name = "<&>";
          String pattern = "&";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol and = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(and);
          this.nameToSymbol.put(name, and);
        }

        // <lparen>
        {
          String name = "<(>";
          String pattern = "(";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol lparen = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(lparen);
          this.nameToSymbol.put(name, lparen);
        }

        // <rparen>
        {
          String name = "<)>";
          String pattern = ")";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol rparen = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(rparen);
          this.nameToSymbol.put(name, rparen);
        }

        // <?>
        {
          String name = "<?>";
          String pattern = "?";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol question = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(question);
          this.nameToSymbol.put(name, question);
        }


        // k's
        {
          String name = "k";
          List<Value> constantValues = new ArrayList<>();
          Map<Value, Double> valueToCost = new HashMap<>();
          for (int i = Main.K_MIN; i <= Main.K_MAX; i++) {
//          for (int i = 1; i <= 1; i ++) {
            IntValue value = new IntValue(i);
//            int cost = i + 10;
            double cost = Main.TERMINAL_COST;
            constantValues.add(value);
            valueToCost.put(value, cost);
          }
          ConstantTerminalSymbol k = new ConstantTerminalSymbol(name, constantValues, valueToCost);

//          this.terminalSymbols.add(k);
          this.nameToSymbol.put(name, k);
        }

        // constants 0-9
        {
          for (int i = 0; i <= 9; i++) {
            // TODO: update the name so it looks like a constant
            String name = "<" + i + ">";
            String pattern = i + "";
            double cost = Main.TERMINAL_COST;
//            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

            this.terminalSymbols.add(constant);
            this.nameToSymbol.put(name, constant);
          }
        }

        // A-Z
        {
          for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            String name = "<" + alphabet + ">";
            String pattern = alphabet + "";
            double cost = Main.TERMINAL_COST;
//            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

            this.terminalSymbols.add(constant);
            this.nameToSymbol.put(name, constant);
          }
        }

        // a-z
        {
          for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            String name = "<" + alphabet + ">";
            String pattern = alphabet + "";
            double cost = Main.TERMINAL_COST;
//            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
            NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

            this.terminalSymbols.add(constant);
            this.nameToSymbol.put(name, constant);
          }
        }

        // special chars
        {
          String name = "<.>";
          String pattern = ".";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<,>";
          String pattern = ",";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "< >";
          String pattern = " ";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<->";
          String pattern = "-";
          double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<_>";
          String pattern = "_";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<+>";
          String pattern = "+";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<*>";
          String pattern = "*";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<#>";
          String pattern = "#";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<=>";
          String pattern = "=";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<^>";
          String pattern = "\\^";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<;>";
          String pattern = ";";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<:>";
          String pattern = ":";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<%>";
          String pattern = "%";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<@>";
          String pattern = "@";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<<>";
          String pattern = "<";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<>>";
          String pattern = ">";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }
        {
          String name = "<!>";
          String pattern = "!";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<$>";
          String pattern = "$";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<~>";
          String pattern = "~";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<{>";
          String pattern = "{";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }

        {
          String name = "<}>";
          String pattern = "}";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(constant);
          this.nameToSymbol.put(name, constant);
        }


      }

      // productions 
      {

        // contain
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "contain";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.CONTAIN_COST;
          Production contain = new Contain(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(contain);
          this.nameToProduction.put(operatorName, contain);

          NonterminalSymbol ctSymbol = new OpNonterminalSymbol(operatorName, contain, 1);
          this.nonterminalSymbols.add(ctSymbol);
          this.nameToSymbol.put(operatorName, ctSymbol);
        }

        // startwith 
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "startwith";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.SW_COST;
          Production startwith = new Startwith(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(startwith);
          this.nameToProduction.put(operatorName, startwith);

          NonterminalSymbol swSymbol = new OpNonterminalSymbol(operatorName, startwith, 1);
          this.nonterminalSymbols.add(swSymbol);
          this.nameToSymbol.put(operatorName, swSymbol);
        }

        // endwith
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "endwith";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.EW_COST;
          Production endwith = new Endwith(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(endwith);
          this.nameToProduction.put(operatorName, endwith);

          NonterminalSymbol ewSymbol = new OpNonterminalSymbol(operatorName, endwith, 1);
          this.nonterminalSymbols.add(ewSymbol);
          this.nameToSymbol.put(operatorName, ewSymbol);
        }

        // repeat at least
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "repeatatleast";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("k");
          double cost = Main.REPEATAL_COST;
          Production repeatatleast = new RepeatAtLeast(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(repeatatleast);
          this.nameToProduction.put(operatorName, repeatatleast);

          NonterminalSymbol rpatSymbol = new OpNonterminalSymbol(operatorName, repeatatleast, 1);
          if (Main.REPEAT_OPTION == 0) this.nonterminalSymbols.add(rpatSymbol);
          this.nameToSymbol.put(operatorName, rpatSymbol);
        }

        // repeat range
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "repeatrange";
          Symbol[] argumentSymbols = new Symbol[3];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("k");
          argumentSymbols[2] = this.nameToSymbol.get("k");
          double cost = Main.REPEATR_COST;
          Production repeatRange = new RepeatRange(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(repeatRange);
          this.nameToProduction.put(operatorName, repeatRange);

          NonterminalSymbol rprangeSymbol = new OpNonterminalSymbol(operatorName, repeatRange, 1);
          if (Main.REPEAT_OPTION == 0) this.nonterminalSymbols.add(rprangeSymbol);
          this.nameToSymbol.put(operatorName, rprangeSymbol);
        }

        // star
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "star";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.STAR_COST;
          Production star = new Productions.Star(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(star);
          this.nameToProduction.put(operatorName, star);

          NonterminalSymbol ctSymbol = new OpNonterminalSymbol(operatorName, star, 1);
          this.nonterminalSymbols.add(ctSymbol);
          this.nameToSymbol.put(operatorName, ctSymbol);
        }

        // optional
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "optional";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.OPTIONAL_COST;
          Production optional = new Optional(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(optional);
          this.nameToProduction.put(operatorName, optional);

          NonterminalSymbol ctSymbol = new OpNonterminalSymbol(operatorName, optional, 1);
          this.nonterminalSymbols.add(ctSymbol);
          this.nameToSymbol.put(operatorName, ctSymbol);
        }

        // or
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "or";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.OR_COST;
          Production or = new Or(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(or);
          this.nameToProduction.put(operatorName, or);

          NonterminalSymbol orSymbol = new OpNonterminalSymbol(operatorName, or, 1);
          this.nonterminalSymbols.add(orSymbol);
          this.nameToSymbol.put(operatorName, orSymbol);
        }

        // and
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "and";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.AND_COST;
          Production and = new And(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(and);
          this.nameToProduction.put(operatorName, and);

          NonterminalSymbol andSymbol = new OpNonterminalSymbol(operatorName, and, 1);
          this.nonterminalSymbols.add(andSymbol);
          this.nameToSymbol.put(operatorName, andSymbol);
        }

        // repeat exact
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "repeat";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("k");
          double cost = Main.REPEAT_COST;
          Production repeat = new Repeat(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(repeat);
          this.nameToProduction.put(operatorName, repeat);

          NonterminalSymbol rpSymbol = new OpNonterminalSymbol(operatorName, repeat, 1);
          if (Main.REPEAT_OPTION == 0) this.nonterminalSymbols.add(rpSymbol);
          this.nameToSymbol.put(operatorName, rpSymbol);
        }

        // concat
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "concat";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.CONCAT_COST;
          Production concat = new Concat(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(concat);
          this.nameToProduction.put(operatorName, concat);

          NonterminalSymbol concatSymbol = new OpNonterminalSymbol(operatorName, concat, 1);
          this.nonterminalSymbols.add(concatSymbol);
          this.nameToSymbol.put(operatorName, concatSymbol);
        }

        // not
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "not";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.NOT_COST;
          Production not = new Not(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(not);
          this.nameToProduction.put(operatorName, not);

          NonterminalSymbol notSymbol = new OpNonterminalSymbol(operatorName, not, 1);
          this.nonterminalSymbols.add(notSymbol);
          this.nameToSymbol.put(operatorName, notSymbol);
        }

        // not cc
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "notcc";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");      // TODO: actually the argument could only be nullary symbol
          double cost = Main.OTHER_COST;
          Production notcc = new Productions.NotCC(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(notcc);
          this.nameToProduction.put(operatorName, notcc);

          NonterminalSymbol notCCSymbol = new OpNonterminalSymbol(operatorName, notcc, 1);
          this.nonterminalSymbols.add(notCCSymbol);
          this.nameToSymbol.put(operatorName, notCCSymbol);
        }

      }

    }


    protected void parse_dr(String dslFolderPath) {
      // non-terminal symbols
      {
        // r
        {
          String name = "r";
          NonterminalSymbol r = new NonterminalSymbol(name, 3);

          // TODO: exclude r in the nonterminalSymbol list
//          this.nonterminalSymbols.add(r);
          this.nameToSymbol.put(name, r);
          this.startSymbol = r;
        }
      }

      // terminal symbols
      {

        // =========== deep regex stuff ================

        // <num>
        {
          String name = "<num>";
          String pattern = "0-9";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol num = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(num);
          this.nameToSymbol.put(name, num);
        }

        // <cap>
        {
          String name = "<cap>";
          double cost = Main.TERMINAL_COST;
          String pattern = "A-Z";
          NullaryTerminalSymbol cap = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(cap);
          this.nameToSymbol.put(name, cap);
        }

        // <low>
        {
          String name = "<low>";
          String pattern = "a-z";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol low = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(low);
          this.nameToSymbol.put(name, low);
        }

        // <let>
        {
          String name = "<let>";
          String pattern = "A-Za-z";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol let = new NullaryTerminalSymbol(name, pattern, cost);
          this.terminalSymbols.add(let);
          this.nameToSymbol.put(name, let);
        }

        // <vow>
        {
          String name = "<vow>";
          String pattern = "AEIOUaeiou";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol vow = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(vow);
          this.nameToSymbol.put(name, vow);
        }

        // <M0>
        {
          String name = "<m0>";
          String pattern = "!";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol m0 = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(m0);
          this.nameToSymbol.put(name, m0);
        }

        // <M1>
        {
          String name = "<m1>";
          String pattern = "@";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol m1 = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(m1);
          this.nameToSymbol.put(name, m1);
        }

        // <M0>
        {
          String name = "<m2>";
          String pattern = "#";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol m2 = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(m2);
          this.nameToSymbol.put(name, m2);
        }

        // <M0>
        {
          String name = "<m3>";
          String pattern = "$";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol m3 = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(m3);
          this.nameToSymbol.put(name, m3);
        }

        // <any>
        {
          String name = "<any>";
          String pattern = ".";
          double cost = Main.TERMINAL_COST;
          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);

          this.terminalSymbols.add(any);
          this.nameToSymbol.put(name, any);
        }


      }

      // productions
      {

        // contain
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "contain";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.CONTAIN_COST;
          Production contain = new Contain(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(contain);
          this.nameToProduction.put(operatorName, contain);

          NonterminalSymbol ctSymbol = new OpNonterminalSymbol(operatorName, contain, 1);
          this.nonterminalSymbols.add(ctSymbol);
          this.nameToSymbol.put(operatorName, ctSymbol);
        }

        // startwith
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "startwith";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.SW_COST;
          Production startwith = new Startwith(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(startwith);
          this.nameToProduction.put(operatorName, startwith);

          NonterminalSymbol swSymbol = new OpNonterminalSymbol(operatorName, startwith, 1);
          this.nonterminalSymbols.add(swSymbol);
          this.nameToSymbol.put(operatorName, swSymbol);
        }

        // endwith
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "endwith";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.EW_COST;
          Production endwith = new Endwith(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(endwith);
          this.nameToProduction.put(operatorName, endwith);

          NonterminalSymbol ewSymbol = new OpNonterminalSymbol(operatorName, endwith, 1);
          this.nonterminalSymbols.add(ewSymbol);
          this.nameToSymbol.put(operatorName, ewSymbol);
        }

        // repeat at least
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "repeatatleast";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("k");
          double cost = Main.REPEATAL_COST;
          Production repeatatleast = new RepeatAtLeast(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(repeatatleast);
          this.nameToProduction.put(operatorName, repeatatleast);

          NonterminalSymbol rpatSymbol = new OpNonterminalSymbol(operatorName, repeatatleast, 1);
          if (Main.REPEAT_OPTION == 0) this.nonterminalSymbols.add(rpatSymbol);
          this.nameToSymbol.put(operatorName, rpatSymbol);
        }

        // star
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "star";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.STAR_COST;
          Production star = new Productions.Star(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(star);
          this.nameToProduction.put(operatorName, star);

          NonterminalSymbol ctSymbol = new OpNonterminalSymbol(operatorName, star, 1);
          this.nonterminalSymbols.add(ctSymbol);
          this.nameToSymbol.put(operatorName, ctSymbol);
        }

        // or
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "or";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.OR_COST;
          Production or = new Or(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(or);
          this.nameToProduction.put(operatorName, or);

          NonterminalSymbol orSymbol = new OpNonterminalSymbol(operatorName, or, 1);
          this.nonterminalSymbols.add(orSymbol);
          this.nameToSymbol.put(operatorName, orSymbol);
        }

        // and
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "and";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.AND_COST;
          Production and = new And(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(and);
          this.nameToProduction.put(operatorName, and);

          NonterminalSymbol andSymbol = new OpNonterminalSymbol(operatorName, and, 1);
          this.nonterminalSymbols.add(andSymbol);
          this.nameToSymbol.put(operatorName, andSymbol);
        }

        // concat
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "concat";
          Symbol[] argumentSymbols = new Symbol[2];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          argumentSymbols[1] = this.nameToSymbol.get("r");
          double cost = Main.CONCAT_COST;
          Production concat = new Concat(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(concat);
          this.nameToProduction.put(operatorName, concat);

          NonterminalSymbol concatSymbol = new OpNonterminalSymbol(operatorName, concat, 1);
          this.nonterminalSymbols.add(concatSymbol);
          this.nameToSymbol.put(operatorName, concatSymbol);
        }

        // not
        {
          NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
          String operatorName = "not";
          Symbol[] argumentSymbols = new Symbol[1];
          argumentSymbols[0] = this.nameToSymbol.get("r");
          double cost = Main.NOT_COST;
          Production not = new Not(returnSymbol, operatorName, argumentSymbols, cost);

          this.productions.add(not);
          this.nameToProduction.put(operatorName, not);

          NonterminalSymbol notSymbol = new OpNonterminalSymbol(operatorName, not, 1);
          this.nonterminalSymbols.add(notSymbol);
          this.nameToSymbol.put(operatorName, notSymbol);
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
      return this.name.hashCode();
    }

    @Override public boolean equals(Object o) {
      // Each symbol is created only once 
      return o == this;
    }

    @Override public String toString() {
      return this.name;
    }

  }

  public static class NonterminalSymbol extends Symbol {

    public final int maxDepth;

    public NonterminalSymbol(String name, int maxDepth) {
      super(name);
      this.maxDepth = maxDepth;
    }

  }

  public static final class OpNonterminalSymbol extends NonterminalSymbol {

    public final Production prod;

    public OpNonterminalSymbol(String name, Production prod, int maxDepth) {
      super(name, maxDepth);
      this.prod = prod;
    }

  }

  public static final class SymbolicConstantTerminalSymbol extends TerminalSymbol {

    public SymbolicConstantTerminalSymbol() {
      super("SymbolicConstant");
    }

  }

  public static final class ConstantTerminalSymbol extends TerminalSymbol {

    public final List<Value> constantValues;

    public final Map<Value, Double> valueToCost;

    public ConstantTerminalSymbol(String name, List<Value> constantValues, Map<Value, Double> valueToCost) {
      super(name);
      this.constantValues = constantValues;
      this.valueToCost = valueToCost;
    }

  }

  public static final class NullaryTerminalSymbol extends TerminalSymbol {

    public final String pattern;

    public double cost; // could be modified by benchmark.java

//    public NullaryTerminalSymbol(String name, String pattern, int cost) {
//      super(name);
//      this.pattern = pattern;
//      this.cost = cost;
//      this.automaton = null;
//    }

    public NullaryTerminalSymbol(String name, String pattern, double cost) {
      super(name);
      this.pattern = pattern;
      this.cost = cost;
    }

    public Value exec(Example e) {
      Pattern p = Pattern.compile(pattern);
      Matcher m = p.matcher(e.input);

      Set<Pair<Integer, Integer>> set = new HashSet<>();
      while (m.find()) {
        Pair<Integer, Integer> pair = new Pair<>(m.start(), m.end());
        set.add(pair);
      }

      RegexValue ret = new RegexValue(set);
      return ret;
    }

  }

  public static abstract class TerminalSymbol extends Symbol {

    public TerminalSymbol(String name) {
      super(name);
    }

  }

  public static abstract class Production {

    // s_0 
    public final NonterminalSymbol returnSymbol;
    // f 
    public final String operatorName;
    // s_1, .., s_n 
    public final Symbol[] argumentSymbols;

    // n 
    public final int rank;
    // TODO: currently only support self-recursion (not mutual recursion) 
    public final boolean isRecursive;

    // the cost of operator f 
    // this is used for computing the cost of a program 
    public final double cost;

    public Production(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, double cost) {
      this.returnSymbol = returnSymbol;
      this.operatorName = operatorName;
      this.argumentSymbols = argumentSymbols;
      this.rank = argumentSymbols.length;
      boolean isRecursive = false;
      {
        for (Symbol argumentSymbol : argumentSymbols) {
          if (returnSymbol.equals(argumentSymbol)) {
            isRecursive = true;
            break;
          }
        }
      }
      this.isRecursive = isRecursive;
      this.cost = cost;
    }

    public abstract SolverNode exec_f(SolverNodes.LengthSolverNode s, SolverNode... args);

    @Override public int hashCode() {
      return this.operatorName.hashCode();
    }

    @Override public boolean equals(Object o) {
      return o == this;
    }

    @Override public String toString() {
      return this.operatorName + "(" + isRecursive + ")";
    }

  }

}
