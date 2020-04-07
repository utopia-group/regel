package resnax;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import resnax.synthesizer.*;
import resnax.synthesizer.DSL.CFG;
import resnax.synthesizer.solver.SolverNodes;

import java.util.*;

public class Learner {

  public static final CFG grammar_default = new CFG("");
  public CFG grammar;

  public Learner() {
    this.grammar = grammar_default;
  }

  public Learner(CFG grammar) {
    this.grammar = grammar;
  }

  public List<BenchmarkRes> learn_output_5(String sketch, List<Example> exs, String gt) {

    List<State> output = new ArrayList<>();

    Synthesizer syn = new Synthesizer(grammar);

    Main.succ = false;
    Main.syntaticErrorSketch = false;

    SketchProgram skProgram = new SketchProgram(grammar);

    SketchParser parser = new SketchParser(skProgram);
    parser.parse(sketch);


    if (skProgram.parseError) {
      Main.matchGT = false;
      Main.syntaticErrorSketch = true;
      return null;
    }

    SolverNodes.IntegerSolverNode.updateIntegerSolverNode(Main.K_MAX);

    long start = System.currentTimeMillis();


    State synProgram = syn.synthesize(skProgram, exs.toArray(new Example[exs.size()]), output);

    long end = System.currentTimeMillis();

    Main.synthesizeTime = ((end - start) / 1000.0);

    Main.succ = true;

    List<BenchmarkRes> ret = new ArrayList<>();


    SketchProgram gtProgram = new SketchProgram(grammar);
    SketchParser gtParser = new SketchParser(gtProgram);
    gtParser.parse(gt);

    // get gt regex
    String gtRegex = gtProgram.getRegex().toString();

    for (State p : output) {

      String resRegex = p.pp.getRegex().toString();

      Automaton resAtn = new RegExp(resRegex).toAutomaton();
      Automaton gtAtn = new RegExp(gtRegex).toAutomaton();

      boolean matchGT = (resAtn.equals(gtAtn));

      ret.add(new BenchmarkRes(Main.succ, p, synProgram.cost, Main.synthesizeTime, matchGT, resRegex));

    }

    return ret;

  }


  public BenchmarkRes learn(String sketch, List<Example> exs, String gt, Synthesizer syn) {

    Main.succ = false;

    SketchProgram skProgram = new SketchProgram(grammar);

    SketchParser parser = new SketchParser(skProgram);
    parser.parse(sketch);

//    System.out.println("skProgram:" + skProgram.toString());

    System.out.println("K_MAX:" + Main.K_MAX);

    SolverNodes.IntegerSolverNode.updateIntegerSolverNode(Main.K_MAX);

    long start = System.currentTimeMillis();

    State synProgram = syn.synthesize(skProgram, exs.toArray(new Example[exs.size()]), null);

    long end = System.currentTimeMillis();

    Main.synthesizeTime = ((end - start) / 1000.0);

    Main.succ = true;

    if (synProgram == null) {
      Main.leanredProgram = null;
      return null;
    } else {
      Main.leanredProgram = synProgram.toOutput();
    }

    if (gt != null) {

      SketchProgram gtProgram = new SketchProgram(grammar);
      SketchParser gtParser = new SketchParser(gtProgram);
      gtParser.parse(gt);

      // get res regex
      String resRegex = synProgram.pp.getRegex().toString();

      // get gt regex
      String gtRegex = gtProgram.getRegex().toString();

      // check res/gt equivalence
      Automaton resAtn = new RegExp(resRegex).toAutomaton();
      Automaton gtAtn = new RegExp(gtRegex).toAutomaton();

      if (resAtn.equals(gtAtn)) Main.matchGT = true;
      else Main.matchGT = false;

    }

    return new BenchmarkRes(Main.succ, synProgram, synProgram.cost, Main.synthesizeTime, Main.matchGT);
  }

