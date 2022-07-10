package resnax.fta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;

import resnax.Example;
import resnax.Main;
import resnax.fta.Productions.And;
import resnax.fta.Productions.Concat;
import resnax.fta.Productions.Contain;
import resnax.fta.Productions.Endwith;
import resnax.fta.Productions.Id;
import resnax.fta.Productions.Not;
import resnax.fta.Productions.Or;
import resnax.fta.Productions.Repeat;
import resnax.fta.Productions.RepeatAtLeast;
import resnax.fta.Productions.RepeatRange;
import resnax.fta.Productions.Startwith;
import resnax.fta.Values.IntValue;
import resnax.fta.Values.RegexValue;

public class DSL {

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

        public List<TerminalSymbol> appliedTerminalSymbols;

        //
        public final Map<String, Production> nameToProduction;
        //
        public final Map<String, Symbol> nameToSymbol;

        public CFG(String dslFolderPath) {
            this.terminalSymbols = new ArrayList<>();
            this.nonterminalSymbols = new ArrayList<>();
            this.productions = new ArrayList<>();
            this.nameToProduction = new HashMap<>();
            this.nameToSymbol = new HashMap<>();
            parse(dslFolderPath);
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
                    // System.out.println("symbol not found: " + s);
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
                    // System.out.println("symbol not found: " + s);
                }
            }

        }

        //     TODO: there is where the grammar is created
        protected void parse(String dslFolderPath) {

            // non-terminal symbols
            {
                // r
                {
                    String name = "r";
                    NonterminalSymbol r = new NonterminalSymbol(name, 3);

                    this.nonterminalSymbols.add(r);
                    this.nameToSymbol.put(name, r);
                    this.startSymbol = r;
                }
            }

            // terminal symbols
            {

                // <num>
                {
                    String name = "<num>";
                    String pattern = "[0-9]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol num = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(num);
                    this.nameToSymbol.put(name, num);
                }

                // <let>
                {
                    String name = "<let>";
                    String pattern = "[a-zA-Z]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol let = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(let);
                    this.nameToSymbol.put(name, let);
                }

                // <cap>
                {
                    String name = "<cap>";
                    double cost = Main.TERMINAL_COST;
                    String pattern = "[A-Z]";
                    NullaryTerminalSymbol cap = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(cap);
                    this.nameToSymbol.put(name, cap);
                }

                // <low>
                {
                    String name = "<low>";
                    String pattern = "[a-z]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol low = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(low);
                    this.nameToSymbol.put(name, low);
                }

                // <vow>
                {
                    String name = "<vow>";
                    String pattern = "[AEIOUaeiou]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol low = new NullaryTerminalSymbol(name, pattern, cost);

//          this.terminalSymbols.add(low);
                    this.nameToSymbol.put(name, low);
                }

                // <hex>
                {
                    String name = "<hex>";
                    String pattern = "[0-9A-Fa-f]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol hex = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(hex);
                    this.nameToSymbol.put(name, hex);
                }

                // <alphanum>
                {
                    String name = "<alphanum>";
                    String pattern = "[0-9A-Za-z]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol alphanum = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(alphanum);
                    this.nameToSymbol.put(name, alphanum);
                }

                // <spec>
                {
                    String name = "<spec>";
                    String pattern = "[!@#$%^&*()_.-]";   // TODO: define this way to fit the pldi #72
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
                    String pattern = "[/]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol slash = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(slash);
                    this.nameToSymbol.put(name, slash);
                }

                // <backslash>
                {
                    String name = "<\\>";
//          String pattern = "\\";
                    String pattern = "[\"\\\"]";
                    double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
                    NullaryTerminalSymbol backslash = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(backslash);
                    this.nameToSymbol.put(name, backslash);
                }

                // <lvert>
                {
                    String name = "<|>";
                    String pattern = "[|]";
                    double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
                    NullaryTerminalSymbol lvert = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(lvert);
                    this.nameToSymbol.put(name, lvert);
                }

                // <and>
                {
                    String name = "<&>";
                    String pattern = "[&]";
                    double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
                    NullaryTerminalSymbol and = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(and);
                    this.nameToSymbol.put(name, and);
                }

                // <lparen>
                {
                    String name = "<(>";
                    String pattern = "[(]";
                    double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
                    NullaryTerminalSymbol lparen = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(lparen);
                    this.nameToSymbol.put(name, lparen);
                }

                // <rparen>
                {
                    String name = "<)>";
                    String pattern = "[)]";
                    double cost = Main.TERMINAL_COST;
//          NullaryTerminalSymbol any = new NullaryTerminalSymbol(name, pattern, cost);
                    NullaryTerminalSymbol rparen = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(rparen);
                    this.nameToSymbol.put(name, rparen);
                }

                // <?>
                {
                    String name = "<?>";
                    String pattern = "[?]";
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
                    for (int i = Main.K_MIN; i <= Main.K_MAX; i ++) {
//          for (int i = 1; i <= 1; i ++) {
                        IntValue value = new IntValue(i);
//            int cost = i + 10;
                        double cost = Main.TERMINAL_COST;
                        constantValues.add(value);
                        valueToCost.put(value, cost);
                    }
                    ConstantTerminalSymbol k = new ConstantTerminalSymbol(name, constantValues, valueToCost);

                    this.terminalSymbols.add(k);
                    this.nameToSymbol.put(name, k);
                }

                // constants 0-9
                {
                    for (int i = 0; i <= 9; i ++) {
                        // TODO: update the name so it looks like a constant
                        String name = "<" + i + ">";
                        String pattern = "[" + i + "]";
                        double cost = Main.TERMINAL_COST;
                        NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                        this.terminalSymbols.add(constant);
                        this.nameToSymbol.put(name, constant);
                    }
                }

                // A-Z
                {
                    for (char alphabet = 'A'; alphabet <= 'Z'; alphabet ++) {
                        String name = "<" + alphabet + ">";
                        String pattern = "[" + alphabet + "]";
                        double cost = Main.TERMINAL_COST;
                        NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                        this.terminalSymbols.add(constant);
                        this.nameToSymbol.put(name, constant);
                    }
                }

                // a-z
                {
                    for (char alphabet = 'a'; alphabet <= 'z'; alphabet ++) {
                        String name = "<" + alphabet + ">";
                        String pattern = "[" + alphabet + "]";
                        double cost = Main.TERMINAL_COST;
                        NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                        this.terminalSymbols.add(constant);
                        this.nameToSymbol.put(name, constant);
                    }
                }

                // special chars
                {
                    String name = "<.>";
                    String pattern = "[\\.]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<,>";
                    String pattern = "[,]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "< >";
                    String pattern = "[ ]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<->";
                    String pattern = "[-]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<_>";
                    String pattern = "[_]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<+>";
                    String pattern = "[+]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

//                {
//                    String name = "<minus>";
//                    String pattern = "-";
//                    int cost = 5;
//                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);
//
//                    this.terminalSymbols.add(constant);
//                    this.nameToSymbol.put(name, constant);
//                }

                {
                    String name = "<*>";
                    String pattern = "[*]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<#>";
                    String pattern = "[#]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<=>";
                    String pattern = "[=]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<^>";
                    String pattern = "[\\^]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<;>";
                    String pattern = "[;]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<:>";
                    String pattern = "[:]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<%>";
                    String pattern = "[%]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<@>";
                    String pattern = "[@]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }


                {
                    String name = "<<>";
                    String pattern = "[<]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<>>";
                    String pattern = "[>]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }
                {
                    String name = "<!>";
                    String pattern = "[!]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<$>";
                    String pattern = "[$]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<~>";
                    String pattern = "[~]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<{>";
                    String pattern = "[{]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

                {
                    String name = "<}>";
                    String pattern = "[}]";
                    double cost = Main.TERMINAL_COST;
                    NullaryTerminalSymbol constant = new NullaryTerminalSymbol(name, pattern, cost);

                    this.terminalSymbols.add(constant);
                    this.nameToSymbol.put(name, constant);
                }

            }

            // productions
            {

                // id_dummy
                {
                    // TODO: this is not right
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_dummy";
                    // TODO: length being 0 might cause issues (not sure but this is a hack)
                    Symbol[] argumentSymbols = new Symbol[1];
                    // TODO: fix this
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    int cost = 0;
                    Production id_dummy = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    // TODO: we _probably_ do not need to add this into productions
//          this.productions.add(id_dummy);
                    this.nameToProduction.put(operatorName, id_dummy);
                }

                // id_<num>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_num";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<num>");
                    int cost = 1;
                    Production id_num = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_num);
                    this.nameToProduction.put(operatorName, id_num);
                }

                // id_<let>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_let";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<let>");
                    int cost = 1;
                    Production id_let = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_let);
                    this.nameToProduction.put(operatorName, id_let);
                }

                // id_<cap>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_cap";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<cap>");
                    int cost = 1;
                    Production id_cap = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_cap);
                    this.nameToProduction.put(operatorName, id_cap);
                }

                // id_<low>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_low";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<low>");
                    int cost = 1;
                    Production id_low = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_low);
                    this.nameToProduction.put(operatorName, id_low);
                }

                // id_<any>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_any";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<any>");
                    int cost = 1;
                    Production id_any = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_any);
                    this.nameToProduction.put(operatorName, id_any);
                }

                // TODO: create a file or something to read the name and the corresponding regex
                // id_<dot>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_dot";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<.>");
                    int cost = 1;
                    Production id_dot = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_dot);
                    this.nameToProduction.put(operatorName, id_dot);
                }

                // id_<comma>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_comma";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<,>");
                    int cost = 1;
                    Production id_comma = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_comma);
                    this.nameToProduction.put(operatorName, id_comma);
                }

                // id_<space>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_space";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("< >");
                    int cost = 1;
                    Production id_space = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_space);
                    this.nameToProduction.put(operatorName, id_space);
                }

                // id_<hyphen>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_hyphen";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<->");
                    int cost = 1;
                    Production id_hyphen = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_hyphen);
                    this.nameToProduction.put(operatorName, id_hyphen);
                }

                // id_<underscore>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_underscore";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<_>");
                    int cost = 1;
                    Production id_underscore = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_underscore);
                    this.nameToProduction.put(operatorName, id_underscore);
                }

                // id_<plus>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_plus";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<+>");
                    int cost = 1;
                    Production id_plus = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_plus);
                    this.nameToProduction.put(operatorName, id_plus);
                }

