package regex;

import edu.stanford.nlp.sempre.ListValue;
import edu.stanford.nlp.sempre.NameValue;
import java.io.*;
import java.util.ArrayList;

public class SketchFunctions {

  public static NameValue unarysketchop(NameValue arg0) {

    String id = "?{" + arg0.id + "}";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue constop(String arg0) {

    int pos = 0;
    int maxLength = arg0.length();
    ArrayList<String> splits = new ArrayList<>();
    while (pos < maxLength) {
      if(arg0.startsWith("upper",pos)) {
        splits.add("upper" + (arg0.charAt(pos + 5)));
        pos += 6;
      } else  {
        splits.add("" + arg0.charAt(pos));
        pos += 1;
      }
    }

    String id = "";
    if (splits.size() == 1) {
      id = "<" + splits.get(0) + ">";
    } else {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < splits.size() - 1; i++) {
        builder.append("concat(");
        builder.append("<" + splits.get(i) + ">,");
      }
      builder.append("<" + splits.get(splits.size() - 1) + ">");
      for (int i = 0; i < splits.size() - 1; i++) builder.append(")");
      id = builder.toString();
    }
    NameValue ret = new NameValue(id);
    return ret;
  };
  
  public static NameValue orop(NameValue arg0, NameValue arg1) {
    String id = "or(" + arg0.id + "," + arg1.id + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };

  public static NameValue sepop(NameValue arg0, NameValue arg1) {
    String id = "sep(" + arg0.id + "," + arg1.id + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };

  public static NameValue concatop(NameValue arg0, NameValue arg1) {
    String id = "concat(" + arg0.id + "," + arg1.id + ")";
    
    NameValue ret = new NameValue(id);
    
    return ret;
  };

  public static NameValue listjoin(NameValue arg0, NameValue arg1) {
    String id = arg0.id + "," + arg1.id;

    NameValue ret = new NameValue(id);
    
    return ret;
  };


  public static NameValue notcontainop(NameValue arg0) {

    String id = "not(contain(" + arg0.id + "))";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue containop(NameValue arg0) {

    String id = "contain(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue startwithop(NameValue arg0) {

    String id = "startwith(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue endwithop(NameValue arg0) {

    String id = "endwith(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue repeatop(NameValue arg0, Integer arg1) {

    String id = "repeat(" + arg0.id + "," + arg1 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue repeatatleastop(NameValue arg0, Integer arg1) {

    String id = "repeatatleast(" + arg0.id + "," + arg1 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue repeatrangeop(NameValue arg0, Integer arg1, Integer arg2) {

    String id = "repeatrange(" + arg0.id + "," + arg1 + "," + arg2 + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue repeatintorintop(NameValue arg0, Integer arg1, Integer arg2) {

    String id = "or(" + repeatop(arg0,arg1).id +"," + repeatop(arg0,arg2).id + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

  public static NameValue notop(NameValue arg0) {

    String id = "not(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };


  public static NameValue notccop(NameValue arg0) {

    String id = "notcc(" + arg0.id + ")";

    NameValue ret = new NameValue(id);

    return ret;

  };

  public static NameValue decimalop(NameValue arg0, NameValue arg1) {
    String id = "sep(" + unarysketchop(listjoin(arg0, arg1)).id + "," + "<.>" + ")";

    NameValue ret = new NameValue(id);

    return ret;
  };

}
