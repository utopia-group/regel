package resnax.synthesizer;

import java.io.BufferedWriter;
import java.util.*;

import com.microsoft.z3.BoolExpr;
import resnax.EvalResult;
import resnax.Evaluate;
import resnax.Main;
import resnax.synthesizer.DSL.CFG;
import resnax.synthesizer.DSL.Symbol;
import resnax.synthesizer.DSL.TerminalSymbol;
import resnax.synthesizer.DSL.NonterminalSymbol;
import resnax.synthesizer.DSL.OpNonterminalSymbol;
import resnax.synthesizer.DSL.ConstantTerminalSymbol;
import resnax.synthesizer.DSL.NullaryTerminalSymbol;
import resnax.synthesizer.Nodes.OperatorNode;
import resnax.synthesizer.Nodes.SketchNode;
import resnax.synthesizer.Nodes.RepSketchNode;
import resnax.synthesizer.Nodes.TerminalNode;
import resnax.synthesizer.Nodes.NullaryTerminalNode;
import resnax.synthesizer.Nodes.RealConstantTerminalNode;
import resnax.synthesizer.Nodes.VariableNode;
import resnax.synthesizer.solver.Expression;
import resnax.synthesizer.solver.SolveResult;

public class Synthesizer {

  public final CFG grammar;

  private PriorityQueue<State> q;
  private Evaluate e;

  public HashSet<String> subsumePrunedProgram;
  public HashSet<String> subsumeNotPrunedProgram;

  public Synthesizer(CFG grammar) {
    Comparator<State> stateComparator = new StateComparator();
    this.q = new PriorityQueue<>(stateComparator);
    this.grammar = grammar;
    this.subsumePrunedProgram = new HashSet<>(1000000);
    this.subsumeNotPrunedProgram = new HashSet<>(1000000);
  }

