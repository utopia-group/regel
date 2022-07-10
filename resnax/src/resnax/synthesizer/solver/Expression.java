package resnax.synthesizer.solver;

import com.microsoft.z3.*;
import resnax.Main;
import resnax.Example;

import resnax.synthesizer.Nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expression {

  public static Context ctx = new Context();
  public static DSL.CFG expressionGrammar = new DSL.CFG();

  public boolean Nonlinear;

  private SolverNode root;
  private int begin_node_id;
  private int node_id;
  private Map<String, Nodes.SymbolicConstantTerminalNode> symbolicConstantNodes;
  private Map<String, SolverNodes.LengthSolverNode> lengthSolverNodes;
  private Map<String, ArithExpr> symbolicConstantZ3Symbol;
  private SolverNodes.LengthSolverNode rootSNode;

  private SolverNode formula;

  public BoolExpr boolExpression;
  public BoolExpr currExpression;

  public Expression(int node_id_max, SolverNodes.LengthSolverNode rootSNode, Map<String, Nodes.SymbolicConstantTerminalNode> symbolicConstantNodes) {
    this.node_id = node_id_max;
    this.begin_node_id = this.node_id;
    this.rootSNode = rootSNode;
    this.symbolicConstantNodes = symbolicConstantNodes;
    this.lengthSolverNodes = new HashMap<>();
    this.symbolicConstantZ3Symbol = new HashMap<>();
    this.Nonlinear = false;
  }

  public Expression init_expression(SolverNode root) {
    this.root = root;
    return this;
  }

  public SolverNode copyNode(SolverNode curr, Map old_new_node_map) {

    assert (curr != null);
//    if (Main.SOLVER_DEBUG == 1) System.out.println("curr: " + curr);

    SolverNode retNode;

    if (curr instanceof SolverNodes.NonterminalSolverNode) {
      retNode = mkNonTerminalNode(curr, old_new_node_map);
    } else if (curr instanceof SolverNodes.LengthSolverNode) {
      retNode = mkLengthSolverNode((SolverNodes.LengthSolverNode) curr, old_new_node_map);
    } else if (curr instanceof SolverNodes.SymbolicConstantSolverNode) {
      retNode = curr;
    } else if (curr instanceof SolverNodes.IntegerSolverNode) {
      retNode = curr;
    } else {
      throw new RuntimeException();
    }
    return retNode;
  }

  public SolverNode generateCompleteFormula(Example[] exs) {

    boolean useOld = true;
    for (int i = 0; i < exs.length; i++) {
      if (exs[i].output) {
        SolverNodes.IntegerSolverNode exsLength;

        if (Main.MODE == 1) {

          if (exs[i].input.length() > Main.K_MAX) exsLength = Expression.mkIntNode(Main.K_MAX);
          else exsLength = Expression.mkIntNode(Main.K_MAX);

        } else {

          exsLength = Expression.mkIntNode(exs[i].input.length());

        }

        assert (exsLength != null);

        if (useOld) {
          this.formula = mkConnectiveNode("and", this.root, mkArithmeticNode("eq", rootSNode, exsLength));
          if (Main.SOLVER_DEBUG == 1) System.out.println("useOld:" + this.formula);
          useOld = false;
          continue;

        }
        Map<Integer, Integer> old_new_node_map = new HashMap<>();
        SolverNode newRoot = mkConnectiveNode("and", copyNode(this.root, old_new_node_map),
            mkArithmeticNode("eq", mkLengthSolverNode(this.begin_node_id), exsLength));
        this.formula = mkConnectiveNode("and", this.formula, newRoot);
        this.begin_node_id = node_id;
//        if (Main.SOLVER_DEBUG == 1) System.out.println("copied:" + newRoot);
      }
    }

    for (Nodes.SymbolicConstantTerminalNode sc : this.symbolicConstantNodes.values()) {
      this.formula = mkConnectiveNode("and", this.formula, sc.generateMinMaxLengthConstraint());
    }

    for (SolverNodes.LengthSolverNode lsn : this.lengthSolverNodes.values()) {
      this.formula = mkConnectiveNode("and", this.formula, mkArithmeticNode("geq", lsn, mkIntNode(0)));
    }

    if (this.symbolicConstantNodes.size() == 2) {
      if (this.symbolicConstantNodes.get("c0").parent.equals(this.symbolicConstantNodes.get("c1").parent)) {
        this.formula = mkConnectiveNode("and", this.formula, mkPropositionNode("neq", mkSCNode(0), mkSCNode(1)));
      }
    }

    return this.formula;
  }

  public BoolExpr generateZ3Expr() {
    this.boolExpression = generateZ3BoolExpr(this.formula);
    return boolExpression;
  }

  // generate z3 formula using the generated complete formula (preprocessed)
  public BoolExpr generateZ3BoolExpr(SolverNode curr) {

    BoolExpr ret;

    assert (curr != null);

    if (curr instanceof SolverNodes.NonterminalSolverNode) {
      SolverNodes.NonterminalSolverNode currNode = (SolverNodes.NonterminalSolverNode) curr;
      switch (currNode.prod.operatorName) {
      case "and":
        ret = ctx.mkAnd(generateZ3BoolExpr(currNode.left), generateZ3BoolExpr(currNode.right));
        break;
      case "or":
        ret = ctx.mkOr(generateZ3BoolExpr(currNode.left), generateZ3BoolExpr(currNode.right));
        break;
      case "geq":
        ret = ctx.mkGe(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right));
        break;
      case "leq":
        ret = ctx.mkLe(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right));
        break;
      case "eq":
        ret = ctx.mkEq(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right));
        break;
      case "neq":
        ret = ctx.mkNot(ctx.mkEq(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right)));
        break;
      default:
        System.out.println("exception:" + curr);
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }

    return ret;
  }

  public ArithExpr generateZ3ArithExpr(SolverNode curr) {

    assert (curr != null);

    ArithExpr ret;
    if (curr instanceof SolverNodes.NonterminalSolverNode) {

      SolverNodes.NonterminalSolverNode currNode = (SolverNodes.NonterminalSolverNode) curr;

      switch (currNode.prod.operatorName) {
      case "plus":
        ret = ctx.mkAdd(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right));
        break;
      case "multiply":
        ret = ctx.mkMul(generateZ3ArithExpr(currNode.left), generateZ3ArithExpr(currNode.right));
        break;
      default:
        System.out.println("exception:" + curr);
        throw new RuntimeException();
      }
    } else if (curr instanceof SolverNodes.TerminalSolverNode) {
      if (curr instanceof SolverNodes.LengthSolverNode) {
        ret = ctx.mkIntConst(((SolverNodes.LengthSolverNode) curr).getName());
      } else if (curr instanceof SolverNodes.SymbolicConstantSolverNode) {
        ret = ctx.mkIntConst(((SolverNodes.SymbolicConstantSolverNode) curr).getName());
        ((SolverNodes.SymbolicConstantSolverNode) curr).z3symbol = ret;
        this.symbolicConstantZ3Symbol.put(((SolverNodes.SymbolicConstantSolverNode) curr).getName(), ret);
      } else if (curr instanceof SolverNodes.IntegerSolverNode) {
        ret = ctx.mkInt(((SolverNodes.IntegerSolverNode) curr).getSym().i);
      } else {
        throw new RuntimeException();
      }

    } else {
      throw new RuntimeException();
    }

    return ret;
  }

  public List<BoolExpr> generateTemplate(List<Nodes.OperatorNode> repeatAtLeastQueue) {

    List<BoolExpr> exprTemplate = new ArrayList<>();
    BoolExpr extraConstraintFormula = null;

    for (Nodes.OperatorNode curr : repeatAtLeastQueue) {
      // repeatatleast's argument always at the 1th
      Nodes.SymbolicConstantTerminalNode sc = (Nodes.SymbolicConstantTerminalNode) curr.args.get(1);
      BoolExpr eqTemp = ctx.mkEq(sc.scNode.z3symbol, ctx.mkInt(1));

      if (extraConstraintFormula == null) {
        exprTemplate.add(ctx.mkAnd(this.boolExpression, eqTemp));
        extraConstraintFormula = ctx.mkAnd(this.boolExpression, ctx.mkNot(eqTemp));
      } else {
        exprTemplate.add(ctx.mkAnd(extraConstraintFormula, eqTemp));
        extraConstraintFormula = ctx.mkAnd(extraConstraintFormula, ctx.mkNot(eqTemp));
      }

    }

    if (extraConstraintFormula == null) exprTemplate.add(this.boolExpression);
    else exprTemplate.add(extraConstraintFormula);

    return exprTemplate;
  }

  public SolveResult z3Solve() {

    double start = System.currentTimeMillis();

    Solver s = ctx.mkSolver();
//    s.add(this.boolExpression);
    s.add(this.currExpression);

    Status q = s.check();

    double end = System.currentTimeMillis();

    if (this.Nonlinear) {
      Main.solverNonLinearRunningTime += (end - start) / 1000.0;
      Main.solverNonLinearCount++;
    } else {
      Main.solverLinearRunningTime += (end - start) / 1000.0;
      Main.solverLinearCount++;
    }

    if (q == Status.SATISFIABLE) {
      return new SolveResult(true, s.getModel());
    } else {
      return new SolveResult(false, null);
    }

  }

  public void addConstantConstraint(int reason, List<Nodes.SymbolicConstantTerminalNode> addedConstants) {
    BoolExpr subsume = null;
    BoolExpr temp = null;

    for (Nodes.SymbolicConstantTerminalNode sc : addedConstants) {

      Nodes.OperatorNode parent = (Nodes.OperatorNode) sc.parent;

      if (reason == 1) {
        // don't accept any positive

        if (parent.operatorName.equals("repeatatleast")) {

          if (subsume == null) {
            subsume = ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value));
          } else {
            subsume = ctx.mkAnd(subsume, ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)));
          }

        } else if (parent.operatorName.equals("repeatrange")) {

          int addIndex = parent.args.indexOf(sc);
          assert (addIndex <= 2);
          if (addIndex == 1) {
            Nodes.SymbolicConstantTerminalNode sc2 = (Nodes.SymbolicConstantTerminalNode) parent.args.get(2);

            if (subsume == null) {
              subsume = ctx.mkOr(ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)),
                  ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc2.getName()), ctx.mkInt(sc2.value)));
            } else {
              subsume = ctx.mkAnd(subsume, ctx.mkOr(ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)),
                  ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc2.getName()), ctx.mkInt(sc2.value))));
            }
          }

        }

      } else if (reason == 2) {
        // accept any negative

        if (parent.operatorName.equals("repeatatleast")) {
          if (subsume == null) {
            subsume = ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value));
          } else {
            subsume = ctx.mkAnd(subsume, ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)));
          }
        } else if (parent.operatorName.equals("repeatrange")) {

          // findout which index is this node
          int addIndex = parent.args.indexOf(sc);
          assert (addIndex <= 2);
          if (addIndex == 1) {
            Nodes.SymbolicConstantTerminalNode sc2 = (Nodes.SymbolicConstantTerminalNode) parent.args.get(2);
            if (subsume == null) {
              subsume = ctx.mkOr(ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)),
                  ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc2.getName()), ctx.mkInt(sc2.value)));
            } else {
              subsume = ctx.mkAnd(subsume, ctx.mkOr(ctx.mkGt(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)),
                  ctx.mkLt(this.symbolicConstantZ3Symbol.get(sc2.getName()), ctx.mkInt(sc2.value))));
            }
          }

        }
      }

      if (temp == null) {
//        System.out.println("sc1:" + sc.getName() + "sc.val:" + sc.value);
        temp = ctx.mkEq(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value));
      } else {
//        System.out.println("sc3:" + sc.getName() + "sc.val:" + sc.value);
//        System.out.println("sc.getsymbol:" + this.symbolicConstantZ3Symbol.get(sc.getName()));
//        ctx.mkAnd(temp, ctx.mkEq(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)));
        temp = ctx.mkAnd(temp, ctx.mkEq(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value)));
      }
