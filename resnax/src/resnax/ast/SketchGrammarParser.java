// Generated from SketchGrammar.g4 by ANTLR 4.7.1
package resnax.ast;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SketchGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, CC=24, CONST=25, 
		INT=26;
	public static final int
		RULE_sketch = 0, RULE_lsketch = 1, RULE_var = 2, RULE_program = 3;
	public static final String[] ruleNames = {
		"sketch", "lsketch", "var", "program"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "'sketch('", "','", "')'", "'startwith('", "'endwith('", 
		"'contain('", "'optional('", "'star('", "'repeat('", "'repeatatleast('", 
		"'repeatrange('", "'concat('", "'not('", "'notcc('", "'and('", "'or('", 
		"'sep('", "'?'", "'null'", "'eps'", "',)'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"CC", "CONST", "INT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SketchGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SketchGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SketchContext extends ParserRuleContext {
		public SketchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sketch; }
	 
		public SketchContext() { }
		public void copyFrom(SketchContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AndSketchContext extends SketchContext {
		public List<SketchContext> sketch() {
			return getRuleContexts(SketchContext.class);
		}
		public SketchContext sketch(int i) {
			return getRuleContext(SketchContext.class,i);
		}
		public AndSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitAndSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatAtLeastSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public TerminalNode INT() { return getToken(SketchGrammarParser.INT, 0); }
		public RepeatAtLeastSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatAtLeastSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConcatSketchContext extends SketchContext {
		public List<SketchContext> sketch() {
			return getRuleContexts(SketchContext.class);
		}
		public SketchContext sketch(int i) {
			return getRuleContext(SketchContext.class,i);
		}
		public ConcatSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitConcatSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FreeVarSketchContext extends SketchContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public FreeVarSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitFreeVarSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContainSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public ContainSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitContainSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotCCSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public NotCCSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitNotCCSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StartwithSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public StartwithSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitStartwithSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OptionalSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public OptionalSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitOptionalSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatRangeSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public List<TerminalNode> INT() { return getTokens(SketchGrammarParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SketchGrammarParser.INT, i);
		}
		public RepeatRangeSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatRangeSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GuardedVarSketchContext extends SketchContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public LsketchContext lsketch() {
			return getRuleContext(LsketchContext.class,0);
		}
		public GuardedVarSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitGuardedVarSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrSketchContext extends SketchContext {
		public List<SketchContext> sketch() {
			return getRuleContexts(SketchContext.class);
		}
		public SketchContext sketch(int i) {
			return getRuleContext(SketchContext.class,i);
		}
		public OrSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitOrSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public TerminalNode INT() { return getToken(SketchGrammarParser.INT, 0); }
		public RepSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public TerminalNode INT() { return getToken(SketchGrammarParser.INT, 0); }
		public RepeatSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EndwithSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public EndwithSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitEndwithSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public NotSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitNotSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProgContext extends SketchContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public ProgContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SepSketchContext extends SketchContext {
		public List<SketchContext> sketch() {
			return getRuleContexts(SketchContext.class);
		}
		public SketchContext sketch(int i) {
			return getRuleContext(SketchContext.class,i);
		}
		public SepSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitSepSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StarSketchContext extends SketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public StarSketchContext(SketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitStarSketch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SketchContext sketch() throws RecognitionException {
		SketchContext _localctx = new SketchContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sketch);
		try {
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				_localctx = new ProgContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(8);
				program();
				}
				break;
			case 2:
				_localctx = new FreeVarSketchContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(9);
				var();
				}
				break;
			case 3:
				_localctx = new GuardedVarSketchContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(10);
				var();
				setState(11);
				match(T__0);
				setState(12);
				lsketch();
				setState(13);
				match(T__1);
				}
				break;
			case 4:
				_localctx = new RepSketchContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(15);
				match(T__2);
				setState(16);
				sketch();
				setState(17);
				match(T__3);
				setState(18);
				match(INT);
				setState(19);
				match(T__4);
				}
				break;
			case 5:
				_localctx = new StartwithSketchContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(21);
				match(T__5);
				setState(22);
				sketch();
				setState(23);
				match(T__4);
				}
				break;
			case 6:
				_localctx = new EndwithSketchContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(25);
				match(T__6);
				setState(26);
				sketch();
				setState(27);
				match(T__4);
				}
				break;
			case 7:
				_localctx = new ContainSketchContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(29);
				match(T__7);
				setState(30);
				sketch();
				setState(31);
				match(T__4);
				}
				break;
			case 8:
				_localctx = new OptionalSketchContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(33);
				match(T__8);
				setState(34);
				sketch();
				setState(35);
				match(T__4);
				}
				break;
			case 9:
				_localctx = new StarSketchContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(37);
				match(T__9);
				setState(38);
				sketch();
				setState(39);
				match(T__4);
				}
				break;
			case 10:
				_localctx = new RepeatSketchContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(41);
				match(T__10);
				setState(42);
				sketch();
				setState(43);
				match(T__3);
				setState(44);
				match(INT);
				setState(45);
				match(T__4);
				}
				break;
			case 11:
				_localctx = new RepeatAtLeastSketchContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(47);
				match(T__11);
				setState(48);
				sketch();
				setState(49);
				match(T__3);
				setState(50);
				match(INT);
				setState(51);
				match(T__4);
				}
				break;
			case 12:
				_localctx = new RepeatRangeSketchContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(53);
				match(T__12);
				setState(54);
				sketch();
				setState(55);
				match(T__3);
				setState(56);
				match(INT);
				setState(57);
				match(T__3);
				setState(58);
				match(INT);
				setState(59);
				match(T__4);
				}
				break;
			case 13:
				_localctx = new ConcatSketchContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(61);
				match(T__13);
				setState(62);
				sketch();
				setState(63);
				match(T__3);
				setState(64);
				sketch();
				setState(65);
				match(T__4);
				}
				break;
			case 14:
				_localctx = new NotSketchContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(67);
				match(T__14);
				setState(68);
				sketch();
				setState(69);
				match(T__4);
				}
				break;
			case 15:
				_localctx = new NotCCSketchContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(71);
				match(T__15);
				setState(72);
				sketch();
				setState(73);
				match(T__4);
				}
				break;
			case 16:
				_localctx = new AndSketchContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(75);
				match(T__16);
				setState(76);
				sketch();
				setState(77);
				match(T__3);
				setState(78);
				sketch();
				setState(79);
				match(T__4);
				}
				break;
			case 17:
				_localctx = new OrSketchContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(81);
				match(T__17);
				setState(82);
				sketch();
				setState(83);
				match(T__3);
				setState(84);
				sketch();
				setState(85);
				match(T__4);
				}
				break;
			case 18:
				_localctx = new SepSketchContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(87);
				match(T__18);
				setState(88);
				sketch();
				setState(89);
				match(T__3);
				setState(90);
				sketch();
				setState(91);
				match(T__4);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LsketchContext extends ParserRuleContext {
		public LsketchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lsketch; }
	 
		public LsketchContext() { }
		public void copyFrom(LsketchContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MultiSketchContext extends LsketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public LsketchContext lsketch() {
			return getRuleContext(LsketchContext.class,0);
		}
		public MultiSketchContext(LsketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitMultiSketch(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleSketchContext extends LsketchContext {
		public SketchContext sketch() {
			return getRuleContext(SketchContext.class,0);
		}
		public SingleSketchContext(LsketchContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitSingleSketch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LsketchContext lsketch() throws RecognitionException {
		LsketchContext _localctx = new LsketchContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_lsketch);
		try {
			setState(100);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new SingleSketchContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				sketch();
				}
				break;
			case 2:
				_localctx = new MultiSketchContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				sketch();
				setState(97);
				match(T__3);
				setState(98);
				lsketch();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarContext extends ParserRuleContext {
		public VarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarContext var() throws RecognitionException {
		VarContext _localctx = new VarContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(T__19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProgramContext extends ParserRuleContext {
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	 
		public ProgramContext() { }
		public void copyFrom(ProgramContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AndProgContext extends ProgramContext {
		public List<ProgramContext> program() {
			return getRuleContexts(ProgramContext.class);
		}
		public ProgramContext program(int i) {
			return getRuleContext(ProgramContext.class,i);
		}
		public AndProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitAndProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EndwithProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public EndwithProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitEndwithProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StartwithProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public StartwithProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitStartwithProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrProgContext extends ProgramContext {
		public List<ProgramContext> program() {
			return getRuleContexts(ProgramContext.class);
		}
		public ProgramContext program(int i) {
			return getRuleContext(ProgramContext.class,i);
		}
		public OrProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitOrProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstantProgContext extends ProgramContext {
		public TerminalNode CONST() { return getToken(SketchGrammarParser.CONST, 0); }
		public ConstantProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitConstantProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EmptyProgContext extends ProgramContext {
		public EmptyProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitEmptyProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StarProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public StarProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitStarProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContainProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public ContainProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitContainProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatAtLeastProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public TerminalNode INT() { return getToken(SketchGrammarParser.INT, 0); }
		public RepeatAtLeastProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatAtLeastProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CharClassProgContext extends ProgramContext {
		public TerminalNode CC() { return getToken(SketchGrammarParser.CC, 0); }
		public CharClassProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitCharClassProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConcatProgContext extends ProgramContext {
		public List<ProgramContext> program() {
			return getRuleContexts(ProgramContext.class);
		}
		public ProgramContext program(int i) {
			return getRuleContext(ProgramContext.class,i);
		}
		public ConcatProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitConcatProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public NotProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitNotProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotCCProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public NotCCProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitNotCCProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SepProgContext extends ProgramContext {
		public List<ProgramContext> program() {
			return getRuleContexts(ProgramContext.class);
		}
		public ProgramContext program(int i) {
			return getRuleContext(ProgramContext.class,i);
		}
		public SepProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitSepProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public TerminalNode INT() { return getToken(SketchGrammarParser.INT, 0); }
		public RepeatProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatRangeProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public List<TerminalNode> INT() { return getTokens(SketchGrammarParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SketchGrammarParser.INT, i);
		}
		public RepeatRangeProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitRepeatRangeProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullProgContext extends ProgramContext {
		public NullProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitNullProg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OptionalProgContext extends ProgramContext {
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public OptionalProgContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SketchGrammarVisitor ) return ((SketchGrammarVisitor<? extends T>)visitor).visitOptionalProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_program);
		try {
			setState(180);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new CharClassProgContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(104);
				match(CC);
				}
				break;
			case 2:
				_localctx = new ConstantProgContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				match(CONST);
				}
				break;
			case 3:
				_localctx = new NullProgContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(106);
				match(T__20);
				}
				break;
			case 4:
				_localctx = new EmptyProgContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(107);
				match(T__21);
				}
				break;
			case 5:
				_localctx = new StartwithProgContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(108);
				match(T__5);
				setState(109);
				program();
				setState(110);
				match(T__4);
				}
				break;
			case 6:
				_localctx = new EndwithProgContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(112);
				match(T__6);
				setState(113);
				program();
				setState(114);
				match(T__4);
				}
				break;
			case 7:
				_localctx = new ContainProgContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(116);
				match(T__7);
				setState(117);
				program();
				setState(118);
				match(T__4);
				}
				break;
			case 8:
				_localctx = new OptionalProgContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(120);
				match(T__8);
				setState(121);
				program();
				setState(122);
				match(T__4);
				}
				break;
			case 9:
				_localctx = new StarProgContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(124);
				match(T__9);
				setState(125);
				program();
				setState(126);
				match(T__4);
				}
				break;
			case 10:
				_localctx = new RepeatProgContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(128);
				match(T__10);
				setState(129);
				program();
				setState(130);
				match(T__3);
				setState(131);
				match(INT);
				setState(132);
				match(T__4);
				}
				break;
			case 11:
				_localctx = new RepeatAtLeastProgContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(134);
				match(T__10);
				setState(135);
				program();
				setState(136);
				match(T__3);
				setState(137);
				match(INT);
				setState(138);
				match(T__22);
				}
				break;
			case 12:
				_localctx = new RepeatRangeProgContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(140);
				match(T__10);
				setState(141);
				program();
				setState(142);
				match(T__3);
				setState(143);
				match(INT);
				setState(144);
				match(T__3);
				setState(145);
				match(INT);
				setState(146);
				match(T__4);
				}
				break;
			case 13:
				_localctx = new ConcatProgContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(148);
				match(T__13);
				setState(149);
				program();
				setState(150);
				match(T__3);
				setState(151);
				program();
				setState(152);
				match(T__4);
				}
				break;
			case 14:
				_localctx = new NotProgContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(154);
				match(T__14);
				setState(155);
				program();
				setState(156);
				match(T__4);
				}
				break;
			case 15:
				_localctx = new NotCCProgContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(158);
				match(T__15);
				setState(159);
				program();
				setState(160);
				match(T__4);
				}
				break;
			case 16:
				_localctx = new AndProgContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(162);
				match(T__16);
				setState(163);
				program();
				setState(164);
				match(T__3);
				setState(165);
				program();
				setState(166);
				match(T__4);
				}
				break;
			case 17:
				_localctx = new OrProgContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(168);
				match(T__17);
				setState(169);
				program();
				setState(170);
				match(T__3);
				setState(171);
				program();
				setState(172);
				match(T__4);
				}
				break;
			case 18:
				_localctx = new SepProgContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(174);
				match(T__18);
				setState(175);
				program();
				setState(176);
				match(T__3);
				setState(177);
				program();
				setState(178);
				match(T__4);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34\u00b9\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\5\2`\n\2\3\3\3\3\3\3\3\3\3\3\5\3g\n\3\3\4\3\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\5\5\u00b7\n\5\3\5\2\2\6\2\4\6\b\2\2\2\u00d7\2_\3\2\2\2"+
		"\4f\3\2\2\2\6h\3\2\2\2\b\u00b6\3\2\2\2\n`\5\b\5\2\13`\5\6\4\2\f\r\5\6"+
		"\4\2\r\16\7\3\2\2\16\17\5\4\3\2\17\20\7\4\2\2\20`\3\2\2\2\21\22\7\5\2"+
		"\2\22\23\5\2\2\2\23\24\7\6\2\2\24\25\7\34\2\2\25\26\7\7\2\2\26`\3\2\2"+
		"\2\27\30\7\b\2\2\30\31\5\2\2\2\31\32\7\7\2\2\32`\3\2\2\2\33\34\7\t\2\2"+
		"\34\35\5\2\2\2\35\36\7\7\2\2\36`\3\2\2\2\37 \7\n\2\2 !\5\2\2\2!\"\7\7"+
		"\2\2\"`\3\2\2\2#$\7\13\2\2$%\5\2\2\2%&\7\7\2\2&`\3\2\2\2\'(\7\f\2\2()"+
		"\5\2\2\2)*\7\7\2\2*`\3\2\2\2+,\7\r\2\2,-\5\2\2\2-.\7\6\2\2./\7\34\2\2"+
		"/\60\7\7\2\2\60`\3\2\2\2\61\62\7\16\2\2\62\63\5\2\2\2\63\64\7\6\2\2\64"+
		"\65\7\34\2\2\65\66\7\7\2\2\66`\3\2\2\2\678\7\17\2\289\5\2\2\29:\7\6\2"+
		"\2:;\7\34\2\2;<\7\6\2\2<=\7\34\2\2=>\7\7\2\2>`\3\2\2\2?@\7\20\2\2@A\5"+
		"\2\2\2AB\7\6\2\2BC\5\2\2\2CD\7\7\2\2D`\3\2\2\2EF\7\21\2\2FG\5\2\2\2GH"+
		"\7\7\2\2H`\3\2\2\2IJ\7\22\2\2JK\5\2\2\2KL\7\7\2\2L`\3\2\2\2MN\7\23\2\2"+
		"NO\5\2\2\2OP\7\6\2\2PQ\5\2\2\2QR\7\7\2\2R`\3\2\2\2ST\7\24\2\2TU\5\2\2"+
		"\2UV\7\6\2\2VW\5\2\2\2WX\7\7\2\2X`\3\2\2\2YZ\7\25\2\2Z[\5\2\2\2[\\\7\6"+
		"\2\2\\]\5\2\2\2]^\7\7\2\2^`\3\2\2\2_\n\3\2\2\2_\13\3\2\2\2_\f\3\2\2\2"+
		"_\21\3\2\2\2_\27\3\2\2\2_\33\3\2\2\2_\37\3\2\2\2_#\3\2\2\2_\'\3\2\2\2"+
		"_+\3\2\2\2_\61\3\2\2\2_\67\3\2\2\2_?\3\2\2\2_E\3\2\2\2_I\3\2\2\2_M\3\2"+
		"\2\2_S\3\2\2\2_Y\3\2\2\2`\3\3\2\2\2ag\5\2\2\2bc\5\2\2\2cd\7\6\2\2de\5"+
		"\4\3\2eg\3\2\2\2fa\3\2\2\2fb\3\2\2\2g\5\3\2\2\2hi\7\26\2\2i\7\3\2\2\2"+
		"j\u00b7\7\32\2\2k\u00b7\7\33\2\2l\u00b7\7\27\2\2m\u00b7\7\30\2\2no\7\b"+
		"\2\2op\5\b\5\2pq\7\7\2\2q\u00b7\3\2\2\2rs\7\t\2\2st\5\b\5\2tu\7\7\2\2"+
		"u\u00b7\3\2\2\2vw\7\n\2\2wx\5\b\5\2xy\7\7\2\2y\u00b7\3\2\2\2z{\7\13\2"+
		"\2{|\5\b\5\2|}\7\7\2\2}\u00b7\3\2\2\2~\177\7\f\2\2\177\u0080\5\b\5\2\u0080"+
		"\u0081\7\7\2\2\u0081\u00b7\3\2\2\2\u0082\u0083\7\r\2\2\u0083\u0084\5\b"+
		"\5\2\u0084\u0085\7\6\2\2\u0085\u0086\7\34\2\2\u0086\u0087\7\7\2\2\u0087"+
		"\u00b7\3\2\2\2\u0088\u0089\7\r\2\2\u0089\u008a\5\b\5\2\u008a\u008b\7\6"+
		"\2\2\u008b\u008c\7\34\2\2\u008c\u008d\7\31\2\2\u008d\u00b7\3\2\2\2\u008e"+
		"\u008f\7\r\2\2\u008f\u0090\5\b\5\2\u0090\u0091\7\6\2\2\u0091\u0092\7\34"+
		"\2\2\u0092\u0093\7\6\2\2\u0093\u0094\7\34\2\2\u0094\u0095\7\7\2\2\u0095"+
		"\u00b7\3\2\2\2\u0096\u0097\7\20\2\2\u0097\u0098\5\b\5\2\u0098\u0099\7"+
		"\6\2\2\u0099\u009a\5\b\5\2\u009a\u009b\7\7\2\2\u009b\u00b7\3\2\2\2\u009c"+
		"\u009d\7\21\2\2\u009d\u009e\5\b\5\2\u009e\u009f\7\7\2\2\u009f\u00b7\3"+
		"\2\2\2\u00a0\u00a1\7\22\2\2\u00a1\u00a2\5\b\5\2\u00a2\u00a3\7\7\2\2\u00a3"+
		"\u00b7\3\2\2\2\u00a4\u00a5\7\23\2\2\u00a5\u00a6\5\b\5\2\u00a6\u00a7\7"+
		"\6\2\2\u00a7\u00a8\5\b\5\2\u00a8\u00a9\7\7\2\2\u00a9\u00b7\3\2\2\2\u00aa"+
		"\u00ab\7\24\2\2\u00ab\u00ac\5\b\5\2\u00ac\u00ad\7\6\2\2\u00ad\u00ae\5"+
		"\b\5\2\u00ae\u00af\7\7\2\2\u00af\u00b7\3\2\2\2\u00b0\u00b1\7\25\2\2\u00b1"+
		"\u00b2\5\b\5\2\u00b2\u00b3\7\6\2\2\u00b3\u00b4\5\b\5\2\u00b4\u00b5\7\7"+
		"\2\2\u00b5\u00b7\3\2\2\2\u00b6j\3\2\2\2\u00b6k\3\2\2\2\u00b6l\3\2\2\2"+
		"\u00b6m\3\2\2\2\u00b6n\3\2\2\2\u00b6r\3\2\2\2\u00b6v\3\2\2\2\u00b6z\3"+
		"\2\2\2\u00b6~\3\2\2\2\u00b6\u0082\3\2\2\2\u00b6\u0088\3\2\2\2\u00b6\u008e"+
		"\3\2\2\2\u00b6\u0096\3\2\2\2\u00b6\u009c\3\2\2\2\u00b6\u00a0\3\2\2\2\u00b6"+
		"\u00a4\3\2\2\2\u00b6\u00aa\3\2\2\2\u00b6\u00b0\3\2\2\2\u00b7\t\3\2\2\2"+
		"\5_f\u00b6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}