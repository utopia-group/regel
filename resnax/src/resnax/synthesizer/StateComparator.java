package resnax.synthesizer;

import java.util.Comparator;

public class StateComparator implements Comparator<State> {

  @Override public int compare(State o1, State o2) {

    if (resnax.Main.MODE == 1) {
      if (o1.cost - o2.cost == 0) {
        if (o1.ts < o2.ts) {
          return -1;
        } else {
          return 1;
        }
      } else if (o1.cost - o2.cost > 0) {
        return 1;
      } else {
        return -1;
      }
    }

    if (o1.cost - o2.cost == 0) {

      if (o1.pp.varNodes.size() == 0) {
        return -1;
      } else if (o2.pp.varNodes.size() == 0) {
        return 1;
      } else {
        return (o1.pp.varNodes.size() - o1.pp.numRefinementSketch) - (o2.pp.varNodes.size() - o2.pp.numRefinementSketch);
      }

    } else {
      return o1.compareTo(o2);
    }

//    return o1.cost - o2.cost;

  }

}
