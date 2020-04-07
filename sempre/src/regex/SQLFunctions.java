package regex;

import edu.stanford.nlp.sempre.NameValue;

/**
 * Returns annotations for each type.
 */
public class SQLFunctions {

  // returns a placeholder + hint for a column
  public static String COLUMN(String str) {
    String res = "C[" + str + "]";
    return res;
  };

  // returns an aggregate function applied on a column placeholder
  public static String AggregateFn(NameValue agrFunc, String str) {
    String res = "G(" + agrFunc.id + "," + str + ")";
    return res;
  }

  // returns a placeholder + hint for a table
  public static String TABLE(String str) {
    String res = "T[" + str + "]";
    return res;
  };

  /*
   * public static String VALUE (String str) { String res = "VALUE(" + str + ")";
   * return res; };
   */

  public static String CombineColumnsFn(String colStr, String restStr) {
    String res = colStr + "," + restStr;
    return res;
  };

  public static String JoinTablesFn(String tabStr, String restStr) {
    String res = tabStr + "," + restStr;
    return res;
  };

  public static String generateSingleJoinConditionFn(String col1Str, NameValue opStr, String col2Str) {
    String res = "FJ[" + col1Str + ",O(" + opStr.id + ")," + col2Str + "]";
    return res;
  };

  public static String generateSinglePredicateFn(String colStr, NameValue opStr, String valStr) {
    String res = "FS[" + colStr + ",O(" + opStr.id + ")," + valStr + "]";
    return res;
  };

  public static String combinePredicatesFn(String predStr, NameValue logicOpStr, String restStr) {
    String res = predStr + ",O(" + logicOpStr.id + ")," + restStr;
    return res;
  };

  public static String SelectFunc(String predStr) {
    String res = "L{" + predStr + "}";
    return res;
  };

  /*
   * public static String SelectFunc (String predStr, String groupingStr) { String
   * res = "selectFuncWithGrouping(" + predStr + ", " + groupingStr + ")"; return
   * res; };
   */

  public static String JoinWithoutPredicateFn(String tabStr) {
    String res = "J(L{" + tabStr + "})";
    return res;
  };

  public static String JoinFunc(String tabStr, String selStr) {
    String res = "S(" + selStr + ",J(L{" + tabStr + "}))";
    return res;
  };

  public static String ProjectFunc(String colStr, String joinStr) {
    String res = "P(L{" + colStr + "}," + joinStr + ")";
    return res;
  };

  /*
   * public static String operandKeyWord (String opStr) { String operand =
   * "eq_op"; if (opStr.equals("=") || opStr.equals("is") || opStr.equals("equal")
   * || opStr.equals("for")) operand = "eq_op"; else if (opStr.equals(">") ||
   * opStr.equals("greater than")) operand = "gt_op"; else if (opStr.equals("<")
   * || opStr.equals("less than")) operand = "lt_op"; String res = "Operand(" +
   * operand + ")"; return res; };
   * 
   * public static String logicOperandKeyWord (String opStr) { String operand =
   * "and_op"; if(opStr.equals("&") || opStr.equals("&&") || opStr.equals("^") ||
   * opStr.equals("and")) operand = "and_op"; else if(opStr.equals("|") ||
   * opStr.equals("||") || opStr.equals("or")) operand = "or_op"; else
   * if(opStr.equals("~") || opStr.equals("!") || opStr.equals("not")) operand =
   * "not_op"; String res = "LogicOperand(" + operand + ")"; return res; };
   */

}
