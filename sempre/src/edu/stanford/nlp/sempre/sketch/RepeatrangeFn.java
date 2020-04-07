package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;
import fig.basic.LispTree;
import fig.basic.LogInfo;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;

public class RepeatrangeFn extends SemanticFn {
    // Which child derivation to select and return.
    int position0 = -1;
    String type1 = "";
    int value1 = -1;
    String type2 = "";
    int value2 = -1;

    public RepeatrangeFn() { }

    public void init(LispTree tree) {
        super.init(tree);
        this.position0 = Integer.valueOf(tree.child(1).value);
        this.type1 = tree.child(2).child(0).value;
        this.value1 = Integer.valueOf(tree.child(2).child(1).value);
        this.type2 = tree.child(3).child(0).value;
        this.value2 = Integer.valueOf(tree.child(3).child(1).value);
    }

    public DerivationStream call(Example ex, final Callable c) {
        return new SingleDerivationStream() {
            @Override
            public Derivation createDerivation() {
                String formula0 = Formulas.getString(c.child(0).formula);
                String formula1 = Formulas.getString(c.child(1).formula);

                String targetCat = c.getCat();

                NameValue formula;
                if (targetCat.equals("$SKETCH") || targetCat.equals("$PROGRAM")) {
                    // execute
                    String[] args = (formula0 + "#DIV#" + formula1).split("#DIV#");
                    String arg0 = args[position0];
                    String arg1 = determineArg(type1, value1, args);
                    String arg2 = determineArg(type2, value2, args);
                    if (Integer.parseInt(arg1) >= Integer.parseInt(arg2))
                        return null;
                    formula = new NameValue("repeatrange(" + arg0 + "," + arg1 + "," + arg2 + ")");
                } else {
                    // Forwarding parameters
                    formula = new NameValue( formula0 + "#DIV#" + formula1);
                }
                return new Derivation.Builder()
                        .withCallable(c)
                        .formula(new ValueFormula<>(formula))
                        .createDerivation();
            }
        };
    }

    String determineArg(String type, int value, String[] args) {
        return type.equals("arg") ? (value + "") : (Double.valueOf(args[value]).intValue() + "");
    }
}
