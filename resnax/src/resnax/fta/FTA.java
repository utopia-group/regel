package resnax.fta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import resnax.fta.DSL.Production;
import resnax.fta.DSL.Symbol;
import resnax.util.ListMultiMap;
import resnax.util.MultiMap;
import resnax.util.SetMultiMap;

// An FTA A = (Q, F, Q_f, \Delta)
public class FTA {

    // Q: set of states, represented by a map (sketch -> a set of states)
    public MultiMap<String, State> sketchToStates;

    // F: list of DSL operators
    public final Collection<Production> alphabet;

    // Q_f: set of final states
    public final Collection<State> finalStates;

    // \Delta: set of transitions
    public Collection<Transition> transitions;

    // the start sketch
    public String startSketch;

    // the set of initial states
    public Set<State> initStates;

    // sketches whose root node is a free variable or a guarded variable
//  public Set<String> varSketches = new HashSet<>();
    // sketches whose root node is a concrete program
//  public Set<String> pSketches = new HashSet<>();

    public FTA() {
        this.sketchToStates = new SetMultiMap<>();
        this.alphabet = new ArrayList<>();
        this.finalStates = new HashSet<>();
        this.transitions = new LinkedList<>();
        this.initStates = new HashSet<>();
    }

    public boolean isEmpty() {
        return this.sketchToStates.isEmpty();
    }

    public int numOfStates() {
        return this.sketchToStates.size();
    }

    public int numOfTransitions() {
        return this.transitions.size();
    }

    // TODO: need to consider a transition has multiple identical argument states
    public MultiMap<State, Transition> computeStateToOutTransitions() {
        MultiMap<State, Transition> ret = new ListMultiMap<>();
        for (Transition transition : this.transitions) {
            for (State argumentState : transition.argumentStates) {
                ret.put(argumentState, transition);
            }
        }
        for (String sketch : this.sketchToStates.keySet()) {
            for (State state : this.sketchToStates.get(sketch)) {
                if (!ret.containsKey(state)) {
                    Collection<Transition> transitions = new ArrayList<>();
                    ret.putAll(state, transitions);
                }
            }
        }
        return ret;
    }

    public MultiMap<State, Transition> computeStateToInTransitions() {
        MultiMap<State, Transition> ret = new ListMultiMap<>();
        for (Transition transition : this.transitions) {
            ret.put(transition.returnState, transition);
        }
        return ret;
    }

    public void removeForwardsUnreachable() {

        // reset markers
        for (Transition transition : transitions) {
            transition.n = transition.argumentStates.length;
        }

        MultiMap<State, Transition> stateToOutTransitions = computeStateToOutTransitions();

        LinkedList<State> wl = new LinkedList<>();
        MultiMap<String, State> sketchToReachableStates = new SetMultiMap<>();
        Collection<Transition> reachableTransitions = new LinkedList<>();

        // initialization
        {
            // mark all bits as false first
            for (String sketch : this.sketchToStates.keySet()) {
                for (State state : this.sketchToStates.get(sketch)) {
                    state.marked = false;
                }
            }
            // initially all final states are added into the work-list
            for (State state : this.initStates) {
                wl.add(state);
                state.marked = true;

            }
        }

        // a work-list algorithm
        while (!wl.isEmpty()) {

            State state = wl.removeFirst();

            this.sketchToStates.put(state.sketch, state);

            Collection<Transition> outTransitions = stateToOutTransitions.get(state);

            for (Transition outTransition : outTransitions) {

                int n = outTransition.n;
                n --;
                outTransition.n = n;

                State outState = outTransition.returnState;

                if (n == 0) {
                    reachableTransitions.add(outTransition);
                    if (!outState.marked) {
                        wl.addLast(outState);
                        outState.marked = true;
                    }
                }
            }
        }

        // re-construct Q and Q_f
        {
            this.sketchToStates = sketchToReachableStates;
            Collection<State> states = sketchToReachableStates.get(this.startSketch);
            this.finalStates.retainAll(states);
        }

        // re-construct \Delta
        {
            this.transitions = reachableTransitions;
        }

        // re-construct initial states
        {
            // the same as before, so no need to update
        }

    }

    public void removeBackwardsUnreachable() {

        MultiMap<State, Transition> stateToInTransitions = computeStateToInTransitions();

        LinkedList<State> wl = new LinkedList<>();
        MultiMap<String, State> sketchToReachableStates = new SetMultiMap<>();
        Collection<Transition> reachableTransitions = new LinkedList<>();
        Set<State> reachableInitStates = new HashSet<>();

        // initialization
        {
            // mark all bits as false first
            for (String sketch : this.sketchToStates.keySet()) {
                for (State state : this.sketchToStates.get(sketch)) {
                    state.marked = false;
                }
            }
            // initially all final states are added into the work-list
            for (State state : this.finalStates) {
                wl.add(state);
                state.marked = true;
            }
        }

        // a work-list algorithm to compute the backwards reachable states
        while (!wl.isEmpty()) {

            State state = wl.removeFirst();

            sketchToReachableStates.put(state.sketch, state);

            Collection<Transition> inTransitions = stateToInTransitions.get(state);

            if (inTransitions == null) {
                assert (this.initStates.contains(state)) : state;
                reachableInitStates.add(state);
                continue;
            }

            assert (!this.initStates.contains(state)) : state;
            assert (!inTransitions.isEmpty()) : state;

            for (Transition inTransition : inTransitions) {

                reachableTransitions.add(inTransition);

                State[] argumentStates = inTransition.argumentStates;

                for (State argumentState : argumentStates) {
                    if (!argumentState.marked) {
                        wl.addLast(argumentState);
                        argumentState.marked = true;
                    }
                }
            }
        }

        // re-construct Q and Q_f
        {
            this.sketchToStates = sketchToReachableStates;
            // Q_f is the same as before, so no need to update Q_f
        }

        // re-construct \Delta
        {
            this.transitions = reachableTransitions;
        }

        // re-construct initial states
        {
            this.initStates = reachableInitStates;
        }

    }