  // Synthesize a program that satisfies SketchProgram and Example
  public State synthesize(SketchProgram sp, Example[] exs, List<State> output_5) {

    this.e = new Evaluate(exs);

    State initialState = initState(sp);
    q.add(initialState);

    // worklist
    while (!q.isEmpty()) {
      {

        State currState = q.poll();
        Main.polledStatesCount++;

        if (Main.PRINT == 1) System.out.println("currState:" + currState.toString());

        if (currState.pp.isCompleteSymbolic()) {

          long start_1 = System.currentTimeMillis();

          if (Main.SPECIAL_PRINT == 1) System.out.println("complete symbolic program:" + currState.pp);

          Expression ex = currState.pp.generateExpression();

          // get repeatqueue
          List<OperatorNode> repeatQueue = new ArrayList<>();
          List<OperatorNode> repeatAtLeastQueue = new ArrayList<>();
          currState.pp.startNode.addRepeatNodes(repeatQueue, repeatAtLeastQueue);

          if (Main.SOLVER_DEBUG == 1) System.out.println(repeatQueue);

          if (ex == null) continue;

          ex.generateCompleteFormula(exs);
          if (Main.SOLVER_DEBUG == 1) System.out.println(ex.toString());

          ex.generateZ3Expr();

          List<BoolExpr> expressionTemplate;
          if (Main.REPEATATLEAST_1_CONSTRAIN) {
            expressionTemplate = ex.generateTemplate(repeatAtLeastQueue);
          } else {
            expressionTemplate = ex.generateTemplate(new ArrayList<>());
          }

          for (BoolExpr currExpr : expressionTemplate) {

            ex.currExpression = currExpr;
            if (Main.SOLVER_DEBUG == 1) System.out.println("curr bool expression:" + currExpr.toString());

            SolveResult solveRes;

            while ((solveRes = ex.z3Solve()).satisfiable) {


              if (Main.SOLVER_DEBUG == 1) System.out.println("evaluate solved:" + currState.pp.toString());
              Main.evaluatedCount++;

              // do the partial evaluation on the solved program
              if (currState.pp.evaluateSolvedProgram(ex, solveRes.model, e, repeatQueue)) {
                if (Main.OUTPUT_5 == 1) {
                  output_5.add(currState);

                  if (output_5.size() >= Main.OUTPUT_5_SIZE) {
                    return currState;
                  } else continue;

                } else {
                  return currState;
                }
              }

              continue;

            }
          }
//          assert false;
          long end_1 = System.currentTimeMillis();
          Main.solveTime += (end_1 - start_1) / 1000.0;

          continue;
        }

        // check closed program
        // only evaluate closed and no symbolic constant program
        long start_2 = System.currentTimeMillis();

        if (currState.pp.varNodes.isEmpty() && currState.pp.symbolicConstantNodes.isEmpty()) {

          if (Main.DEBUG == 1) System.out.println("evaluate closed:" + currState.pp.toString());
          Main.evaluatedCount++;

          if (Main.MODE != 0) {

            // check duplicate
            if (currState.checkDuplicate()) continue;

            State retState = evaluateRepeatMutate(currState);
            if (retState != null) {
              if (Main.OUTPUT_5 == 1) {
                output_5.add(currState);

                if (output_5.size() >= Main.OUTPUT_5_SIZE) {
                  return currState;
                } else continue;

              } else {
                return currState;
              }
            }

          }

          if (e.evaluate(currState.pp.getRegex()).result) {
            if (Main.OUTPUT_5 == 1) {
              output_5.add(currState);

              if (output_5.size() >= Main.OUTPUT_5_SIZE) {
                return currState;
              } else continue;

            } else {
              return currState;
            }
          }
          else continue;
        }

        long end_2 = System.currentTimeMillis();
        Main.evaluateTime += (end_2 - start_2) / 1000.0;

        // select var node
        long start_3 = System.currentTimeMillis();

        VariableNode v;

        if (Main.MODE == 0) v = currState.pp.selectVar();
        else v = currState.pp.dr_selectVar();

        Node sketch = v.sketch;

        if (sketch instanceof TerminalNode) {


          TerminalNode terminalSketch = (TerminalNode) sketch;
          q.addAll(expandStateTerminalNode(currState, terminalSketch, v));



        } else if (sketch instanceof OperatorNode) {


          OperatorNode opSketch = (OperatorNode) sketch;
          q.addAll(expandStateOperatorNode(currState, opSketch, v));



        } else if (sketch instanceof SketchNode) {


          SketchNode skSketch = (SketchNode) sketch;

          // check if the current variable has sketch components
          if (skSketch.containsComponents()) {

            q.addAll(substitueStateSketch(currState, skSketch.components));

            List<NonterminalSymbol> hypothesis = this.grammar.nonterminalSymbols;

            for (NonterminalSymbol sym : hypothesis) {

              assert (sym instanceof OpNonterminalSymbol);
              OpNonterminalSymbol opSym = (OpNonterminalSymbol) sym;

              q.addAll(expandStateOperatorSym(currState, opSym, skSketch));

            }

          } else {

            // free var case
            List<Symbol> hypothesis = selectHypothesis(currState, v, skSketch);

            for (Symbol sym : hypothesis) {
              if (sym instanceof TerminalSymbol) {

                q.addAll(expandStateTerminalSym(currState, sym));

              } else if (sym instanceof OpNonterminalSymbol) {

                OpNonterminalSymbol opSym = (OpNonterminalSymbol) sym;
                q.addAll(expandStateOperatorSym(currState, opSym, skSketch));
              }
            }
          }


        } else if (sketch instanceof RepSketchNode) {

          RepSketchNode rsn_sketch = (RepSketchNode) sketch;

          // first check if we have the cache
          if (currState.pp.repSketchNodeMap.containsKey(rsn_sketch.sid)) {

            currState.pp.substituteVar(v, currState.pp.repSketchNodeMap.get(rsn_sketch.sid));
            q.add(currState);

            currState.pp.deselectVar();


          } else {

            // replace variable node with a new RepSketchNode
            RepSketchNode ret = (RepSketchNode) currState.pp.mkRepSketchNode(rsn_sketch.sketch, rsn_sketch.sid, v.parent);
            currState.pp.substituteVar(v, ret);
            currState.pp.deselectVar();

            // add to the hashmap


            // init ret
            VariableNode new_v = (VariableNode) currState.pp.mkVarNode(ret.sketch, ret, (v.depth + 1));
            // make this new node the selected var
            new_v.selected = 1;
            currState.pp.selectedVar = new_v;

            ret.expand = new_v;


            // gist is to repeat the previous process
            Node new_sketch = new_v.sketch;

            // TODO: involving creating new states, which needs to be changed

            if (new_sketch instanceof TerminalNode) {

              List<State> retStates = expandStateTerminalNode(currState, (TerminalNode) new_sketch, new_v);

              assert (retStates.size() == 1);

              for (State s : retStates) {
                s.pp.repSketchNodeMap.put(ret.sid, ret);
              }

              q.addAll(retStates);

            } else if (new_sketch instanceof OperatorNode) {

              List<State> retStates = expandStateOperatorNode(currState, (OperatorNode) new_sketch, new_v);

              assert (retStates.size() == 1);

              for (State s : retStates) {
                s.pp.repSketchNodeMap.put(ret.sid, ret);
              }

              q.addAll(retStates);

            } else if (new_sketch instanceof SketchNode) {

              SketchNode new_skSketch = (SketchNode) new_sketch;

              // this sketch **must** contains components
              assert (!new_skSketch.components.isEmpty());

              q.addAll(substitueStateSketch(currState, new_skSketch.components));

              List<NonterminalSymbol> hypothesis = this.grammar.nonterminalSymbols;

              for (NonterminalSymbol sym : hypothesis) {

                assert (sym instanceof OpNonterminalSymbol);
                OpNonterminalSymbol opSym = (OpNonterminalSymbol) sym;

                q.addAll(expandStateOperatorSym(currState, opSym, new_skSketch));

              }

            } else {
              throw new RuntimeException();
            }

          }

        } else {
          throw new RuntimeException();
        }
        long end_3 = System.currentTimeMillis();
        Main.enumerateTime += (end_3 - start_3) / 1000.0;
      }

    }

    return null;
  }

