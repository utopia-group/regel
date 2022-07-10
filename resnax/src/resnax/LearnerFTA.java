package resnax;

import java.util.*;

import org.javatuples.Pair;

import resnax.fta.DSL.CFG;
import resnax.fta.DSL.ConstantTerminalSymbol;
import resnax.fta.DSL.NullaryTerminalSymbol;
import resnax.fta.DSL.TerminalSymbol;
import resnax.fta.FTA;
import resnax.fta.FTA.NonVarState;
import resnax.fta.FTA.State;
import resnax.fta.FTA.Transition;
import resnax.fta.FTA.VarState;
import resnax.fta.Value;
import resnax.fta.Values.ErrRegex;
import resnax.fta.Values.IntValue;
import resnax.fta.Values.RegexValue;
import resnax.util.MultiMap;
import sun.lwawt.macosx.CPrinterDevice;

public class LearnerFTA {

    public final FTA fta;

    public final CFG grammar;

    public LearnerFTA() {
        this.fta = new FTA();
        this.grammar = new CFG("");
    }

    public LearnerFTA(CFG grammar) {
        this.fta = new FTA();
        this.grammar = grammar;
    }

    // TODO: modified the constructor to parse maxDepth
    public BenchmarkRes learn(String sketch, List<Example> examples) {
        BenchmarkRes ret = learn(sketch, examples.toArray(new Example[0]), -1);
        return ret;
    }

    public BenchmarkRes learn(String sketch, List<Example> examples, int maxDepth) {
        BenchmarkRes ret = learn(sketch, examples.toArray(new Example[0]), maxDepth);
        return ret;
    }