//                // id_<minus>
//                {
//                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
//                    String operatorName = "id_minus";
//                    Symbol[] argumentSymbols = new Symbol[1];
//                    argumentSymbols[0] = this.nameToSymbol.get("<->");
//                    int cost = 1;
//                    Production id_minus = new Id(returnSymbol, operatorName, argumentSymbols, cost);
//
//                    this.productions.add(id_minus);
//                    this.nameToProduction.put(operatorName, id_minus);
//                }

                // id_<star>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_star";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<*>");
                    int cost = 1;
                    Production id_star = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_star);
                    this.nameToProduction.put(operatorName, id_star);
                }

                // id_<sharp>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_sharp";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<#>");
                    int cost = 1;
                    Production id_sharp = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_sharp);
                    this.nameToProduction.put(operatorName, id_sharp);
                }

                // id_<equal>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_equal";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<=>");
                    int cost = 1;
                    Production id_equal = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_equal);
                    this.nameToProduction.put(operatorName, id_equal);
                }

                // id_<hat>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_hat";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<^>");
                    int cost = 1;
                    Production id_hat = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_hat);
                    this.nameToProduction.put(operatorName, id_hat);
                }

                // id_<semicolon>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_semicolon";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<;>");
                    int cost = 1;
                    Production id_semicolon = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_semicolon);
                    this.nameToProduction.put(operatorName, id_semicolon);
                }

                // id_<colon>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_colon";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<:>");
                    int cost = 1;
                    Production id_colon = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_colon);
                    this.nameToProduction.put(operatorName, id_colon);
                }

                // id_<percentage>
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_percentage";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<%>");
                    int cost = 1;
                    Production id_percentage = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_percentage);
                    this.nameToProduction.put(operatorName, id_percentage);
                }

                // id_at
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "id_at";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("<@>");
                    int cost = 1;
                    Production id_at = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(id_at);
                    this.nameToProduction.put(operatorName, id_at);
                }

                // id_constant0-9
                {
                    for (int i = 0; i <= 9; i ++) {

                        NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                        String operatorName = "id_" + i;
                        Symbol[] argumentSymbols = new Symbol[1];
                        argumentSymbols[0] = this.nameToSymbol.get("<" + i + ">");
                        int cost = 1;
                        Production id_i = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                        this.productions.add(id_i);
                        this.nameToProduction.put(operatorName, id_i);
                    }

                }

                // id_A-Z
                {
                    for (char alphabet = 'A'; alphabet <= 'Z'; alphabet ++) {

                        NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                        String operatorName = "id_" + alphabet;
                        Symbol[] argumentSymbols = new Symbol[1];
                        argumentSymbols[0] = this.nameToSymbol.get("<" + alphabet + ">");

                        int cost = 1;
                        Production id_alphabet = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                        this.productions.add(id_alphabet);
                        this.nameToProduction.put(operatorName, id_alphabet);
                    }

                }

                // id_a-z
                {
                    for (char alphabet = 'a'; alphabet <= 'z'; alphabet ++) {

                        NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                        String operatorName = "id_" + alphabet;
                        Symbol[] argumentSymbols = new Symbol[1];
                        argumentSymbols[0] = this.nameToSymbol.get("<" + alphabet + ">");

                        int cost = 1;
                        Production id_alphabet = new Id(returnSymbol, operatorName, argumentSymbols, cost);

                        this.productions.add(id_alphabet);
                        this.nameToProduction.put(operatorName, id_alphabet);
                    }

                }

                // TODO: add id_xxx for all constant symbols, not necessary do this now
                // id_dummy should work under current setting

                // startwith
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "startwith";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production startwith = new Startwith(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(startwith);
                    this.nameToProduction.put(operatorName, startwith);
                }

                // endwith
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "endwith";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production endwith = new Endwith(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(endwith);
                    this.nameToProduction.put(operatorName, endwith);
                }

                // contain
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "contain";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production contain = new Contain(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(contain);
                    this.nameToProduction.put(operatorName, contain);
                }

                // repeat exact
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "repeat";
                    Symbol[] argumentSymbols = new Symbol[2];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("k");
                    int cost = 1;
                    Production repeat = new Repeat(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(repeat);
                    this.nameToProduction.put(operatorName, repeat);
                }

                // repeat at least
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "repeatatleast";
                    Symbol[] argumentSymbols = new Symbol[2];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("k");
                    int cost = 1;
                    Production repeatatleast = new RepeatAtLeast(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(repeatatleast);
                    this.nameToProduction.put(operatorName, repeatatleast);
                }

                // repeat range
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "repeatrange";
                    Symbol[] argumentSymbols = new Symbol[3];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("k");
                    argumentSymbols[2] = this.nameToSymbol.get("k");
                    int cost = 1;
                    Production repeatRange = new RepeatRange(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(repeatRange);
                    this.nameToProduction.put(operatorName, repeatRange);
                }

                // concat
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "concat";
                    Symbol[] argumentSymbols = new Symbol[2];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production concat = new Concat(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(concat);
                    this.nameToProduction.put(operatorName, concat);
                }

                // not
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "not";
                    Symbol[] argumentSymbols = new Symbol[1];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production not = new Not(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(not);
                    this.nameToProduction.put(operatorName, not);
                }

                // and
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "and";
                    Symbol[] argumentSymbols = new Symbol[2];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production and = new And(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(and);
                    this.nameToProduction.put(operatorName, and);
                }

                // or
                {
                    NonterminalSymbol returnSymbol = (NonterminalSymbol) this.nameToSymbol.get("r");
                    String operatorName = "or";
                    Symbol[] argumentSymbols = new Symbol[2];
                    argumentSymbols[0] = this.nameToSymbol.get("r");
                    argumentSymbols[1] = this.nameToSymbol.get("r");
                    int cost = 1;
                    Production or = new Or(returnSymbol, operatorName, argumentSymbols, cost);

                    this.productions.add(or);
                    this.nameToProduction.put(operatorName, or);
                }

            }

        }

    }

    public static abstract class Symbol {

        public final String name;

        public Symbol(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            // Each symbol is created only once
            return o == this;
        }

        @Override
        public String toString() {
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

        public final resnax.synthesizer.DSL.Production prod;

        public OpNonterminalSymbol(String name, resnax.synthesizer.DSL.Production prod, int maxDepth) {
            super(name, maxDepth);
            this.prod = prod;
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

        public double cost;

        public NullaryTerminalSymbol(String name, String pattern, double cost) {
            super(name);
            this.pattern = pattern;
            this.cost = cost;
        }

        public Value exec(Example e) {
//            System.out.println("pattern:" + pattern);
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
        public final int cost;

        public Production(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
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

        public abstract Value exec(Example e, Value... args);

        @Override
        public int hashCode() {
            return this.operatorName.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public String toString() {
            return this.operatorName + "(" + isRecursive + ")";
        }

    }

}