  // Only for NOT operator
  private State createNewState(State oldState, SketchNode sk, OpNonterminalSymbol opSym) {

    assert (opSym.prod.operatorName.equals("not"));

    State newState = new State(oldState);
    assert (oldState.pp.numRefinementSketch == newState.pp.numRefinementSketch);
    VariableNode newV = newState.pp.findSelectedVar();

    if (newV.depth == Main.DEPTH_LIMIT) return null;

    Node[] args = new Node[1];
    OperatorNode add = newState.pp.mkOperatorNode(opSym, newV.parent, args);

    add.args.set(0, newState.pp.mkVarNode(sk, add, true, false, (newV.depth + 1)));

    newState.pp.substituteVar(newV, add);
    newState.cost += opSym.prod.cost;
//    newState.cost = Math.floor(newState.cost) + opSym.prod.cost;

    if (newV.containNot) newState.cost += Main.MORE_THAN_ONE_NOT;

    assert (newState.pp.varNodes.size() >= newState.pp.numRefinementSketch) : newState.toString();
    return newState;
  }

  // mode : 0 => both variable has rf
  // mode : 1 => first variable has rf
  // mode : 2 => second variable has rf
  // mode only applies when argSize == 2
  private State createNewState(State oldState, SketchNode sk, OpNonterminalSymbol opSym, int argSize, int mode) {

    State newState = new State(oldState);
    assert (oldState.pp.numRefinementSketch == newState.pp.numRefinementSketch);
    VariableNode newV = newState.pp.findSelectedVar();

    if (newV.depth == Main.DEPTH_LIMIT) return null;

    Node[] args = new Node[argSize];
    OperatorNode add = newState.pp.mkOperatorNode(opSym, newV.parent, args);

    if (argSize == 1) {
      add.args.set(0, newState.pp.mkVarNode(sk, add, false, newV.containRepeat, (newV.depth + 1)));
      if (opSym.name.equals("startwith") || opSym.name.equals("endwith") || opSym.name.equals("contain")) {
        if (newV.containRepeat) {newState.cost += Main.SW_EW_CONTAIN_IN_REPEAT;}
      }
    } else {
      switch (mode) {
      case 0: {
        add.args.set(0, newState.pp.mkVarNode(sk, add, false, newV.containRepeat, (newV.depth + 1)));
        add.args.set(1, newState.pp.mkVarNode(sk, add, false, newV.containRepeat, (newV.depth + 1)));
        break;
      }
      case 1: {
        add.args.set(0, newState.pp.mkVarNode(add, false, newV.containRepeat, (newV.depth + 1)));
        add.args.set(1, newState.pp.mkVarNode(sk, add, false, newV.containRepeat, (newV.depth + 1)));
        break;
      }
      case 2: {
        add.args.set(0, newState.pp.mkVarNode(sk, add, false, newV.containRepeat, (newV.depth + 1)));
        add.args.set(1, newState.pp.mkVarNode(add, false, newV.containRepeat, (newV.depth + 1)));
        break;
      }
      default: {
        throw new RuntimeException();
      }
      }
    }

    newState.pp.substituteVar(newV, add);
    newState.cost += opSym.prod.cost;
//    newState.cost = Math.floor(newState.cost) + opSym.prod.cost;
    if (newV.parent != null && !(newV.parent instanceof RepSketchNode)) {
      if (((OperatorNode) newV.parent).operatorName.equals("not")) {
        if (!(opSym.name.equals("startwith") || opSym.name.equals("endwith") || opSym.name.equals("contain"))) {
          newState.cost += Main.NOT_NOT_CONTAIN_SW_EW_PATTERN;
        }
      }
    }

    assert (newState.pp.varNodes.size() >= newState.pp.numRefinementSketch) : newState.toString();
//
    return newState;
  }

