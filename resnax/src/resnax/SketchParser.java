package resnax;

import java.util.HashSet;
import java.util.Set;

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
import resnax.ast.SketchGrammarParser.ConstantProgContext;
import resnax.ast.SketchGrammarParser.ContainProgContext;
import resnax.ast.SketchGrammarParser.ConcatSketchContext;
import resnax.ast.SketchGrammarParser.ContainSketchContext;
import resnax.ast.SketchGrammarParser.EmptyProgContext;
import resnax.ast.SketchGrammarParser.EndwithProgContext;
import resnax.ast.SketchGrammarParser.EndwithSketchContext;
import resnax.ast.SketchGrammarParser.FreeVarSketchContext;
import resnax.ast.SketchGrammarParser.GuardedVarSketchContext;
import resnax.ast.SketchGrammarParser.MultiSketchContext;
import resnax.ast.SketchGrammarParser.NotProgContext;
import resnax.ast.SketchGrammarParser.NotSketchContext;
import resnax.ast.SketchGrammarParser.NullProgContext;
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

import resnax.synthesizer.Node;
import resnax.synthesizer.Nodes;
import resnax.synthesizer.SketchProgram;

@SuppressWarnings("Duplicates")
public class SketchParser extends AbstractParseTreeVisitor<Object> implements SketchGrammarVisitor<Object> {

  public final SketchProgram sp;

  protected SketchParser(SketchProgram sp) {
    this.sp = sp;
  }

  public SketchProgram parse(String sketch) {
    {

      CharStream input = CharStreams.fromString(sketch);

      SketchGrammarLexer lexer = new SketchGrammarLexer(input);

      CommonTokenStream tokens = new CommonTokenStream(lexer);

      SketchGrammarParser parser = new SketchGrammarParser(tokens);

      ParseTree tree = parser.sketch();

      if (!tree.getText().equals(sketch)) {

        this.sp.parseError = true;
        return this.sp;
      }

      Node ret = (Node) this.visit(tree);

      try {
        ret.toString();
      } catch (NullPointerException e) {
        this.sp.parseError = true;
        return this.sp;
      }

      this.sp.startNode = ret;

    }

    return this.sp;

  }

  @Override public Object visitProg(ProgContext ctx) {
    Node p = (Node) visit(ctx.program());
    return p;
  }

  @Override
  // TODO: empty argument here, not sure...
  public Object visitFreeVarSketch(FreeVarSketchContext ctx) {
    Set<Node> components = new HashSet<>();

    Node ret = this.sp.mkSketchNode(components);
    return ret;
  }

  @Override public Object visitGuardedVarSketch(GuardedVarSketchContext ctx) {
    Set<Node> components = (Set<Node>) visit(ctx.lsketch());

    Node ret = this.sp.mkSketchNode(components);
    return ret;
  }

  @Override
  public Object visitRepSketch(SketchGrammarParser.RepSketchContext ctx) {

    int id = Integer.parseInt(ctx.INT().getText());

    if (this.sp.repSketches.containsKey(id)) {
      return this.sp.repSketches.get(id);
    } else {
      Node repSketch = this.sp.mkRepSketchNode((Node) visit(ctx.sketch()), id);

      this.sp.repSketches.put(id, repSketch);
      return repSketch;
    }
  }

  @Override public Object visitStartwithSketch(StartwithSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());

