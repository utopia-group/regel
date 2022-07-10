package resnax.fta;

import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;

import resnax.Example;
import resnax.fta.DSL.NonterminalSymbol;
import resnax.fta.DSL.Production;
import resnax.fta.DSL.Symbol;
import resnax.fta.Values.IntValue;
import resnax.fta.Values.RegexValue;
import resnax.fta.Values.ErrRegex;

public class Productions {

    public static final class Id extends Production {

        public Id(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 1) : args.length;

            return args[0];

        }

    }

    // startwith(s, r)
    public static final class Startwith extends Production {

        public Startwith(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 1) : args.length;

            Value arg0 = args[0];

            if (arg0 instanceof RegexValue) {
                return exec1(e, (RegexValue) arg0);
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }

        }

        protected static Value exec1(Example e, RegexValue r0) {

            String input = e.input;

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            for (Pair<Integer, Integer> span0 : r0.value) {
                int left0 = span0.getValue0();
                int right0 = span0.getValue1();
                for (int i = right0; i <= input.length(); i ++) {
                    Pair<Integer, Integer> span = new Pair<>(left0, i);
                    value.add(span);
                }
            }

            RegexValue ret = new RegexValue(value);

            return ret;

        }

    }

    public static final class Endwith extends Production {

        public Endwith(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 1) : args.length;

            Value arg0 = args[0];

            if (arg0 instanceof RegexValue) {
                return exec1(e, (RegexValue) arg0);
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }

        }

        protected static Value exec1(Example e, RegexValue r0) {

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            for (Pair<Integer, Integer> span0 : r0.value) {
                int left0 = span0.getValue0();
                int right0 = span0.getValue1();
                for (int i = 0; i <= left0; i ++) {
                    Pair<Integer, Integer> span = new Pair<>(i, right0);
                    value.add(span);
                }
            }

            RegexValue ret = new RegexValue(value);

            return ret;
        }

    }

    public static final class Contain extends Production {

        public Contain(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {
            assert (args.length == 1) : args.length;

            Value arg0 = args[0];

            if (arg0 instanceof RegexValue) {
                return exec1(e, (RegexValue) arg0);
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }

        }

        protected static Value exec1(Example e, RegexValue r0) {

            String input = e.input;

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            for (Pair<Integer, Integer> span0 : r0.value) {
                int left0 = span0.getValue0();
                int right0 = span0.getValue1();
                for (int i = 0; i <= left0; i ++) {
                    for (int j = right0; j <= input.length(); j ++) {
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

        public Repeat(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {
            assert (args.length == 2) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof IntValue) {
                    return exec1(e, (RegexValue) arg0, (IntValue) arg1);
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }
        }

        protected static Value exec1(Example e, RegexValue r0, IntValue arg1) {

            assert (arg1.value >= 0) : arg1.value;

            Set<Pair<Integer, Integer>> retSet = new HashSet<>();

            if (arg1.value > e.input.length()) {
                return new RegexValue(retSet);
            }

            Set<Pair<Integer, Integer>> curSet = new HashSet<>();
            Set<Pair<Integer, Integer>> baseSet = r0.value;

            f(e, baseSet, curSet, 0, arg1.value, arg1.value, retSet);

            RegexValue ret = new RegexValue(retSet);

            return ret;
        }

    }

    public static final class RepeatAtLeast extends Production {

        public RepeatAtLeast(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {
            assert (args.length == 2) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof IntValue) {
                    return exec1(e, (RegexValue) arg0, (IntValue) arg1);
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }
        }

        protected static Value exec1(Example e, RegexValue r0, IntValue min) {

            assert (min.value >= 0) : min.value;

            int max = e.input.length();

            Set<Pair<Integer, Integer>> retSet = new HashSet<>();

            if (min.value > max) {
                return new RegexValue(retSet);
            }

            Set<Pair<Integer, Integer>> curSet = new HashSet<>();
            Set<Pair<Integer, Integer>> baseSet = r0.value;

            f(e, baseSet, curSet, 0, min.value, max, retSet);

            RegexValue ret = new RegexValue(retSet);

            return ret;
        }

    }

    public static final class RepeatRange extends Production {

        public RepeatRange(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {
            assert (args.length == 3) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];
            Value arg2 = args[2];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof IntValue) {
                    if (arg2 instanceof IntValue) {
                        return exec1(e, (RegexValue) arg0, (IntValue) arg1, (IntValue) arg2);
                    } else {
                        throw new RuntimeException();
                    }
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }
        }

        protected static Value exec1(Example e, RegexValue r0, IntValue min, IntValue max) {

            assert (min.value >= 0) : min.value;

            if (min.value > max.value) {
                return ErrRegex.v();
            }

            Set<Pair<Integer, Integer>> retSet = new HashSet<>();

            if (min.value > e.input.length()) {
                return new RegexValue(retSet);
            }

            Set<Pair<Integer, Integer>> curSet = new HashSet<>();
            Set<Pair<Integer, Integer>> baseSet = r0.value;

            f(e, baseSet, curSet, 0, min.value, max.value, retSet);

            RegexValue ret = new RegexValue(retSet);

            return ret;
        }

    }

    // concat( r0, r1 )
    public static final class Concat extends Production {

        public Concat(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 2) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof RegexValue) {
                    return exec1(e, (RegexValue) arg0, (RegexValue) arg1);
                } else if (arg1 instanceof ErrRegex) {
                    return arg1;
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }

        }

        protected static Value exec1(Example e, RegexValue r0, RegexValue r1) {

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            for (Pair<Integer, Integer> span0 : r0.value) {
                int left0 = span0.getValue0();
                int right0 = span0.getValue1();
                for (Pair<Integer, Integer> span1 : r1.value) {
                    int left1 = span1.getValue0();
                    int right1 = span1.getValue1();
                    if (right0 == left1) {
                        Pair<Integer, Integer> span = new Pair<>(left0, right1);
                        value.add(span);
                    }
                }
            }

            RegexValue ret = new RegexValue(value);

            return ret;

        }

    }

    public static final class Not extends Production {

        public Not(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 1) : args.length;

            Value arg0 = args[0];

            if (arg0 instanceof RegexValue) {
                return exec1(e, (RegexValue) arg0);
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }

        }

        protected static Value exec1(Example e, RegexValue r0) {
            String input = e.input;

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            Set<Pair<Integer, Integer>> currValue = r0.value;

            for (int right = 1; right <= input.length(); right ++) {
                for (int left = 0; left < right; left ++) {
                    Pair<Integer, Integer> span = new Pair<>(left, right);
                    if (!currValue.contains(span)) {
                        value.add(span);
                    }
                }
            }

            RegexValue ret = new RegexValue(value);

            return ret;

        }

    }

    public static final class And extends Production {

        public And(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {

            assert (args.length == 2) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof RegexValue) {
                    return exec1(e, (RegexValue) arg0, (RegexValue) arg1);
                } else if (arg1 instanceof ErrRegex) {
                    return arg1;
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }
        }

        protected static Value exec1(Example e, RegexValue r0, RegexValue r1) {

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            for (Pair<Integer, Integer> span0 : r0.value) {
                int left0 = span0.getValue0();
                int right0 = span0.getValue1();
                for (Pair<Integer, Integer> span1 : r1.value) {
                    int left1 = span1.getValue0();
                    int right1 = span1.getValue1();
                    if (right0 == right1) {
                        if (left0 == left1) {
                            value.add(span1);
                        }
                    }
                }
            }

            RegexValue ret = new RegexValue(value);

            return ret;

        }

    }

    public static final class Or extends Production {

        public Or(NonterminalSymbol returnSymbol, String operatorName, Symbol[] argumentSymbols, int cost) {
            super(returnSymbol, operatorName, argumentSymbols, cost);
        }

        @Override
        public Value exec(Example e, Value... args) {
            assert (args.length == 2) : args.length;

            Value arg0 = args[0];
            Value arg1 = args[1];

            if (arg0 instanceof RegexValue) {
                if (arg1 instanceof RegexValue) {
                    return exec1(e, (RegexValue) arg0, (RegexValue) arg1);
                } else if (arg1 instanceof ErrRegex) {
                    return arg1;
                } else {
                    throw new RuntimeException();
                }
            } else if (arg0 instanceof ErrRegex) {
                return arg0;
            } else {
                throw new RuntimeException();
            }
        }

        private Value exec1(Example e, RegexValue r0, RegexValue r1) {

            Set<Pair<Integer, Integer>> value = new HashSet<>();

            value.addAll(r0.value);
            value.addAll(r1.value);

            RegexValue ret = new RegexValue(value);

            return ret;

        }

    }

    // repeat helper function, for k >= 0
    public static void f(Example e, Set<Pair<Integer, Integer>> baseSet, Set<Pair<Integer, Integer>> curSet, int cur, int min, int max,
                         Set<Pair<Integer, Integer>> ret) {

        // early stopping
        if (cur > e.input.length()) {
            return;
        }

        // base case
        if (cur > max) {
            return;
        }

        // recursive
        {
            Set<Pair<Integer, Integer>> set = new HashSet<>();
            if (cur == 0) {
                String input = e.input;

                for (int i = 0; i <= input.length(); i ++) {
                    set.add(new Pair<Integer, Integer>(i, i));
                }

            } else {
                for (Pair<Integer, Integer> curPair : curSet) {
                    int curLeft = curPair.getValue0();
                    int curRight = curPair.getValue1();
                    for (Pair<Integer, Integer> basePair : baseSet) {
                        int baseLeft = basePair.getValue0();
                        int baseRight = basePair.getValue1();
                        if (baseLeft == curRight) {
                            set.add(new Pair<Integer, Integer>(curLeft, baseRight));
                        } else if (baseRight == curLeft) {
                            set.add(new Pair<Integer, Integer>(baseLeft, curRight));
                        }
                    }
                }
            }

            if (cur >= min) {
                ret.addAll(set);
            }

            f(e, baseSet, set, cur + 1, min, max, ret);
        }
    }

}