  // Only for REPEAT, REPEATRANGE, REPEATATLEAST
  private State createNewState(State oldState, SketchNode sk, OpNonterminalSymbol opSym, int argSize, int[] ks) {

    if (opSym.name.contains("repeat") && Main.SYMBOLOC_ENABLED == 0 && ks == null) assert false;

    State newState = new State(oldState);
    assert (oldState.pp.numRefinementSketch == newState.pp.numRefinementSketch);

    VariableNode newV = newState.pp.findSelectedVar();

    if (newV.depth == Main.DEPTH_LIMIT) return null;

    Node[] args = new Node[argSize];
    OperatorNode add = newState.pp.mkOperatorNode(opSym, newV.parent, args);

    if (argSize == 1) {

      assert (opSym.name.equals("optional") || opSym.name.equals("star"));
      add.args.set(0, newState.pp.mkVarNode(sk, add, false, true, (newV.depth + 1)));

    } else if (argSize == 2) {

      assert (opSym.name.equals("repeat") || opSym.name.equals("repeatatleast"));
      add.args.set(0, newState.pp.mkVarNode(sk, add, false, true, (newV.depth + 1)));

      if (Main.SYMBOLOC_ENABLED == 0) {
        add.args.set(1, newState.pp.mkRealConstantNode(ks[0], add));
      } else {
        add.args.set(1, newState.pp.mkSymbolicConstantNode(add));
      }

      newState.pp.numRepeat++;
    } else if (argSize == 3) {
      assert (opSym.name.equals("repeatrange"));
      add.args.set(0, newState.pp.mkVarNode(sk, add, false, true, (newV.depth + 1)));
      if (Main.SYMBOLOC_ENABLED == 0) {
        add.args.set(1, newState.pp.mkRealConstantNode(ks[0], add));
        add.args.set(2, newState.pp.mkRealConstantNode(ks[1], add));
      } else {
        add.args.set(1, newState.pp.mkSymbolicConstantNode(add));
        add.args.set(2, newState.pp.mkSymbolicConstantNode(add));
      }

      newState.pp.numRepeat++;
    } else {
      throw new RuntimeException();
    }

    newState.pp.substituteVar(newV, add);
    newState.cost += opSym.prod.cost;
//    newState.cost = Math.floor(newState.cost) + opSym.prod.cost;

    if (newState.pp.numRepeat > Main.EXTRA_REPEAT_THRESHOLD) newState.cost += Main.EXTRA_REPEAT_COST;
    if (checkConsecutiveRepeat(add)) newState.cost += Main.CONSECUTIVE_REPEAT_COST;
    if (newV.containNot) newState.cost += Main.REPEAT_WITHIN_NOT;

    if (newV.parent != null && !(newV.parent instanceof RepSketchNode)) {
      if (((OperatorNode) newV.parent).operatorName.equals("not")) {
        newState.cost += Main.NOT_NOT_CONTAIN_SW_EW_PATTERN;
      }
    }


    assert (newState.pp.varNodes.size() >= newState.pp.numRefinementSketch) : newState.toString();

    return newState;
  }

