package regex;

import edu.stanford.nlp.sempre.NameValue;

public class RegexFunctions {

  public static NameValue contain(NameValue arg0) {

    String id = "contain(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue and(NameValue arg0, NameValue arg1) {

    String id = "and(" + arg0.id + "," + arg1.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue startwith(NameValue arg0) {

    String id = "startwith(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue endwith(NameValue arg0) {

    String id = "endwith(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue repeat(NameValue arg0, Integer arg1) {

    String id = "repeat(" + arg0.id + "," + arg1 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };
  
  public static NameValue repeat(Integer arg0, Integer arg1, NameValue arg2) {

    String id = "repeat(" + arg2.id + "," + arg0 + "," + arg1 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue followedby(NameValue arg0, NameValue arg1) {
    String id = "followedby(" + arg0.id + "," + arg1.id + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue not(NameValue arg0) {
    String id = "not(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue or(NameValue arg0, NameValue arg1) {
    String id = "or(" + arg0.id + "," + arg1.id + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };
  
  public static NameValue or(Integer arg0, Integer arg1) {
    String id = "or(" + arg0 + "," + arg1 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };
  
  public static NameValue optional(NameValue arg0) {
    String id = "optional(" + arg0.id + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };
  
  public static NameValue more(Integer arg0, NameValue arg1) {
    String id = "repeat(" + arg1.id + "," + arg0 + ",)";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };
  
  public static NameValue only(NameValue arg0) {
    return more(1, arg0);
  };
  
  public static NameValue only(Integer arg0, NameValue arg1) {
    return repeat(arg1, arg0);
  };
  
  public static NameValue upto(Integer arg0, NameValue arg1) {
    String id = "repeat(" + arg1.id + ",1," + arg0 + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };
  
  public static NameValue sep(NameValue arg0) {
    String id = "sep(" + arg0.id + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };

}