    //
    //
    //

    protected Map<Pair<String, List<Value>>, NonVarState> nonVarStateFactory = new HashMap<>(5000);
    protected int maxNonVarStateId = -1;

    protected Map<Triplet<String, Symbol, List<Value>>, VarState> varStateFactory = new HashMap<>(5000);
    protected int maxVarStateId = -1;

    public NonVarState mkNonVarState(String sketch, Value[] values) {
        List<Value> list = Arrays.asList(values);
        Pair<String, List<Value>> key = new Pair<>(sketch, list);
        NonVarState ret = this.nonVarStateFactory.get(key);
        if (ret == null) {
            this.maxNonVarStateId ++;
            ret = new NonVarState(sketch, values, this.maxNonVarStateId);
            this.nonVarStateFactory.put(key, ret);
        }
        return ret;
    }

    public VarState mkVarState(String sketch, Symbol symbol, Value[] values) {
        List<Value> list = Arrays.asList(values);
        Triplet<String, Symbol, List<Value>> key = new Triplet<>(sketch, symbol, list);
        VarState ret = this.varStateFactory.get(key);
        if (ret == null) {
            this.maxVarStateId ++;
            ret = new VarState(sketch, values, this.maxVarStateId, symbol);
            this.varStateFactory.put(key, ret);
        }
        return ret;
    }

    public Transition mkTransition(Production production, State[] argumentStates, State returnState) {
        return new Transition(production, argumentStates, returnState);
    }

    public abstract static class State {

        public final int id;

        public final String sketch;

        public final Value[] values;

        // an integer used in the ranking algorithm
        // it records the minimum cost among routes that reach this state
        public double minCost = -1;

        //
        //
        //

        // a bit for mark-and-sweep in various algorithms
        public boolean marked;

        public boolean valid;

        protected State(String sketch, Value[] values, int id) {
            this.sketch = sketch;
            this.values = values;
            this.id = id;
        }

        @Override
        public int hashCode() {
            return this.id;
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public abstract String toString();

        public abstract String toStringDetails();

    }

    // A variable state is of the form q_{sketch, symbol}^{value}
    // meaning the "symbol" in the grammar of "sketch" can evaluate to "value"
    public static final class VarState extends State {

        public final Symbol symbol;

        // an integer used in the FTA construction algorithm
        // it records the minimum recursion depth (for the symbol this state belongs to)
        // of programs that reach this state
//    public int minDepth = Integer.MAX_VALUE;

        protected VarState(String sketch, Value[] values, int id, Symbol symbol) {
            super(sketch, values, id);
            this.symbol = symbol;
        }

        @Override
        public String toString() {
//      String ret = this.sketch + "," + this.symbol + ":[";
//      for (int i = 0; i < this.values.length; i ++) {
//        if (i > 0) ret += ",";
//        ret += this.values[i];
//      }
//      ret += "]";
//      return ret;

            return "v" + this.id + "" + this.valid;
        }

        public String toStringDetails() {
            String ret = this.sketch + "," + this.symbol + ":[";
            for (int i = 0; i < this.values.length; i ++) {
                if (i > 0) ret += ",";
                ret += this.values[i];
            }
            ret += "]";
            return ret;

        }

    }

    // A non-variable state is of the form q_{sketch}^{value}
    // meaning "sketch" can evaluate to "value"
    public static final class NonVarState extends State {

        protected NonVarState(String sketch, Value[] values, int id) {
            super(sketch, values, id);
        }

        @Override
        public String toString() {
//      String ret = this.sketch + ":[";
//      for (int i = 0; i < this.values.length; i ++) {
//        if (i > 0) ret += ",";
//        ret += this.values[i];
//      }
//      ret += "]";
//      return ret;
            return "nv" + this.id + "" + this.valid;
        }

        @Override
        public String toStringDetails() {
            String ret = this.sketch + ":[";
            for (int i = 0; i < this.values.length; i ++) {
                if (i > 0) ret += ",";
                ret += this.values[i];
            }
            ret += "]";
            return ret;
        }

    }

    //
    public static class Transition {

        public final Production production;

        public final State[] argumentStates;

        public final State returnState;

        // this integer is used in various algorithms
        public int n;

        // the cost of this transition used in the ranking algorithm
        public final int cost;

        protected Transition(Production production, State[] argumentStates, State returnState) {
            this.production = production;
            this.argumentStates = argumentStates;
            this.returnState = returnState;
            this.cost = production.cost;
        }

        @Override
        public int hashCode() {
            throw new RuntimeException();
        }

        @Override
        public boolean equals(Object o) {
            throw new RuntimeException();
        }

        @Override
        public String toString() {
            String ret = this.production.operatorName + "(";
            for (int i = 0; i < this.argumentStates.length; i ++) {
                if (i > 0) ret += ",";
                ret += this.argumentStates[i];
            }
            ret += ")->" + returnState;
            return ret;
        }

    }

}