  private Node expandStateArgNode(State s, Node curr, Node parent, int depth) {

    // TerminalNode: make a new node in s and returns the new node
    // OperatorNode: create a new operator node in s and parse the argument recursively
    // Other Node: create a new variable node with the sketch its argument and returns

    Node add = null;

    if (curr instanceof TerminalNode) {

      if (curr instanceof NullaryTerminalNode) {

        if (parent != null && !(parent instanceof RepSketchNode)) {

          if (((OperatorNode) parent).operatorName.equals("notcc")) {

            if (((TerminalNode) curr).sym.name.equals("<any>")) return add;

          } else if (((OperatorNode) parent).operatorName.equals("not")) {

            s.cost += Main.NOT_TERMINAL_PATTERN;

          }

        }

        add = s.pp.mkTerminalNode(((TerminalNode) curr).sym.name, parent);


      } else if (curr instanceof RealConstantTerminalNode) {

        RealConstantTerminalNode cn = (RealConstantTerminalNode) curr;
        add = s.pp.mkRealConstantNode(cn.k, curr.parent);

      }

      return add;

    } else if (curr instanceof OperatorNode) {

      Node[] args = new Node[((OperatorNode) curr).opSymbol.prod.argumentSymbols.length];
      add = s.pp.mkOperatorNode(((OperatorNode) curr).opSymbol, parent, args);


      if (((OperatorNode) curr).operatorName.contains("repeat")) {

        s.pp.numRepeat++;

        if (checkConsecutiveRepeat(add)) s.cost += Main.CONSECUTIVE_REPEAT_COST;

      }


      for (int i = 0; i < args.length; i++) {

        Node curr_arg = ((OperatorNode) curr).args.get(i);
        Node parsed_arg = expandStateArgNode(s, curr_arg, add, depth + 1);

        ((OperatorNode) add).args.set(i, parsed_arg);

      }

      return add;
    } else {

      add = s.pp.mkVarNode(curr, parent, depth);

      return add;

    }

  }

  // expand the state s with a terminal node
  // no new state created but only substitute v with new node
  private List<State> expandStateTerminalNode(State s, TerminalNode n, VariableNode v) {

    List<State> ret = new ArrayList<>();

    if (n instanceof NullaryTerminalNode) {

      if (v.parent != null && !(v.parent instanceof RepSketchNode)) {
        if (((OperatorNode) v.parent).operatorName.equals("notcc")) {
          if (n.sym.name.equals("<any>")) return ret;
        } else if (((OperatorNode) v.parent).operatorName.equals("not")) {
          s.cost += Main.NOT_TERMINAL_PATTERN;
        }
      }

      Node add = s.pp.mkTerminalNode(n.sym.name, v.parent);
      s.pp.substituteVar(v, add, true);

    } else if (n instanceof RealConstantTerminalNode) {

      RealConstantTerminalNode cn = (RealConstantTerminalNode) n;
      Node add = s.pp.mkRealConstantNode(cn.k, v.parent);
      s.pp.substituteVar(v, add);

    }

    if (evalApprox(s)) ret.add(s);


    return ret;
  }


  // substitute variable node v in State s with the given operator node
  // no new state should be created
  // TODO: eg: concat(v:repeat(<num>,3,)) current way doing this is expand repeat then num
  // TODO need a way to do this at a time


  private List<State> expandStateOperatorNode(State s, OperatorNode n, VariableNode v) {

    List<State> ret = new ArrayList<>();

    // do not continue if we are trying to replace argument of notcc with a op node
    if (v.parent != null && !(v.parent instanceof RepSketchNode)) {
      if (((OperatorNode) v.parent).operatorName.equals("notcc")) {
        return ret;
      }
    }

    // Added on 1014
    Node add = expandStateArgNode(s, n, v.parent, v.depth);

    s.pp.substituteVar(v, add, true);
    if (evalApprox(s)) ret.add(s);

    return ret;
  }

