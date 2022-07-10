package resnax;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import resnax.synthesizer.RegexProgram;

import java.util.List;
import java.util.regex.Pattern;

import static resnax.Main.AUTOMATON_MINIMIZE;

@SuppressWarnings("Duplicates") public class Evaluate {

  public final Example[] exs;

  public Evaluate(Example[] exs) {
    this.exs = exs;
  }

  public Evaluate(List<Example> exs) {
    this.exs = exs.toArray(new Example[exs.size()]);
  }

  public EvalResult evaluate(RegexProgram rp) {

    boolean dont_match_any_positive = true;
    boolean dont_match_some_positive = false;

    if (Main.USE_PATTERN_LIB == 1 && !rp.containAndorNot) {

      Pattern pattern = Pattern.compile(rp.programs[0].toString());

      for (Example e : this.exs) {
        if (pattern.matcher(e.input).matches()) {
          if (e.output == false) return new EvalResult(false, 2);
          else dont_match_any_positive = false;
        } else {
//          if (e.output == true) return new EvalResult(false, e.output);
          if (e.output == true) dont_match_some_positive = true;
        }
      }

      if (dont_match_any_positive) return new EvalResult(false, 1);
      if (dont_match_some_positive) return new EvalResult(false, 0);
      return new EvalResult(true);

    } else {

      // call the automaton library
      RegExp r = new RegExp(rp.programs[0].toString());

      Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

      for (Example e : this.exs) {
        if (a.run(e.input)) {
          if (e.output == false) return new EvalResult(false, 2);
          else dont_match_any_positive = false;
        } else {
//          if (e.output == true) return new EvalResult(false, e.output);
          if (e.output == true) dont_match_some_positive = true;
        }
      }

//      System.out.println("matched!");
      if (dont_match_any_positive) return new EvalResult(false, 1);
      if (dont_match_some_positive) return new EvalResult(false, 0);
      return new EvalResult(true);
    }

  }

//  public EvalResult evaluate(RegexProgram rp) {
//
//    boolean dont_match_any_positive = true;
//    boolean dont_match_some_positive = false;
//
////    if (rp.containAndorNot) {
//      // call the automaton library
//      RegExp r = new RegExp(rp.programs[0].toString());
//
//      Automaton a = r.toAutomaton();
//
//
//      for (Example e : this.exs) {
//        if (a.run(e.input)) {
//          if (e.output == false) return new EvalResult(false, 2);
//          else dont_match_any_positive = false;
//        } else {
////          if (e.output == true) return new EvalResult(false, e.output);
//          if (e.output == true) dont_match_some_positive = true;
//        }
//      }
//
////      System.out.println("matched!");
//    if (dont_match_any_positive) return new EvalResult(false, 1);
//    if (dont_match_some_positive) return new EvalResult(false, 0);
//      return new EvalResult(true);
//
////    } else {
////      // call java library
////      Pattern pattern = Pattern.compile(rp.programs[0].toString());
////
////      for (Example e : this.exs) {
////        if (pattern.matcher(e.input).matches()) {
////          if (e.output == false) return new EvalResult(false, 2);
////          else dont_match_any_positive = false;
////        } else {
//////          if (e.output == true) return new EvalResult(false, e.output);
////          if (e.output == true) dont_match_some_positive = true;
////        }
////      }
////
////      if (dont_match_any_positive) return new EvalResult(false, 1);
////      if (dont_match_some_positive) return new EvalResult(false, 0);
////      return new EvalResult(true);
////    }
//  }

  public boolean evaluate(String program) {

    RegExp r = new RegExp(program);

    Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

    for (int i = 0; i < this.exs.length; i++) {
      if (a.run(this.exs[i].input)) {
        if (this.exs[i].output == false) {
          return false;
        }
      } else {
        if (this.exs[i].output == true) {
          return false;
        }
      }
    }

//      System.out.println("matched!");
    return true;

  }