//      System.out.println("temp:" +  temp.toString());
    }

//    this.boolExpression = ctx.mkAnd(this.boolExpression, ctx.mkNot(temp));
//    if (subsume != null) {
//      this.boolExpression = ctx.mkAnd(this.boolExpression, subsume);
//    }

    this.currExpression = ctx.mkAnd(this.currExpression, ctx.mkNot(temp));
    if (subsume != null) {
      this.currExpression = ctx.mkAnd(this.currExpression, subsume);
    }
//    for (Nodes.SymbolicConstantTerminalNode sc : this.symbolicConstantNodes.values()) {
//      this.boolExpression = ctx.mkAnd(this.boolExpression, ctx.mkNot(ctx.mkEq(this.symbolicConstantZ3Symbol.get(sc.getName()), ctx.mkInt(sc.value))));
//    }
  }

  public static DSL.Production getProd(String name) {
    return expressionGrammar.nameToProduction.get(name);
  }

  public static DSL.Symbol getSym(String name) {
    return expressionGrammar.nameToSymbol.get(name);
  }

  public SolverNode mkNonTerminalNode(SolverNode sn, Map old_new_node_map) {
    if (sn instanceof SolverNodes.ConnectiveNode) {
      return mkConnectiveNode(((SolverNodes.ConnectiveNode) sn).prod, copyNode(((SolverNodes.ConnectiveNode) sn).left, old_new_node_map),
          copyNode(((SolverNodes.ConnectiveNode) sn).right, old_new_node_map));
    } else if (sn instanceof SolverNodes.PropositionNode) {
      return mkPropositionNode(((SolverNodes.PropositionNode) sn).prod, copyNode(((SolverNodes.PropositionNode) sn).left, old_new_node_map),
          copyNode(((SolverNodes.PropositionNode) sn).right, old_new_node_map));
    } else if (sn instanceof SolverNodes.ArithmeticNode) {
      return mkArithmeticNode(((SolverNodes.ArithmeticNode) sn).prod, copyNode(((SolverNodes.ArithmeticNode) sn).left, old_new_node_map),
          copyNode(((SolverNodes.ArithmeticNode) sn).right, old_new_node_map));
    } else {
      throw new RuntimeException();
    }

  }

  public static SolverNodes.ConnectiveNode mkConnectiveNode(DSL.Production prod, SolverNode l, SolverNode r) {
    return new SolverNodes.ConnectiveNode(prod, l, r);
  }

  public static SolverNodes.ConnectiveNode mkConnectiveNode(String prod, SolverNode l, SolverNode r) {
    return new SolverNodes.ConnectiveNode(getProd(prod), l, r);
  }

  public static SolverNodes.PropositionNode mkPropositionNode(DSL.Production prod, SolverNode l, SolverNode r) {
    return new SolverNodes.PropositionNode(prod, l, r);
  }

  public static SolverNodes.PropositionNode mkPropositionNode(String prod, SolverNode l, SolverNode r) {
    return new SolverNodes.PropositionNode(getProd(prod), l, r);
  }

  public static SolverNodes.ArithmeticNode mkArithmeticNode(DSL.Production prod, SolverNode l, SolverNode r) {
    return new SolverNodes.ArithmeticNode(prod, l, r);
  }

  public static SolverNodes.ArithmeticNode mkArithmeticNode(String prod, SolverNode l, SolverNode r) {
    return new SolverNodes.ArithmeticNode(getProd(prod), l, r);
  }

  public static SolverNodes.LengthSolverNode mkLengthSolverNode(int id) {
    return new SolverNodes.LengthSolverNode(getSym("s"), id);
  }

  public SolverNodes.LengthSolverNode mkLengthSolverNode(SolverNodes.LengthSolverNode old, Map old_new_node_map) {

    Integer mapped_id = (Integer) old_new_node_map.get(old.id);

    if (mapped_id == null) {
      int new_id = node_id++;
      old_new_node_map.put(old.id, new_id);
      SolverNodes.LengthSolverNode newN = new SolverNodes.LengthSolverNode(getSym("s"), new_id);
      lengthSolverNodes.put(newN.getName(), newN);
      return newN;
    } else {
      return new SolverNodes.LengthSolverNode(getSym("s"), (int) mapped_id);
    }

  }

  public static SolverNodes.SymbolicConstantSolverNode mkSCNode(int id) {
    return new SolverNodes.SymbolicConstantSolverNode(getSym("c"), id);
  }

  public static SolverNodes.IntegerSolverNode mkIntNode(int k) {

    SolverNodes.IntegerSolverNode res = SolverNodes.IntegerSolverNode.getInstance(k);

    // if (res == null) System.out.println("null int: " + k);

    return res;
  }

  @Override public String toString() {
    return this.formula.toStringBuilder(new StringBuilder()).toString();
  }

}
