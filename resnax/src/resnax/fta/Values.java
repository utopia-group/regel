package resnax.fta;

import java.util.Iterator;
import java.util.Set;

import org.javatuples.Pair;

public class Values {

    public static final class RegexValue extends Value {

        public Set<Pair<Integer, Integer>> value;

        public RegexValue(Set<Pair<Integer, Integer>> value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return this.value.size();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof RegexValue)) return false;
            RegexValue other = (RegexValue) o;
            return other.value.equals(this.value);
        }

        @Override
        public String toString() {
            String ret = "{";
            if (this.value.isEmpty()) {
                ret += "}";
                return ret;
            }
            Iterator<Pair<Integer, Integer>> iter = this.value.iterator();
            Pair<Integer, Integer> p = iter.next();
            ret += "(" + p.getValue0() + "," + p.getValue1() + ")";
            while (iter.hasNext()) {
                p = iter.next();
                ret = ret + "," + "(" + p.getValue0() + "," + p.getValue1() + ")";
            }
            ret += "}";
            return ret;
        }

    }

    public static final class IntValue extends Value {

        public final int value;

        public IntValue(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof IntValue)) return false;
            IntValue other = (IntValue) o;
            return other.value == this.value;
        }

        @Override
        public String toString() {
            return this.value + "";
        }
    }

    public static final class ErrRegex extends Value {

        private static final ErrRegex instance = new ErrRegex();

        private ErrRegex() {

        }

        public static ErrRegex v() {
            return instance;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "ErrRegex";
        }
    }

}