  public EvalResult evaluateApprox(RegexProgram rp) {

    Main.approximatedEvalCount++;

    // negative
    {

      if (Main.DEBUG == 1) System.out.println("negative: automaton");
      RegExp r = new RegExp(rp.programs[1].toString());

      Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

      for (Example e : this.exs) {
        if (e.output) continue;
        if (a.run(e.input)) return new EvalResult(false, 2);
      }

    }

    // Positive
    {

      boolean dont_match_any_positive = true;
      boolean dont_match_some_positive = false;

      if (Main.USE_PATTERN_LIB == 1 && !rp.containAndorNot) {

        if (Main.DEBUG == 1) System.out.println("positive: run pattern");

        Pattern pattern = Pattern.compile(rp.programs[0].toString());

        for (Example e : this.exs) {
          if (!e.output) continue;
          if (pattern.matcher(e.input).matches()) dont_match_any_positive = false;
          else dont_match_some_positive = true;
//          if (!pattern.matcher(e.input).matches()) return new EvalResult(false, true);
        }

      } else {

        if (Main.DEBUG == 1) System.out.println("positive: run automaton");

        try {

          RegExp r = new RegExp(rp.programs[0].toString());
          Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

          for (Example e : this.exs) {
            if (!e.output) continue;
            if (a.run(e.input)) dont_match_any_positive = false;
            else dont_match_some_positive = true;
//            if (!a.run(e.input)) return new EvalResult(false, true);
          }

        } catch (Exception e) {
          System.out.println("regex:" + rp.programs[0]);
          assert false;
        }

      }

      if (dont_match_any_positive) return new EvalResult(false, 1);
      if (dont_match_some_positive) return new EvalResult(false, 0);
    }

    return new EvalResult(true);

  }

//  // NOTE: have to use automaton library to evaluate under-approximation
//  public EvalResult evaluateApprox(RegexProgram rp) {
//
//    Main.approximatedEvalCount++;
//
//    // negative
//    {
//
//      if (Main.DEBUG == 1) System.out.println("negative: automaton");
//      RegExp r = new RegExp(rp.programs[1].toString());
//
//      Automaton a = r.toAutomaton();
//
//      for (Example e : this.exs) {
//        if (e.output) continue;
//        if (a.run(e.input)) return new EvalResult(false, 2);
//      }
//
//    }
//
//    // Positive
//    {
//
//      boolean dont_match_any_positive = true;
//      boolean dont_match_some_positive = false;
//
////      if (rp.containAndorNot) {
//
//        if (Main.DEBUG == 1) System.out.println("positive: run automaton");
//
//        try {
//
//          RegExp r = new RegExp(rp.programs[0].toString());
//          Automaton a = r.toAutomaton();
//
//          for (Example e : this.exs) {
//            if (!e.output) continue;
//            if (a.run(e.input)) dont_match_any_positive = false;
//            else dont_match_some_positive = true;
////            if (!a.run(e.input)) return new EvalResult(false, true);
//          }
//
//        } catch (Exception e) {
//          System.out.println("regex:" + rp.programs[0]);
//          assert false;
//        }
//
////      } else {
////
////        if (Main.DEBUG == 1) System.out.println("positive: run pattern");
////
////        Pattern pattern = Pattern.compile(rp.programs[0].toString());
////
////        for (Example e : this.exs) {
////          if (!e.output) continue;
////          if (pattern.matcher(e.input).matches()) dont_match_any_positive = false;
////          else dont_match_some_positive = true;
//////          if (!pattern.matcher(e.input).matches()) return new EvalResult(false, true);
////        }
////      }
//
//      if (dont_match_any_positive) return new EvalResult(false, 1);
//      if (dont_match_some_positive) return new EvalResult(false, 0);
//    }
//
//    return new EvalResult(true);
//
//  }

  public boolean evaluateApprox(String[] approx) {

    Main.approximatedEvalCount++;

    // positive
    {
      try {

        RegExp r = new RegExp(approx[0]);
        Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

        for (int i = 0; i < this.exs.length; i++) {

          if (this.exs[i].output == false) continue;

          if (a.run(this.exs[i].input) == false) return false;

        }

      } catch (Exception e) {
        System.out.println("regex:" + approx[0]);
        assert false;
      }

    }

    // negative
    {
      RegExp r = new RegExp(approx[1]);

      Automaton a = r.toAutomaton(AUTOMATON_MINIMIZE);

      for (int i = 0; i < this.exs.length; i++) {

        if (this.exs[i].output) continue;

        if (a.run(this.exs[i].input)) return false;
      }

    }

    return true;

  }
}
