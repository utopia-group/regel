// Generated from SketchGrammar.g4 by ANTLR 4.7.1
package resnax.ast;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SketchGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SketchGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code Prog}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(SketchGrammarParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FreeVarSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFreeVarSketch(SketchGrammarParser.FreeVarSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GuardedVarSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGuardedVarSketch(SketchGrammarParser.GuardedVarSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepSketch(SketchGrammarParser.RepSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StartwithSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStartwithSketch(SketchGrammarParser.StartwithSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndwithSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndwithSketch(SketchGrammarParser.EndwithSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContainSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainSketch(SketchGrammarParser.ContainSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OptionalSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionalSketch(SketchGrammarParser.OptionalSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StarSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStarSketch(SketchGrammarParser.StarSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatSketch(SketchGrammarParser.RepeatSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatAtLeastSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatAtLeastSketch(SketchGrammarParser.RepeatAtLeastSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatRangeSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatRangeSketch(SketchGrammarParser.RepeatRangeSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ConcatSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatSketch(SketchGrammarParser.ConcatSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotSketch(SketchGrammarParser.NotSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotCCSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotCCSketch(SketchGrammarParser.NotCCSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AndSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndSketch(SketchGrammarParser.AndSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OrSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrSketch(SketchGrammarParser.OrSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SepSketch}
	 * labeled alternative in {@link SketchGrammarParser#sketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSepSketch(SketchGrammarParser.SepSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SingleSketch}
	 * labeled alternative in {@link SketchGrammarParser#lsketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleSketch(SketchGrammarParser.SingleSketchContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultiSketch}
	 * labeled alternative in {@link SketchGrammarParser#lsketch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiSketch(SketchGrammarParser.MultiSketchContext ctx);
	/**
	 * Visit a parse tree produced by {@link SketchGrammarParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(SketchGrammarParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CharClassProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharClassProg(SketchGrammarParser.CharClassProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ConstantProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantProg(SketchGrammarParser.ConstantProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullProg(SketchGrammarParser.NullProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EmptyProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyProg(SketchGrammarParser.EmptyProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StartwithProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStartwithProg(SketchGrammarParser.StartwithProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndwithProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndwithProg(SketchGrammarParser.EndwithProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContainProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainProg(SketchGrammarParser.ContainProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OptionalProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionalProg(SketchGrammarParser.OptionalProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StarProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStarProg(SketchGrammarParser.StarProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatProg(SketchGrammarParser.RepeatProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatAtLeastProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatAtLeastProg(SketchGrammarParser.RepeatAtLeastProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RepeatRangeProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatRangeProg(SketchGrammarParser.RepeatRangeProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ConcatProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatProg(SketchGrammarParser.ConcatProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotProg(SketchGrammarParser.NotProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotCCProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotCCProg(SketchGrammarParser.NotCCProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AndProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndProg(SketchGrammarParser.AndProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OrProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrProg(SketchGrammarParser.OrProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SepProg}
	 * labeled alternative in {@link SketchGrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSepProg(SketchGrammarParser.SepProgContext ctx);
}