    // TODO: add eval so we won't modified the Main field
    // Evaluate on 1 example at a time
    public Boolean eval(String sketch, Example example) {
        Example[] examples = new Example[1];
        examples[0] = example;

        SketchParserFTA parser = new SketchParserFTA(this.grammar, this.fta, examples);
        parser.parse(sketch);

        this.fta.startSketch = sketch;
        markFinalStates(examples);

        if (fta.finalStates.isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean consistent(Value[] values, Example[] examples) {
        assert (values.length == examples.length);

        for (int i = 0; i < values.length; i ++) {
            Example example = examples[i];
            Value value = values[i];
            if (value instanceof ErrRegex) {
                return false; // TODO: if negative return ErrRegex still valid?
            } else if (value instanceof RegexValue) {
                RegexValue regexValue = (RegexValue) value;
                Pair<Integer, Integer> target = new Pair<>(0, example.input.length());
                if (example.output && !regexValue.value.contains(target)) {
                    return false;
                } else if (!example.output && regexValue.value.contains(target)) {
                    return false;
                }
            } else {
                throw new RuntimeException();
            }
        }
        return true;
    }

    public void markFinalStates(Example[] examples) {
        for (State state : this.fta.sketchToStates.get(this.fta.startSketch)) {
            if (!state.valid) continue;

            System.out.println("iterate over states: " + state.toStringDetails());
            System.out.println("valid state: " +  state.valid);

            if (state instanceof VarState) {
                VarState varState = (VarState) state;
                if ("r".equals(varState.symbol.name)) {
                    Value[] values = varState.values;
                    boolean consistent = consistent(values, examples);
                    if (consistent) {
                        this.fta.finalStates.add(state);
                    }
                }
            } else if (state instanceof NonVarState) {
                Value[] values = state.values;
                boolean consistent = consistent(values, examples);
                if (consistent) {
                    this.fta.finalStates.add(state);
                    break;
                }

            } else {
                throw new RuntimeException();
            }
        }
    }

    public void markInitialStates() {
        Set<State> internalStates = new HashSet<>();
        for (Transition transition : this.fta.transitions) {
            internalStates.add(transition.returnState);
        }
        for (String sketch : this.fta.sketchToStates.keySet()) {
            for (State state : this.fta.sketchToStates.get(sketch)) {
                if (!internalStates.contains(state)) {
                    this.fta.initStates.add(state);
                }
            }
        }
    }

    public BenchmarkRes learn(String sketch, Example[] examples, int maxDepth) {

        Main.succ = false;
        SketchParserFTA parser = new SketchParserFTA(this.grammar, this.fta, examples);

        System.out.println("examples:" +  Arrays.toString(examples));

        if (maxDepth != -1) {
            parser.maxDepth = maxDepth;
        }

        long startTime = 0, endTime = 0, ftaTime = 0, otherTime = 0, rankTime = 0;

        // parse sketch
        // construct all states and transitions in the FTA
        {
            startTime = System.currentTimeMillis();
            parser.parse(sketch);
            endTime = System.currentTimeMillis();
            ftaTime += (endTime - startTime);
        }

        System.out.println("==========================================================");
        System.out.println("construction finished ");

//    System.out.println("states:" + this.fta.sketchToStates);

        // mark start sketch
        this.fta.startSketch = sketch;

        // mark final states
        {
            startTime = System.currentTimeMillis();
            markFinalStates(examples);
            endTime = System.currentTimeMillis();
            ftaTime += (endTime - startTime);
        }

//    System.out.println("finalStates:" + this.fta.finalStates);

        // mark initial states
        {
            startTime = System.currentTimeMillis();
            markInitialStates();
            endTime = System.currentTimeMillis();
            otherTime += (endTime - startTime);
        }

        System.out.println("==========================================================");
        System.out.println("number of states: " + this.fta.numOfStates());
        System.out.println("number of transitions: " + this.fta.numOfTransitions());

        // minimization
        {
            startTime = System.currentTimeMillis();
            this.fta.removeBackwardsUnreachable();
            endTime = System.currentTimeMillis();
            otherTime += (endTime - startTime);
        }

        System.out.println("==========================================================");
        System.out.println("after pruning");
        System.out.println("number of states: " + this.fta.numOfStates());
        System.out.println("number of transitions: " + this.fta.numOfTransitions());

        // rank
        String ret = null;
        {

            startTime = System.currentTimeMillis();
            ret = rank(this.fta);
            endTime = System.currentTimeMillis();
            rankTime += (endTime - startTime);

            System.out.println("==========================================================");
            System.out.println("synthesis result: ");
            System.out.println(ret);

            System.out.println("==========================================================");

        }


//        {
//            String gtRegex = null;
//            if (gt != null) {
//                SketchProgram gtProgram = new SketchProgram(this.grammar);
//                SketchParser gtParser = new SketchParser(gtProgram);
//                gtParser.parse(gt);
//
//                // get gt regex
//                gtRegex = gtProgram.getRegex().toString();
//            }
//
//            for (resnax.synthesizer.State p : output) {
//
//                String resRegex = p.pp.getRegex().toString();
//
//                boolean matchGT = false;
//
//                if (gt != null) {
//                    Automaton resAtn = new RegExp(resRegex).toAutomaton();
//                    Automaton gtAtn = new RegExp(gtRegex).toAutomaton();
//
//                    matchGT = (resAtn.equals(gtAtn));
//                }
//            }
//        }

//        if (ret == null) {
//            Main.succ = false;
//            System.out.println("FAILED");
//        } else {
//            Main.succ = true;
//            Main.learnedProg = ret;
//            Main.totalTime = Main.ftaTime + Main.rankTime + Main.otherTime;
//            Main.maxDepth = parser.maxDepth;
//            Main.numOfStatesInFinalFTA = this.fta.numOfStates();
//            Main.numOfTransitionsInFinalFTA = this.fta.numOfTransitions();
//        }

        boolean succ = (ret == null);
        long totalTime = ftaTime + rankTime + otherTime;

        return new BenchmarkRes(succ, null, 0, totalTime, true, ret);

    }

    // NOTE: a tunable function used in computing cost in the ranking algorithm
    protected int f(Transition transition) {
        int ret = 0;
        for (State argumentState : transition.argumentStates) {
            ret += argumentState.minCost;
        }
        return ret;
    }

    public String rank(FTA fta) {

        // no program exists
        if (fta.isEmpty()) return null;

        // initialization of costs of states
        {
            MultiMap<String, State> sketchToStates = fta.sketchToStates;
            for (String sketch : sketchToStates.keySet()) {
                for (State state : sketchToStates.get(sketch)) {
                    if (!this.fta.initStates.contains(state)) {
                        state.minCost = Integer.MAX_VALUE;
                    }
                }
            }

            // sanity check
            {
                for (String sketch : sketchToStates.keySet()) {
                    for (State state : sketchToStates.get(sketch)) {
                        assert (state.minCost != -1) : state;
                    }
                }
            }
        }

        // initialization of the number of unprocessed arguments for each transition
        {
            for (Transition transition : fta.transitions) {
                transition.n = transition.production.rank;

            }
        }

        // map from each state to its in-transition in the final result
        // map[s] represents the transition (along the minimum weighted route) that flows into state s
        Map<State, Transition> prev = new HashMap<>();
        {

            class StateCostComparator implements Comparator<State> {
                @Override
                public int compare(State state1, State state2) {
                    double diff = state1.minCost - state2.minCost;
                    if (diff >= 0)
                        return 1;
                    else
                        return -1;
                }
            }

            Comparator<State> comparator = new StateCostComparator();

            // use a priority queue (heap) to achieve O(log n * size(fta)) running time complexity where n is the number of states in fta
            PriorityQueue<State> wl = new PriorityQueue<>(fta.numOfStates(), comparator);
            // initialize the work-list to include all leaf states
            {
                MultiMap<String, State> sketchToStates = fta.sketchToStates;
                for (String sketch : sketchToStates.keySet()) {
                    for (State state : sketchToStates.get(sketch)) {
                        state.marked = false;
                    }
                }
                for (State state : this.fta.initStates) {
                    wl.add(state);
                    state.marked = true;
                }
            }

            MultiMap<State, Transition> stateToOutTransitions = fta.computeStateToOutTransitions();

            // a work-list algorithm

            while (!wl.isEmpty()) {

                State state = wl.remove();
                state.marked = false;

                Collection<Transition> outTransitions = stateToOutTransitions.get(state);

                for (Transition outTransition : outTransitions) {

//          System.out.println("outTransition:" + outTransition);

                    // TODO: this is a bug if duplicated arguments are allowed in a transition
                    int n = outTransition.n;
                    n --;
                    outTransition.n = n;
                    if (n == 0) {
                        State outState = outTransition.returnState;
                        // NOTE: the additive weighting function (generalized Bellman's equations)
                        int cost = outTransition.cost + f(outTransition);
                        double minCost = outState.minCost;
                        if (cost < minCost) {
                            if (!outState.marked) {
                                // if outState is not in the current work-list, add it into the work-list
                                wl.add(outState);
                                outState.marked = true;

                                if (minCost < Integer.MAX_VALUE) {
                                    // if outState has been visited before, then all of its out-transitions must be processed again
                                    for (Transition outTransition1 : stateToOutTransitions.get(outState)) {
                                        // TODO: this also needs to be fixed if duplicated arguments are allowed
                                        int n1 = outTransition1.n;
                                        n1 ++;
                                        assert (n1 <= outTransition1.argumentStates.length);
                                        outTransition1.n = n1;
                                    }
                                }
                            }
                            outState.minCost = cost;
                            prev.put(outState, outTransition);
                        }
                    }
                }

            }
        }

//    System.out.println("prev:"+ prev);
        // construct the program tree for the minimum weighted route (encoded in prev)
        String ret = constructProgramTree(fta, prev);

        return ret;

    }

    // given the predecessor map "prev", construct a program tree which represents the best program
    private String constructProgramTree(FTA fta, Map<State, Transition> prev) {

        // find the final state with the minimum cost
        Collection<State> finalStates = fta.finalStates;
        State finalStateWithMinCost = null;
        for (State finalState : finalStates) {
            double minCost = finalState.minCost;
            if (finalStateWithMinCost == null || minCost < finalStateWithMinCost.minCost) {
                finalStateWithMinCost = finalState;
            }
        }
        assert (finalStateWithMinCost != null);

        // program tree construction
        String ret = constructProgramTree(prev, finalStateWithMinCost);

        return ret;

    }

    // construct a program tree whose root node has rootState
    private String constructProgramTree(Map<State, Transition> prev, State rootState) {

        Transition transition = prev.get(rootState);

        // base case: rootState is a leaf node (terminal state)
        if (transition == null) {
            assert (this.fta.initStates.contains(rootState)) : rootState;
            if (rootState instanceof VarState) {
                VarState varState = (VarState) rootState;
                assert (varState.symbol instanceof TerminalSymbol) : varState.symbol.getClass();
                if (varState.symbol instanceof NullaryTerminalSymbol) {
                    String ret = varState.symbol.name;
                    return ret;
                } else if (varState.symbol instanceof ConstantTerminalSymbol) {
                    Value value = varState.values[0];
                    if (value instanceof IntValue) {
                        String ret = ((IntValue) value).value + "";
                        return ret;
                    } else {
                        throw new RuntimeException();
                    }
                } else {
                    throw new RuntimeException();
                }
            } else if (rootState instanceof NonVarState) {
                String ret = rootState.sketch;
                return ret;
            } else {
                throw new RuntimeException();
            }
        }

        // recursive case: rootState is an internal node
        // either a non-variable state with an operator sketch
        // or a variable (free or guarded) state with a non-terminal symbol
        // TODO: modified this part to avoid printing "id_xxx" for example evaluation
        if (transition.production.operatorName.startsWith("id")) {
            String ret = "";
            State[] argumentStates = transition.argumentStates;
            for (int i = 0; i < argumentStates.length; i ++) {
                if (i > 0) ret += ",";
                State argumentState = argumentStates[i];
                ret += constructProgramTree(prev, argumentState);
            }
            return ret;
        } else {
            String ret = transition.production.operatorName + "(";
            State[] argumentStates = transition.argumentStates;
            for (int i = 0; i < argumentStates.length; i ++) {
                if (i > 0) ret += ",";
                State argumentState = argumentStates[i];
                ret += constructProgramTree(prev, argumentState);
            }
            ret += ")";
            return ret;
        }

    }

}