package resnax;

import java.util.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import resnax.ast.SketchGrammarLexer;
import resnax.ast.SketchGrammarParser;
import resnax.ast.SketchGrammarParser.AndProgContext;
import resnax.ast.SketchGrammarParser.AndSketchContext;
import resnax.ast.SketchGrammarParser.CharClassProgContext;
import resnax.ast.SketchGrammarParser.ConcatProgContext;
import resnax.ast.SketchGrammarParser.ConcatSketchContext;
import resnax.ast.SketchGrammarParser.ConstantProgContext;
import resnax.ast.SketchGrammarParser.ContainProgContext;
import resnax.ast.SketchGrammarParser.ContainSketchContext;
import resnax.ast.SketchGrammarParser.EndwithProgContext;
import resnax.ast.SketchGrammarParser.EndwithSketchContext;
import resnax.ast.SketchGrammarParser.FreeVarSketchContext;
import resnax.ast.SketchGrammarParser.GuardedVarSketchContext;
import resnax.ast.SketchGrammarParser.MultiSketchContext;
import resnax.ast.SketchGrammarParser.NotProgContext;
import resnax.ast.SketchGrammarParser.NotSketchContext;
import resnax.ast.SketchGrammarParser.OrProgContext;
import resnax.ast.SketchGrammarParser.OrSketchContext;
import resnax.ast.SketchGrammarParser.ProgContext;
import resnax.ast.SketchGrammarParser.RepeatAtLeastProgContext;
import resnax.ast.SketchGrammarParser.RepeatAtLeastSketchContext;
import resnax.ast.SketchGrammarParser.RepeatProgContext;
import resnax.ast.SketchGrammarParser.RepeatRangeProgContext;
import resnax.ast.SketchGrammarParser.RepeatRangeSketchContext;
import resnax.ast.SketchGrammarParser.RepeatSketchContext;
import resnax.ast.SketchGrammarParser.SingleSketchContext;
import resnax.ast.SketchGrammarParser.StartwithProgContext;
import resnax.ast.SketchGrammarParser.StartwithSketchContext;
import resnax.ast.SketchGrammarParser.VarContext;
import resnax.ast.SketchGrammarVisitor;
import resnax.fta.DSL.CFG;
import resnax.fta.DSL.ConstantTerminalSymbol;
import resnax.fta.DSL.NullaryTerminalSymbol;
import resnax.fta.DSL.Production;
import resnax.fta.DSL.Symbol;
import resnax.fta.DSL.TerminalSymbol;
import resnax.fta.FTA;
import resnax.fta.FTA.State;
import resnax.fta.FTA.Transition;
import resnax.fta.FTA.VarState;
import resnax.fta.Value;
import resnax.fta.Values.IntValue;
import resnax.util.MultiMap;
import resnax.util.SetMultiMap;
import resnax.util.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SketchParserFTA extends AbstractParseTreeVisitor<Object> implements SketchGrammarVisitor<Object> {

    public final CFG grammar;

    public final FTA fta;

    public final Example[] examples;

    // TODO(XINYU): control the FTA construction for variables
    // TODO: switch from final to non-final, not the right way to do it
    // 3 is the default depth
    public int maxDepth = 1;

    protected SketchParserFTA(CFG grammar, FTA fta, Example[] examples) {
        this.grammar = grammar;
        this.fta = fta;
        this.examples = examples;
    }

    public FTA parse(String sketch) {

        {

            CharStream input = CharStreams.fromString(sketch);

            SketchGrammarLexer lexer = new SketchGrammarLexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            SketchGrammarParser parser = new SketchGrammarParser(tokens);

            ParseTree tree = parser.sketch();

            assert (tree.getText().equals(sketch)) : tree.getText() + " != " + sketch;

            this.visit(tree);

        }

        return this.fta;

    }

    //
    //
    //
    //

    // construct FTA for variable or guarded variable sketch
    protected Set<State> constructFTA(String sketch, SetMultiMap<Symbol, VarState> initialNewStates) {

        SetMultiMap<Symbol, VarState> oldStates = new SetMultiMap<>();
        SetMultiMap<Symbol, VarState> newStates = initialNewStates;

        // transitions that have been created so far
        LinkedList<Transition> transitions = new LinkedList<>();

        // a work-list algorithm
        {

            for (int curDepth = 0; curDepth <= this.maxDepth && !newStates.isEmpty(); curDepth ++) {

                System.out.println("==========================================================");
                System.out.println("currDepth: " + curDepth);

                // all the states that have been created so far before this iteration
                SetMultiMap<Symbol, VarState> oldStates1 = Utils.unionSetMultiMaps(oldStates, newStates);

                System.out.println("==========================================================");
                System.out.println("number of total states: " + oldStates1.size());
                System.out.println("number of total transitions: " + transitions.size());

                // the new states that will be created in this iteration
                SetMultiMap<Symbol, VarState> newStates1 = new SetMultiMap<>();

                // the productions that might produce new states in this iteration
                // these productions are "activated" by the new states ("newStates") created in the previous iteration
                List<Production> activatedProductions = computeActivatedProductions(oldStates1, newStates);

//        System.out.println("activated productions: " + activatedProductions);

                for (Production activatedProduction : activatedProductions) {

                    // apply the Prod rule
                    // 1. compute new states before next iteration and store them into "newStates1"
                    // 2. compute new transitions that are created by the new states ("newStates") in the previous iteration and add them into "transitions"
                    handleActivatedProduction(sketch, activatedProduction, oldStates, newStates, oldStates1, newStates1, transitions);

                }

                oldStates = oldStates1;
                newStates = newStates1;

            }

            oldStates = Utils.unionSetMultiMaps(oldStates, newStates);

        }

        // update FTA
        {
            // states
            for (Symbol symbol : oldStates.keySet()) {
                for (VarState state : oldStates.get(symbol)) {
                    this.fta.sketchToStates.put(sketch, state);
                }
            }
            // transitions
            for (Transition transition : transitions) {
                this.fta.transitions.add(transition);
            }
        }

        Set<State> ret = new HashSet<>();
        for (State state : oldStates.get(this.grammar.startSymbol)) {
            ret.add(state);
        }

        return ret;

    }

    // a production is activated iff
    // (1). the production has at least one argument symbol that has new states being created (newStates)
    // (2). all of its argument symbols have non-empty sets of states that have been created so far (oldStates1)
    private List<Production> computeActivatedProductions(MultiMap<Symbol, VarState> oldStates1, MultiMap<Symbol, VarState> newStates) {

        List<Production> ret = new ArrayList<>();

        Loop: for (Production production : this.grammar.productions) {
            Symbol[] argumentSymbols = production.argumentSymbols;
            // check (2)
            for (Symbol argumentSymbol : argumentSymbols) {
                if (!oldStates1.containsKey(argumentSymbol)) continue Loop;
                assert (!oldStates1.get(argumentSymbol).isEmpty());
            }
            // check (1)
            for (Symbol argumentSymbol : argumentSymbols) {
                if (newStates.containsKey(argumentSymbol)) {
                    ret.add(production);
                    continue Loop;
                }
            }
        }

        return ret;
    }

    // (Q_1 + Q'_1) * (Q_2 + Q'_2) * .. * (Q_n + Q'_n)
    // = [Q'_1 * (Q_2 + Q'_2) * .. * (Q_n + Q'_n)][computed] + Q_1 * (Q_2 + Q'_2) * .. * (Q_n + Q'_n)
    // = [computed] + [Q_1 * Q'_2 * .. * (Q_n +Q'_n)][computed] + Q_1 * Q_2 * .. * (Q_n + Q'_n)
    // = [computed] + [computed] + ..
    private void handleActivatedProduction(String sketch, Production production, MultiMap<Symbol, VarState> oldStates,
                                           MultiMap<Symbol, VarState> newStates, MultiMap<Symbol, VarState> oldStates1, MultiMap<Symbol, VarState> newStates1,
                                           LinkedList<Transition> transitions) {

        System.out.println("prodcution activated:" + production);

        Symbol[] argumentSymbols = production.argumentSymbols;

        // the current states of arguments for which we will compute the cross product
        List<Collection<VarState>> list = new ArrayList<>(production.rank);
        // initialize states of arguments to oldStates1 (all current states)
        // list = [ Q_1 + Q'_1, Q_2 + Q'_2, .., Q_n + Q'_n ]
        for (Symbol argumentSymbol : argumentSymbols) {
            list.add(oldStates1.get(argumentSymbol));
        }

        for (int i = 0; i < production.rank; i ++) {

            // in the i-th iteration, we compute Q_1 * .. * Q_{i-1} * Q'_i * (Q_{i+1} + Q'_{i+1}) * .. * (Q'_n + Q_n)

            Symbol symbol = argumentSymbols[i];

            // new states Q'_i
            Collection<VarState> ithNewStates = newStates.get(symbol);

            // if there is no states in Q'_i, we do not compute the cross product
            // it seems we should do list.set(i, Q_i) before continue, however we do not do this
            // since Q'_i being empty means (Q'_i + Q_i) = Q_i -- LOL
            if (ithNewStates == null || ithNewStates.isEmpty()) continue;

            // set to Q'_i (new states)
            list.set(i, ithNewStates);

            // compute Q_1 * .. * Q_{i-1} * Q'_i * (Q_{i+1} + Q'_{i+1}) * .. * (Q_n + Q'_n)
            // by applying the abstract transformer on the Cartesian product of states for the arguments
            cartesian(sketch, production, list, oldStates1, newStates1, transitions);

            // old states Q_i
            Collection<VarState> ithOldStates = oldStates.get(symbol);
            // if Q_i is empty, we can break the whole loop
            if (ithOldStates == null || ithOldStates.isEmpty()) return;

            // set to Q_i (old states)
            list.set(i, ithOldStates);

        }

    }

    private void cartesian(String sketch, Production production, List<Collection<VarState>> list, MultiMap<Symbol, VarState> oldStates1,
                           MultiMap<Symbol, VarState> newStates1, Collection<Transition> transitions) {

        cartesian(sketch, production, list, list.size() - 1, new VarState[production.rank], oldStates1, newStates1, transitions);

    }

    // NOTE: the core function for FTA construction
    private void cartesian(String sketch, Production production, List<Collection<VarState>> list, int currIndex, VarState[] currStates,
                           MultiMap<Symbol, VarState> oldStates1, MultiMap<Symbol, VarState> newStates1, Collection<Transition> transitions) {

        // base case: we find one combination of states for arguments
        if (currIndex == -1) {

            int numOfExamples = this.examples.length;
            Symbol returnSymbol = production.returnSymbol;
            Value[] returnValues = new Value[numOfExamples];
            boolean valid = false;
            {
                Value[][] argsForExamples = new Value[numOfExamples][production.rank];
                for (int i = 0; i < production.rank; i ++) {
                    Value[] currValues = currStates[i].values;
                    for (int j = 0; j < numOfExamples; j ++) {
                        argsForExamples[j][i] = currValues[j];
                    }
                    if (currStates[i].valid) valid = true;
                }
                for (int i = 0; i < returnValues.length; i ++) {
                    Value[] args = argsForExamples[i];
                    Value returnValue = production.exec(this.examples[i], args);
                    returnValues[i] = returnValue;
                }

            }

            //
            // NOTE: create a new state for the return value
            //
            VarState returnState = this.fta.mkVarState(sketch, returnSymbol, returnValues);
            returnState.valid = valid || returnState.valid;

            State[] argumentStates = new State[currStates.length];
            System.arraycopy(currStates, 0, argumentStates, 0, argumentStates.length);

            // NOTE: create a new transition
            // NOTE: This transition is guaranteed to be a new one (!) since it has at least one new argument state
            Transition transition = this.fta.mkTransition(production, argumentStates, returnState);
            transitions.add(transition);

            System.out.println("transition:" + transition);

            //
            // update "newStates1"
            //
            if (!oldStates1.contains(returnSymbol, returnState)) {
                newStates1.put(returnSymbol, returnState);
            }

            return;

        }

        // recursive case
        for (VarState state : list.get(currIndex)) {
            currStates[currIndex] = state;
            cartesian(sketch, production, list, currIndex - 1, currStates, oldStates1, newStates1, transitions);
        }

    }

    //
    //
    //
    //

    // construct FTA for a non-variable sketch
    protected Set<State> constructFTA(String sketch, Production production, List<Collection<State>> list) {

        Set<State> ret = new HashSet<>();

        cartesian(sketch, production, list, list.size() - 1, new State[production.rank], ret);

        return ret;
    }

    private void cartesian(String sketch, Production production, List<Collection<State>> list, int currIndex, State[] currStates, Set<State> ret) {

        System.out.println("Production:" + production);

        if (currIndex == -1) {

            int numOfExamples = this.examples.length;
            boolean valid = true;
            Value[] returnValues = new Value[numOfExamples];
            {
                Value[][] argsForExamples = new Value[numOfExamples][production.rank];
                for (int i = 0; i < production.rank; i ++) {
                    System.out.println("currState:" + currStates[i]);
                    Value[] currValues = currStates[i].values;
                    for (int j = 0; j < numOfExamples; j ++) {
                        argsForExamples[j][i] = currValues[j];
                    }
                    if (!currStates[i].valid) valid = false;
                }
                for (int i = 0; i < returnValues.length; i ++) {
                    Value[] args = argsForExamples[i];
                    Value returnValue = production.exec(this.examples[i], args);
                    returnValues[i] = returnValue;
                }
                State returnState = this.fta.mkNonVarState(sketch, returnValues);
                returnState.valid = valid;

                System.out.println("returnState:" + returnState);

                ret.add(returnState);

                // update FTA
                {

                    // update states
                    this.fta.sketchToStates.put(sketch, returnState);

                    State[] argumentStates = new State[currStates.length];
                    System.arraycopy(currStates, 0, argumentStates, 0, argumentStates.length);
                    Transition transition = this.fta.mkTransition(production, argumentStates, returnState);

                    // update transitions
                    this.fta.transitions.add(transition);
                }

            }

            return;
        }

        for (State state : list.get(currIndex)) {
            currStates[currIndex] = state;
            cartesian(sketch, production, list, currIndex - 1, currStates, ret);
        }

    }

    //
    //
    //
    //

    @Override
    public Object visitProg(ProgContext ctx) {

        String sketch = ctx.getText();

        Value[] values = (Value[]) visit(ctx.program());

        State state = this.fta.mkNonVarState(sketch, values);
        state.minCost = 1;

        // update FTA
        {
            this.fta.sketchToStates.put(sketch, state);
        }

        Set<State> ret = new HashSet<>();
        ret.add(state);

        return ret;
    }

    @Override
    public Object visitFreeVarSketch(FreeVarSketchContext ctx) {

        String sketch = ctx.getText();

        SetMultiMap<Symbol, VarState> initStates = new SetMultiMap<>();
        {
            for (TerminalSymbol terminalSymbol : this.grammar.terminalSymbols) {

                if (terminalSymbol instanceof ConstantTerminalSymbol) {

                    // k
                    ConstantTerminalSymbol constantTerminalSymbol = (ConstantTerminalSymbol) terminalSymbol;
                    for (Value value : constantTerminalSymbol.valueToCost.keySet()) {
                        double cost = constantTerminalSymbol.valueToCost.get(value);
                        Value[] values = new Value[this.examples.length];
                        for (int i = 0; i < values.length; i ++) {
                            values[i] = value;
                        }

                        VarState state = this.fta.mkVarState(sketch, constantTerminalSymbol, values);
                        state.minCost = cost;

                        initStates.put(terminalSymbol, state);
                    }

                } else if (terminalSymbol instanceof NullaryTerminalSymbol) {

                    if (!this.grammar.appliedTerminalSymbols.contains(terminalSymbol))
                        continue;

                    // char class
                    NullaryTerminalSymbol nullaryTerminalSymbol = (NullaryTerminalSymbol) terminalSymbol;
                    Value[] values = new Value[this.examples.length];

                    for (int i = 0; i < values.length; i ++) {
                        Example e = this.examples[i];
                        values[i] = nullaryTerminalSymbol.exec(e);
                    }

                    System.out.println("values for eval terminal symbols:" +  Arrays.toString(values));

                    VarState state = this.fta.mkVarState(sketch, nullaryTerminalSymbol, values);
                    state.valid = false;
                    state.minCost = nullaryTerminalSymbol.cost;

                    initStates.put(terminalSymbol, state);

                } else {
                    throw new RuntimeException();
                }
            }
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = constructFTA(sketch, initStates);
        }

        return ret;

    }

    @Override
    public Object visitGuardedVarSketch(GuardedVarSketchContext ctx) {

        String sketch = ctx.getText();

        System.out.println("sketch:" + sketch.toString());

        SetMultiMap<Symbol, VarState> initialNewStates = new SetMultiMap<>();
        LinkedList<Transition> stitches = new LinkedList<>();
        {
            // k and char classes
            {
                for (TerminalSymbol terminalSymbol : this.grammar.terminalSymbols) {

                    if (terminalSymbol instanceof ConstantTerminalSymbol) {

                        // k
                        ConstantTerminalSymbol constantTerminalSymbol = (ConstantTerminalSymbol) terminalSymbol;
                        for (Value value : constantTerminalSymbol.valueToCost.keySet()) {

//              System.out.println("value: " + value);

                            double cost = constantTerminalSymbol.valueToCost.get(value);

                            Value[] values = new Value[this.examples.length];
                            for (int i = 0; i < values.length; i ++) {
                                values[i] = value;
                            }

                            VarState state = this.fta.mkVarState(sketch, constantTerminalSymbol, values);
                            state.minCost = cost;

                            initialNewStates.put(terminalSymbol, state);
                        }

                    } else if (terminalSymbol instanceof NullaryTerminalSymbol) {

                        if (!this.grammar.appliedTerminalSymbols.contains(terminalSymbol))
                            continue;

                        // char class
                        NullaryTerminalSymbol nullaryTerminalSymbol = (NullaryTerminalSymbol) terminalSymbol;
                        Value[] values = new Value[this.examples.length];

                        System.out.println("terminalSymbol:" + nullaryTerminalSymbol.toString());

                        for (int i = 0; i < values.length; i ++) {
                            Example e = this.examples[i];
                            values[i] = nullaryTerminalSymbol.exec(e);
                        }

                        System.out.println("values for eval terminal symbols:" +  Arrays.toString(values));

                        VarState state = this.fta.mkVarState(sketch, nullaryTerminalSymbol, values);
                        state.valid = false;
                        state.minCost = nullaryTerminalSymbol.cost;

                        initialNewStates.put(terminalSymbol, state);

                    } else {
                        throw new RuntimeException();
                    }
                }
            }

            // sketches
            {
                // TODO(XINYU): the symbol r could take any value that can be taken by lsketch
                // we are creating states for symbol r because r can take those values
                // this is effectively augmenting r's grammar in the following way:
                // r := <NUM> | ... | startwith( r ) | ... | startwith( value_1 ) | ...
                // where value_i is a value that can be taken by lsketch
                Symbol symbol = this.grammar.nameToSymbol.get("r");
                @SuppressWarnings("unchecked")
                Set<State> argStates = (Set<State>) visit(ctx.lsketch());
                for (State argState : argStates) {
                    Value[] values = argState.values;

                    // TODO(XINYU): since this is not an initial (leaf) state (of the whole FTA)
                    // we do not need to assign a cost here
                    VarState state = this.fta.mkVarState(sketch, symbol, values);
                    state.valid = true;

                    initialNewStates.put(symbol, state);

                    // TODO(XINYU): this might not be correct
                    // include transition id( argState ) -> state in the FTA
                    {
                        State[] argumentStates = new State[] { argState };
                        Production production = this.grammar.nameToProduction.get("id_dummy");
                        Transition transition = this.fta.mkTransition(production, argumentStates, state);
                        stitches.add(transition);
                    }
                }
            }
        }

        Set<State> ret = null;

    System.out.println("initialStates: " + initialNewStates);
    System.out.println("stitches: " + stitches);

        // update FTA
        {

            ret = constructFTA(sketch, initialNewStates);
            this.fta.transitions.addAll(stitches);

        }

    System.out.println("AllStates:" + ret);
    System.out.println("Transitions:" +  this.fta.transitions);

        return ret;

    }

    @Override
    public Object visitRepSketch(SketchGrammarParser.RepSketchContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Object visitStartwithSketch(StartwithSketchContext ctx) {

        String sketch = ctx.getText();

        Production production = this.grammar.nameToProduction.get("startwith");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;

    }

    @Override
    public Object visitEndwithSketch(EndwithSketchContext ctx) {
        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("endwith");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override
    public Object visitContainSketch(ContainSketchContext ctx) {
        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("contain");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);
        }

        Set<State> ret = this.constructFTA(sketch, production, list);

        return ret;
    }

    @Override public Object visitOptionalSketch(SketchGrammarParser.OptionalSketchContext ctx) {
        throw new NotImplementedException();
    }

    @Override public Object visitStarSketch(SketchGrammarParser.StarSketchContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Object visitRepeatSketch(RepeatSketchContext ctx) {

        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("repeat");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);

            Set<State> arg1States = new HashSet<>();
            {
                String arg1Sketch = ctx.INT().getText();
                int k = Integer.parseInt(arg1Sketch);

                assert (k >= 0) : k;

                IntValue value = new IntValue(k);
                Value[] arg1Values = new Value[this.examples.length];
                for (int i = 0; i < arg1Values.length; i ++) {
                    arg1Values[i] = value;
                }

                // TODO(XINYU): k itself is the sketch
                State arg1State = this.fta.mkNonVarState(arg1Sketch, arg1Values);
                arg1State.minCost = 0;
                arg1State.valid = true;

                arg1States.add(arg1State);

                // update FTA
                {
                    this.fta.sketchToStates.put(arg1Sketch, arg1State);
                }
            }
            list.add(arg1States);
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;

    }

    @Override
    public Object visitRepeatAtLeastSketch(RepeatAtLeastSketchContext ctx) {

        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("repeatatleast");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);

            Set<State> arg1States = new HashSet<>();
            {
                String arg1Sketch = ctx.INT().getText();
                int k = Integer.parseInt(arg1Sketch);

                assert (k >= 0) : k;

                IntValue value = new IntValue(k);
                Value[] arg1Values = new Value[this.examples.length];
                for (int i = 0; i < arg1Values.length; i ++) {
                    arg1Values[i] = value;
                }

                State arg1State = this.fta.mkNonVarState(arg1Sketch, arg1Values);
                arg1State.minCost = 0;
                arg1State.valid = true;

                arg1States.add(arg1State);

                // update FTA
                {
                    this.fta.sketchToStates.put(arg1Sketch, arg1State);
                }
            }
            list.add(arg1States);
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override
    public Object visitRepeatRangeSketch(RepeatRangeSketchContext ctx) {

        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("repeatrange");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);

            Set<State> arg1States = new HashSet<>();
            {
                String arg1Sketch = ctx.INT(0).getText();
                int k = Integer.parseInt(arg1Sketch);

                assert (k >= 0) : k;

                IntValue value = new IntValue(k);
                Value[] values = new Value[this.examples.length];
                for (int i = 0; i < values.length; i ++) {
                    values[i] = value;
                }

                State arg1State = this.fta.mkNonVarState(arg1Sketch, values);
                arg1State.minCost = 0;
                arg1State.valid = true;

                arg1States.add(arg1State);

                // update FTA
                {
                    this.fta.sketchToStates.put(arg1Sketch, arg1State);
                }
            }
            list.add(arg1States);

            Set<State> arg2States = new HashSet<>();
            {
                String arg2Sketch = ctx.INT(1).getText();
                int k = Integer.parseInt(arg2Sketch);

                assert (k >= 0) : k;

                IntValue value = new IntValue(k);
                Value[] values = new Value[this.examples.length];
                for (int i = 0; i < values.length; i ++) {
                    values[i] = value;
                }

                State arg2State = this.fta.mkNonVarState(arg2Sketch, values);
                arg2State.minCost = 0;
                arg2State.valid = true;

                arg2States.add(arg2State);

                // update FTA
                {
                    this.fta.sketchToStates.put(arg2Sketch, arg2State);
                }
            }
            list.add(arg2States);
        }

        Set<State> ret = null;

        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override
    public Object visitConcatSketch(ConcatSketchContext ctx) {
        String sketch = ctx.getText();
        System.out.println("sketch: " + sketch);
        Production production = this.grammar.nameToProduction.get("concat");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch(0));
            list.add(arg0States);
            @SuppressWarnings("unchecked")
            Set<State> arg1States = (Set<State>) visit(ctx.sketch(1));
            list.add(arg1States);
        }

        Set<State> ret = null;
        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override
    public Object visitNotSketch(NotSketchContext ctx) {

        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("not");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch());
            list.add(arg0States);
        }

        Set<State> ret = null;
        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override public Object visitNotCCSketch(SketchGrammarParser.NotCCSketchContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Object visitAndSketch(AndSketchContext ctx) {
        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("and");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch(0));
            list.add(arg0States);
            @SuppressWarnings("unchecked")
            Set<State> arg1States = (Set<State>) visit(ctx.sketch(1));
            list.add(arg1States);
        }

        Set<State> ret = null;
        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override
    public Object visitOrSketch(OrSketchContext ctx) {
        String sketch = ctx.getText();
        Production production = this.grammar.nameToProduction.get("or");
        List<Collection<State>> list = new ArrayList<>();
        {
            @SuppressWarnings("unchecked")
            Set<State> arg0States = (Set<State>) visit(ctx.sketch(0));
            list.add(arg0States);
            @SuppressWarnings("unchecked")
            Set<State> arg1States = (Set<State>) visit(ctx.sketch(1));
            list.add(arg1States);
        }

        Set<State> ret = null;
        // update FTA
        {
            ret = this.constructFTA(sketch, production, list);
        }

        return ret;
    }

    @Override public Object visitSepSketch(SketchGrammarParser.SepSketchContext ctx) {
        throw new NotImplementedException();
    }

    //
    //
    //

    @Override
    public Object visitSingleSketch(SingleSketchContext ctx) {
        @SuppressWarnings("unchecked")
        Set<State> ret = (Set<State>) visit(ctx.sketch());
        return ret;
    }

    @Override
    public Object visitMultiSketch(MultiSketchContext ctx) {
        @SuppressWarnings("unchecked")
        Set<State> sketchStates = (Set<State>) visit(ctx.sketch());
        @SuppressWarnings("unchecked")
        Set<State> lsketchStates = (Set<State>) visit(ctx.lsketch());
        Set<State> ret = new HashSet<>();
        ret.addAll(sketchStates);
        ret.addAll(lsketchStates);
        return ret;
    }

    //
    //
    //

    @Override
    public Object visitVar(VarContext ctx) {
        throw new RuntimeException();
    }

    //
    //
    //

    @Override
    public Object visitCharClassProg(CharClassProgContext ctx) {
        String charClassName = ctx.getText();
        NullaryTerminalSymbol charClass = (NullaryTerminalSymbol) this.grammar.nameToSymbol.get(charClassName);
        assert (charClass != null) : charClassName;
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            Example e = this.examples[i];
            Value value = charClass.exec(e);
            ret[i] = value;
        }
        return ret;
    }

    @Override
    public Object visitConstantProg(ConstantProgContext ctx) {
        String constantName = ctx.getText();
        System.out.println("constantName:" + constantName);
        NullaryTerminalSymbol constant = (NullaryTerminalSymbol) this.grammar.nameToSymbol.get(constantName);
        assert (constant != null) : constantName;
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            Example e = this.examples[i];

            System.out.println("e:" + e.toString());
            Value value = constant.exec(e);
            ret[i] = value;
        }
        return ret;
    }

    @Override public Object visitNullProg(SketchGrammarParser.NullProgContext ctx) {
        throw new RuntimeException();
    }

    @Override public Object visitEmptyProg(SketchGrammarParser.EmptyProgContext ctx) {
        throw new RuntimeException();
    }

    @Override
    public Object visitStartwithProg(StartwithProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        Production production = this.grammar.nameToProduction.get("startwith");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i]);
        }
        return ret;
    }

    @Override
    public Object visitEndwithProg(EndwithProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        Production production = this.grammar.nameToProduction.get("endwith");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i]);
        }
        return ret;
    }

    @Override
    public Object visitContainProg(ContainProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        Production production = this.grammar.nameToProduction.get("contain");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i]);
        }
        return ret;
    }

    @Override public Object visitOptionalProg(SketchGrammarParser.OptionalProgContext ctx) {
       throw new NotImplementedException();
    }

    @Override public Object visitStarProg(SketchGrammarParser.StarProgContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Object visitRepeatProg(RepeatProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        IntValue arg1 = new IntValue(Integer.parseInt(ctx.INT().getText()));
        Production production = this.grammar.nameToProduction.get("repeat");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], arg1);
        }
        return ret;
    }

    @Override
    public Object visitRepeatAtLeastProg(RepeatAtLeastProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        IntValue arg1 = new IntValue(Integer.parseInt(ctx.INT().getText()));
        Production production = this.grammar.nameToProduction.get("repeatatleast");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], arg1);
        }
        return ret;
    }

    @Override
    public Object visitRepeatRangeProg(RepeatRangeProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        IntValue arg1 = new IntValue(Integer.parseInt(ctx.INT(0).getText()));
        IntValue arg2 = new IntValue(Integer.parseInt(ctx.INT(1).getText()));
        Production production = this.grammar.nameToProduction.get("repeatrange");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], arg1, arg2);
        }
        return ret;
    }

    @Override
    public Object visitConcatProg(ConcatProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program(0));
        Value[] args1 = (Value[]) visit(ctx.program(1));
        Production production = this.grammar.nameToProduction.get("concat");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], args1[i]);
        }
        return ret;
    }

    @Override
    public Object visitNotProg(NotProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program());
        Production production = this.grammar.nameToProduction.get("not");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i]);
        }
        return ret;
    }

    @Override public Object visitNotCCProg(SketchGrammarParser.NotCCProgContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Object visitAndProg(AndProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program(0));
        Value[] args1 = (Value[]) visit(ctx.program(1));
        Production production = this.grammar.nameToProduction.get("and");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], args1[i]);
        }
        return ret;
    }

    @Override
    public Object visitOrProg(OrProgContext ctx) {
        Value[] args0 = (Value[]) visit(ctx.program(0));
        Value[] args1 = (Value[]) visit(ctx.program(1));
        Production production = this.grammar.nameToProduction.get("or");
        Value[] ret = new Value[this.examples.length];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = production.exec(this.examples[i], args0[i], args1[i]);
        }
        return ret;
    }

    @Override public Object visitSepProg(SketchGrammarParser.SepProgContext ctx) {
        throw new NotImplementedException();
    }

}