  // create a new state by substitute variable in oldState with the terminal symbol sym
  private List<State> expandStateTerminalSym(State oldState, Symbol sym) {

//    long start = System.currentTimeMillis();

    List<State> ret = new ArrayList<>();

    // duplicate pp
    State newState = new State(oldState);
    VariableNode newV = newState.pp.findSelectedVar();

    if (sym instanceof NullaryTerminalSymbol) {

      if (newV.parent != null) {
        if (((OperatorNode) newV.parent).operatorName.equals("notcc")) {
          if (sym.name.equals("<any>")) return ret;
        } else if (((OperatorNode) newV.parent).operatorName.equals("not")) {
          newState.cost += Main.NOT_TERMINAL_PATTERN;
        }

      }

      Node add = newState.pp.mkTerminalNode(sym.name, newV.parent);
      newState.pp.substituteVar(newV, add);
      newState.cost += ((NullaryTerminalSymbol) sym).cost;

    } else if (sym instanceof ConstantTerminalSymbol) {

      throw new RuntimeException();

    }

    if (evalApprox(newState)) ret.add(newState);


    return ret;
  }


  // create new states by substitute selected variable v in oldState with new Operator symbol
  // and update the corresponding sketch
  private List<State> expandStateOperatorSym(State oldState, OpNonterminalSymbol opSym, SketchNode sk) {


    List<State> ret = new ArrayList<>();

    // if sk is in the format of ?{temrinal}
//    if (sk.containsComponents() && sk.components.size() == 1 && sk.components.)

    // do not continue if we are trying to replace argument of notcc with a op node
    if (oldState.pp.findSelectedVar().parent != null && !(oldState.pp.findSelectedVar().parent instanceof RepSketchNode)) {
      if (((OperatorNode) oldState.pp.findSelectedVar().parent).operatorName.equals("notcc")) {
        return ret;
      }
    }

    if (opSym.name.equals("repeat") || opSym.name.equals("repeatatleast")) {

      if (Main.SYMBOLOC_ENABLED == 0) {

        for (int v = Main.K_MIN; v <= Main.K_MAX; v++) {
          State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, new int[] { v });
          if (newState != null) {
            if (Main.PRUNE_ONLY_LEAF == 0) {
              if (evalApprox(newState)) ret.add(newState);
            } else { ret.add(newState); }
          }
        }

      } else {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, null);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }

      }

    } else if (opSym.name.equals("repeatrange")) {

      if (Main.SYMBOLOC_ENABLED == 0) {

        for (int k1 = Main.K_MIN; k1 < Main.K_MAX; k1++) {

          for (int k2 = k1 + 1; k2 <= Main.K_MAX; k2++) {

            State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, new int[] { k1, k2 });
            if (newState != null) {
              if (Main.PRUNE_ONLY_LEAF == 0) {
                if (evalApprox(newState)) ret.add(newState);
              } else { ret.add(newState); }
            }
          }
        }

      } else {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, null);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

    } else if (opSym.name.equals("optional") || opSym.name.equals("star")) {

      State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, null);


//      System.out.println("op/st:" + newState);

      if (newState != null) {
        if (Main.PRUNE_ONLY_LEAF == 0) {
          if (evalApprox(newState)) ret.add(newState);
//          else{System.out.println("pruned");}
        } else { ret.add(newState); }
      }
    } else if (opSym.name.equals("concat")) {
      // A' = A[v -> \null, v1 -> A(v), v2 -> A(v)]
      {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 0);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

      // A' = A[v -> \null, v1 -> ??, v2 -> A(v)]
      {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 2);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

      // A' = A[v -> \null, v1 -> A(v), v2 -> ??]
      {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 1);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }
//      return;
    } else if (opSym.name.equals("not")) {

      {
        State newState = createNewState(oldState, sk, opSym);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

    } else if (opSym.prod.argumentSymbols.length == 1) {

      // A' = A[v -> \null, v1 -> A(v)]

      State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 0);
      if (newState != null) {
        if (Main.PRUNE_ONLY_LEAF == 0) {
          if (evalApprox(newState)) ret.add(newState);
        } else { ret.add(newState); }
      }

//      return;

    } else if (opSym.prod.argumentSymbols.length == 2) {

      // A' = A[v -> \null, v1 -> A(v), v2 -> A(v)]
      {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 0);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

      // A' = A[v -> \null, v1 -> A(v), v2 -> ??]
      {

        State newState = createNewState(oldState, sk, opSym, opSym.prod.argumentSymbols.length, 1);
        if (newState != null) {
          if (Main.PRUNE_ONLY_LEAF == 0) {
            if (evalApprox(newState)) ret.add(newState);
          } else { ret.add(newState); }
        }
      }

    } else {
      throw new RuntimeException();
    }