    Node ret = this.sp.mkOperatorNode("startwith", arg1);
    return ret;
  }

  @Override public Object visitEndwithSketch(EndwithSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());

    Node ret = this.sp.mkOperatorNode("endwith", arg1);
    return ret;
  }

  @Override public Object visitContainSketch(ContainSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());

    Node ret = this.sp.mkOperatorNode("contain", arg1);
    return ret;
  }

  @Override public Object visitOptionalSketch(SketchGrammarParser.OptionalSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());
    Node ret = this.sp.mkOperatorNode("optional", arg1);

    return ret;
  }

  @Override public Object visitStarSketch(SketchGrammarParser.StarSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());
    Node ret = this.sp.mkOperatorNode("star", arg1);

    return ret;
  }

  @Override public Object visitRepeatSketch(RepeatSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());
    int k1 = Integer.parseInt(ctx.INT().getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    Node arg2 = this.sp.mkConstantNode(k1);

    Node ret = this.sp.mkOperatorNode("repeat", arg1, arg2);
    return ret;
  }

  @Override public Object visitRepeatAtLeastSketch(RepeatAtLeastSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());
    int k1 = Integer.parseInt(ctx.INT().getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    Node arg2 = this.sp.mkConstantNode(k1);

    Node ret = this.sp.mkOperatorNode("repeatatleast", arg1, arg2);
    return ret;
  }

  @Override public Object visitRepeatRangeSketch(RepeatRangeSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());
    int k1 = Integer.parseInt(ctx.INT(0).getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    int k2 = Integer.parseInt(ctx.INT(1).getText());
    if (Main.K_MAX < k2) Main.K_MAX = k2;
    Node arg2 = this.sp.mkConstantNode(k1);
    Node arg3 = this.sp.mkConstantNode(k2);

    Node ret = this.sp.mkOperatorNode("repeatrange", arg1, arg2, arg3);
    return ret;
  }

  @Override public Object visitConcatSketch(ConcatSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch(0));
    Node arg2 = (Node) visit(ctx.sketch(1));

    Node ret = this.sp.mkOperatorNode("concat", arg1, arg2);
    return ret;
  }

  @Override public Object visitNotSketch(NotSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());

    Node ret = this.sp.mkOperatorNode("not", arg1);
    return ret;
  }

  @Override public Object visitNotCCSketch(SketchGrammarParser.NotCCSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch());

    assert (arg1 instanceof Nodes.NullaryTerminalNode || arg1 instanceof Nodes.VariableNode || arg1 instanceof Nodes.SketchNode) : "notcc only allow terminal arguments or variable or sketch";

    Node ret = this.sp.mkOperatorNode("notcc", arg1);
    return ret;
  }

  @Override public Object visitAndSketch(AndSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch(0));
    Node arg2 = (Node) visit(ctx.sketch(1));

    Node ret = this.sp.mkOperatorNode("and", arg1, arg2);
    return ret;
  }

  @Override public Object visitOrSketch(OrSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch(0));
    Node arg2 = (Node) visit(ctx.sketch(1));

    Node ret = this.sp.mkOperatorNode("or", arg1, arg2);
    return ret;
  }

  @Override public Object visitSepSketch(SketchGrammarParser.SepSketchContext ctx) {
    Node arg1 = (Node) visit(ctx.sketch(0));
    Node arg2 = (Node) visit(ctx.sketch(1));

    Node ret = this.sp.mkOperatorNode("sep", arg1, arg2);
    return ret;

  }

  @Override public Object visitSingleSketch(SingleSketchContext ctx) {
    Set<Node> components = new HashSet<Node>();

    Node parsedNode = (Node) visit(ctx.sketch());
    components.add(parsedNode);

    // if it is a cc, generate a "repeatatleast($cc,1)" and note it as a speical node
    if (Main.MODE == 0) {
      if (parsedNode instanceof Nodes.NullaryTerminalNode) {

        Nodes.NullaryTerminalNode nParsedNode = (Nodes.NullaryTerminalNode) parsedNode;

        if (nParsedNode.sym.name.equals("<let>") || nParsedNode.sym.name.equals("<num>") || nParsedNode.sym.name.equals("<alphanum>")) {

          Node arg2 = this.sp.mkConstantNode(1);
          Node special = this.sp.mkOperatorNode("repeatatleast", true, nParsedNode, arg2);
          components.add(special);

        }
      }
    }

    return components;
  }

  @Override public Object visitMultiSketch(MultiSketchContext ctx) {

    Set<Node> components = new HashSet<Node>();

    Node parsedNode = (Node) visit(ctx.sketch());
    components.add(parsedNode);

    // if it is a cc, generate a "repeatatleast($cc,1)" and note it as a speical node
    if (Main.MODE == 0) {
      if (parsedNode instanceof Nodes.NullaryTerminalNode) {
        Nodes.NullaryTerminalNode nParsedNode = (Nodes.NullaryTerminalNode) parsedNode;

        if (nParsedNode.sym.name.equals("<let>") || nParsedNode.sym.name.equals("<num>") || nParsedNode.sym.name.equals("<alphanum>")) {

          Node arg2 = this.sp.mkConstantNode(1);
          Node special = this.sp.mkOperatorNode("repeatatleast", true, nParsedNode, arg2);
          components.add(special);

        }
      }
    }

    components.addAll((Set<Node>) visit(ctx.lsketch()));
    return components;
  }

  @Override public Object visitVar(VarContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public Object visitCharClassProg(CharClassProgContext ctx) {
    String ccName = ctx.getText();
    Node ret = this.sp.mkTerminalNode(ccName);

    return ret;
  }

  @Override public Object visitConstantProg(ConstantProgContext ctx) {
    String constantName = ctx.getText();
    Node ret = this.sp.mkTerminalNode(constantName);

    return ret;
  }

  @Override public Object visitNullProg(NullProgContext ctx) {
    throw new RuntimeException();
  }

  @Override public Object visitEmptyProg(EmptyProgContext ctx) {
    throw new RuntimeException();
  }

  @Override public Object visitStartwithProg(StartwithProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    Node ret = this.sp.mkOperatorNode("startwith", arg1);

    return ret;
  }

  @Override public Object visitEndwithProg(EndwithProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    Node ret = this.sp.mkOperatorNode("endwith", arg1);

    return ret;
  }

  @Override public Object visitContainProg(ContainProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    Node ret = this.sp.mkOperatorNode("contain", arg1);

    return ret;
  }

  @Override public Object visitOptionalProg(SketchGrammarParser.OptionalProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    Node ret = this.sp.mkOperatorNode("optional", arg1);

    return ret;
  }

  @Override public Object visitStarProg(SketchGrammarParser.StarProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    Node ret = this.sp.mkOperatorNode("star", arg1);

    return ret;
  }

  @Override public Object visitRepeatProg(RepeatProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    int k1 = Integer.parseInt(ctx.INT().getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    Node arg2 = this.sp.mkConstantNode(k1);

    Node ret = this.sp.mkOperatorNode("repeat", arg1, arg2);
    return ret;
  }

  @Override public Object visitRepeatAtLeastProg(RepeatAtLeastProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    int k1 = Integer.parseInt(ctx.INT().getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    Node arg2 = this.sp.mkConstantNode(k1);

    Node ret = this.sp.mkOperatorNode("repeatatleast", arg1, arg2);
    return ret;
  }

  @Override public Object visitRepeatRangeProg(RepeatRangeProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program());
    int k1 = Integer.parseInt(ctx.INT(0).getText());
    int k2 = Integer.parseInt(ctx.INT(1).getText());
    if (Main.K_MAX < k1) Main.K_MAX = k1;
    if (Main.K_MAX < k2) Main.K_MAX = k2;
    Node arg2 = this.sp.mkConstantNode(k1);
    Node arg3 = this.sp.mkConstantNode(k2);

    Node ret = this.sp.mkOperatorNode("repeatrange", arg1, arg2, arg3);
    return ret;
  }

  @Override public Object visitConcatProg(ConcatProgContext ctx) {

    Node arg1 = (Node) visit(ctx.program(0));
    Node arg2 = (Node) visit(ctx.program(1));

    Node ret = this.sp.mkOperatorNode("concat", arg1, arg2);
    return ret;
  }

  @Override public Object visitNotProg(NotProgContext ctx) {

    Node arg1 = (Node) visit(ctx.program());

    Node ret = this.sp.mkOperatorNode("not", arg1);
    return ret;
  }

  @Override public Object visitNotCCProg(SketchGrammarParser.NotCCProgContext ctx) {

    Node arg1 = (Node) visit(ctx.program());
    assert (arg1 instanceof Nodes.NullaryTerminalNode) : "notcc only allow terminal arguments";

    Node ret = this.sp.mkOperatorNode("notcc", arg1);
    return ret;

  }

  @Override public Object visitAndProg(AndProgContext ctx) {

    Node arg1 = (Node) visit(ctx.program(0));
    Node arg2 = (Node) visit(ctx.program(1));

    Node ret = this.sp.mkOperatorNode("and", arg1, arg2);
    return ret;
  }

  @Override public Object visitOrProg(OrProgContext ctx) {

    Node arg1 = (Node) visit(ctx.program(0));
    Node arg2 = (Node) visit(ctx.program(1));

    Node ret = this.sp.mkOperatorNode("or", arg1, arg2);
    return ret;

  }

  @Override public Object visitSepProg(SketchGrammarParser.SepProgContext ctx) {
    Node arg1 = (Node) visit(ctx.program(0));
    Node arg2 = (Node) visit(ctx.program(1));

    Node ret = this.sp.mkOperatorNode("sep", arg1, arg2);
    return ret;
  }

}