  public BenchmarkRes learn(String sketch, List<Example> exs, String gt) {

    Main.skipPruningCount = 0;

    Main.totalStatesCount = 0;
    Main.addedStatesCount = 0;
    Main.polledStatesCount = 0;
    Main.prunedStatesCount = 0;
    Main.evaluatedCount = 0;
    Main.approximatedEvalCount = 0;

    Main.solverLinearCount = 0;
    Main.solverNonLinearRunningTime = 0;

    Main.solverNonLinearRunningTime = 0;
    Main.solverLinearRunningTime = 0;
    Main.subsumeGenerateTime = 0;
    Main.pruningEvaluateTime = 0;
    Main.pruningConstructionTime = 0;

    Main.subsumePrunedHit = 0;
    Main.subsumeNotPrunedHit = 0;
    Main.subsumeAvoidPruning = 0;

    Main.enumerateTime = 0;
    Main.solveTime = 0;
    Main.evaluateTime = 0;

    BenchmarkRes r;
    Synthesizer syn = new Synthesizer(grammar);

    Main.succ = false;
    Main.syntaticErrorSketch = false;

    SketchProgram skProgram = new SketchProgram(grammar);

    SketchParser parser = new SketchParser(skProgram);
    parser.parse(sketch);

    if (skProgram.parseError) {
      Main.matchGT = false;
      Main.syntaticErrorSketch = true;
      return null;
    }

    if (Main.MODE != 0) {

      if (skProgram.startNode instanceof Nodes.SketchNode) {
        int component_size = ((Nodes.SketchNode) skProgram.startNode).components.size();

        if (component_size == 1) {
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("concat"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("or"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("and"));
          Main.DEPTH_LIMIT = 2;
        } else if (component_size == 2) {
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("startwith"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("endwith"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("repeatatleast"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("star"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("contain"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("not"));
          Main.DEPTH_LIMIT = 2;
        } else if (component_size > 2) {
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("startwith"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("endwith"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("repeatatleast"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("star"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("contain"));
          syn.grammar.nonterminalSymbols.remove(this.grammar.nameToSymbol.get("not"));
          Main.DEPTH_LIMIT = 3;
        }
      }


      if (Main.DEBUG == 1) System.out.println(syn.grammar.nonterminalSymbols);

    }


    if (Main.DEBUG == 1) System.out.println("skProgram:" + skProgram);

    if (Main.DEBUG == 1) System.out.println("K_MAX:" + Main.K_MAX);

    SolverNodes.IntegerSolverNode.updateIntegerSolverNode(Main.K_MAX);

    long start = System.currentTimeMillis();

    State synProgram = syn.synthesize(skProgram, exs.toArray(new Example[exs.size()]), null);

    long end = System.currentTimeMillis();

    Main.synthesizeTime = ((end - start) / 1000.0);

    Main.succ = true;

    if (synProgram == null) {
      if (Main.MODE != 0) {
        Main.matchGT = false;
      }
      Main.leanredProgram = null;
      return null;
    } else {
      Main.leanredProgram = synProgram.toOutput();
    }

    if (gt != null) {

      SketchProgram gtProgram = new SketchProgram(grammar);
      SketchParser gtParser = new SketchParser(gtProgram);
      gtParser.parse(gt);

      // get res regex
      String resRegex = synProgram.pp.getRegex().toString();

      // get gt regex
      String gtRegex = gtProgram.getRegex().toString();

      Main.regex = resRegex;

      // check res/gt equivalence
      Automaton resAtn = new RegExp(resRegex).toAutomaton();
      Automaton gtAtn = new RegExp(gtRegex).toAutomaton();

      if (resAtn.equals(gtAtn)) Main.matchGT = true;
      else Main.matchGT = false;

    }

    return new BenchmarkRes(Main.succ, synProgram, synProgram.cost, Main.synthesizeTime, Main.matchGT);

  }

  public void learn_ablation(String sketch, List<Example> exs, String gt) {

    Synthesizer syn = null;

    if (grammar == null) {
      syn = new Synthesizer(grammar_default);
      this.learn(sketch, exs, gt, syn);
    } else {
      syn = new Synthesizer(grammar);
      this.learn(sketch, exs, gt, syn);
    }

  }


}