//    if (Main.DEBUG == 1) {
//      for (State st : ret) {
//        System.out.println("added operator sym: " + st);
//      }
//    }

//    long end = System.currentTimeMillis();

//    Main.enumerate_operatorSym += (end - start) / 1000.0;

    return ret;

  }

  // create new States that subsitute the chosen var with its corresponding sketch component
  private List<State> substitueStateSketch(State oldState, Set<Node> components) {

//    long start = System.currentTimeMillis();

    List<State> ret = new ArrayList<>();

    for (Node sk : components) {

      State newState = new State(oldState);
      VariableNode v = newState.pp.findSelectedVar();
      v.sketch = sk;

      if (sk instanceof NullaryTerminalNode) {

        NullaryTerminalNode n = (NullaryTerminalNode) sk;

        if (v.parent != null && !(v.parent instanceof RepSketchNode)) {
          if (((OperatorNode) v.parent).operatorName.equals("notcc")) {
            if (n.sym.name.equals("<any>")) continue;
          } else if (((OperatorNode) v.parent).operatorName.equals("not")) {
            newState.cost += Main.NOT_TERMINAL_PATTERN;
          }
        }

        newState.pp.numNullaryTerminals++;
      } else if (sk instanceof OperatorNode) {
        if (((OperatorNode) sk).special) newState.cost += Main.SPECIAL_REPEATATLEAST_1;

        String opName = ((OperatorNode) sk).operatorName;

        if (v.parent != null && !(v.parent instanceof RepSketchNode)) {
          if (((OperatorNode) v.parent).operatorName.equals("not")) {
            if (!(opName.equals("startwith") || opName.equals("endwith") || opName.equals("contain"))) {
              newState.cost += Main.NOT_NOT_CONTAIN_SW_EW_PATTERN;
            }
          }
        }
      } else {

        // do not continue if we are trying to replace argument of notcc with a op node
        if (v.parent != null && !(v.parent instanceof RepSketchNode)) {
          if (((OperatorNode) v.parent).operatorName.equals("notcc")) {
            continue;
          }
        }
      }

      newState.pp.numOperatorSketch++; // TODO: it might be a problem if we have a rf sketch such as concat(v:contain(v:?{<num>}))
      newState.pp.deselectVar();
      if (evalApprox(newState)) ret.add(newState);

    }

    return ret;

  }

  private State initState(SketchProgram sp) {

    PartialProgram p = new PartialProgram(this.grammar);
    p.mkVarNode(sp.startNode, null, 1);

    return new State(p, 0.0);

  }

  private boolean evalApprox(State s) {

    if (Main.PRUNING_ENABLED == 0) {
      Main.totalStatesCount++;
      return true;
    }

    // give up pruning once the cost is beyond threshold
    if (s.cost > 1000) {
      Main.skipPruningCount++;
      Main.totalStatesCount++;
      return true;
    }

    // give up pruning if the current state does not have **any** terminal leaf node
    if (s.pp.numNullaryTerminals <= Main.MIN_NUM_TERMINAL_PRUNING) {
//      if (Main.DEBUG == 1) System.out.println("skipped pruning: " + s.pp.toString());
      Main.totalStatesCount++;
      Main.skipPruningCount++;
      return true;
    }

    String p = s.pp.toString();

    {
      if (Main.DEBUG == 1) System.out.println("approx: " + p);

      long start = System.currentTimeMillis();

      if (Main.PRUNED_SUBSUMPTION_ENABLED == 1) {
        if (subsumePrunedProgram.contains(p)) {
          if (Main.DEBUG == 1) System.out.println("subsumed pruned hit: " + p);
//        if (p.equals("or(repeat(v:<num>,7),repeat(v:<num>,10))")) assert false;

          Main.subsumePrunedHit++;
//        Main.prunedStatesCount++;
          Main.totalStatesCount++;
          return false;
        }
      }

      if (Main.NOT_PRUNED_SUBSUMPTION_ENABLED == 1) {
        if (subsumeNotPrunedProgram.contains(p)) {
          if (Main.DEBUG == 1) System.out.println("subsumed not pruned hit: " + p);

          Main.subsumeNotPrunedHit++;
          Main.totalStatesCount++;
          return true;
        }
      }

      long end = System.currentTimeMillis();
      Main.subsumeCheckingTime += ((end - start) / 1000.0);
    }

    RegexProgram approxS;
    EvalResult res;

    {
      long start = System.currentTimeMillis();

      approxS = s.pp.getApproximate();

      // calculate the approximation in a different way if the program is a complete symbolic program

      if (Main.DEBUG == 1) System.out.println("evaluate approx:" + approxS);

      long end = System.currentTimeMillis();
      Main.pruningConstructionTime += ((end - start) / 1000.0);
    }

    {
      long start = System.currentTimeMillis();

      res = e.evaluateApprox(approxS);

      long end = System.currentTimeMillis();
      Main.pruningEvaluateTime += ((end - start) / 1000.0);

    }

//    if (Main.DEBUG == 1) System.out.println("generate subsumption");

    {
      long start = System.currentTimeMillis();

      if (s.pp.varNodes.size() - s.pp.numOperatorSketch != 0) {
        if (res.result) {
          if (Main.NOT_PRUNED_SUBSUMPTION_ENABLED == 1) {
            this.subsumeNotPrunedProgram.addAll(s.pp.generateNotPrunedSubsumeProgram());
          }
        } else {
          if (Main.PRUNED_SUBSUMPTION_ENABLED == 1) {
            this.subsumePrunedProgram.addAll(s.pp.generatePrunedSubsumeProgram());
          }
        }
      } else {
//      if(Main.DEBUG == 1) System.out.println("res:" + res + ", approx0: " +approxS[0] + ", approx1: " + approxS[1]);

      }

      long end = System.currentTimeMillis();
      Main.subsumeGenerateTime += ((end - start) / 1000.0);
    }

    {
        if (!(res.result)) {
          if (Main.DEBUG == 1) System.out.println("pruned: " + p);
          Main.prunedStatesCount++;
        } else {
          if (Main.DEBUG == 1) System.out.println("not prunted: " + p);
        }

    }

    Main.totalStatesCount++;

    return res.result;
  }

  // evalaute function hack for deepregex and kb13
  private State evaluateRepeatMutate(State currState) {

    int prev_k = -1;

    // mutate repeat related stuff
    PartialProgram currPP = currState.pp;

    if (currPP.startNode instanceof OperatorNode) {

      OperatorNode opNode = ((OperatorNode) currPP.startNode);

      if ((opNode.operatorName.equals("repeatatleast")) || (opNode.operatorName.equals("repeat"))) {

        if (opNode.args.get(0) instanceof OperatorNode) {

          OperatorNode opArg = (OperatorNode) opNode.args.get(0);
          RealConstantTerminalNode opInt = (RealConstantTerminalNode) opNode.args.get(1);

          if ((Main.MODE == 1 && opArg.operatorName.equals("concat")) || (Main.MODE == 2 && (opArg.operatorName.equals("concat") || opArg.operatorName
                  .equals("contain")))) {

            if (opInt.k % 2 == 0) {
              prev_k = opInt.k;
              opInt.k = opInt.k / 2;

              if (e.evaluate(currState.pp.getRegex()).result) return currState;
              else {
                opInt.k = prev_k;
                if (e.evaluate(currState.pp.getRegex()).result) return currState;
                else return null;
              }
            }
          }
        }
      }
    }

    return null;

  }

  private List<Symbol> selectHypothesis(State st, VariableNode v, SketchNode sk) {

    List<Symbol> hypothesis = new ArrayList<>();

    hypothesis.addAll(this.grammar.appliedTerminalSymbols);
    hypothesis.addAll(this.grammar.nonterminalSymbols);

    return hypothesis;
  }

  private boolean checkConsecutiveRepeat(Node n) {
    if (n.parent == null || (n.parent instanceof RepSketchNode)) return false;

    String opName = ((OperatorNode) n.parent).operatorName;

    if (opName.contains("repeat") || opName.equals("star") || opName.contains("optional")) return true;

    return false;
  }

  @Override public int hashCode() {
    throw new RuntimeException();
  }

  @Override public boolean equals(Object obj) {
    throw new RuntimeException();
  }

  @Override public String toString() {
    throw new RuntimeException();
  }

}