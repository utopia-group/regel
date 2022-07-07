package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;
import fig.basic.LispTree;
import fig.basic.LogInfo;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;

public class RepeatatleastFn extends SemanticFn {
    // Which child derivation to select and return.
    int position0 = -1;
    String type1 = "";
    int value1 = -1;

    public RepeatatleastFn() { }

    public void init(LispTree tree) {
        super.init(tree);
        this.position0 = Integer.valueOf(tree.child(1).value);
        this.type1 = tree.child(2).child(0).value;
        this.value1 = Integer.valueOf(tree.child(2).child(1).value);
    }

    public DerivationStream call(Example ex, final Callable c) {
        return new SingleDerivationStream() {
            @Override
            public Derivation createDerivation() {
                String arg0 = Formulas.getString(c.child(0).formula);
                String arg1 = Formulas.getString(c.child(1).formula);

                String targetCat = c.getCat();

                NameValue formula;
                if (targetCat.equals("$SKETCH") || targetCat.equals("$PROGRAM")) {
                    // execute
                    String[] args = (arg0 + "#DIV#" + arg1).split("#DIV#");
                    arg0 = args[position0];
                    arg1 = type1.equals("arg") ? (value1 + "") : (Double.valueOf(args[value1]).intValue() + "");
                    if (arg1.equals("0"))
                        formula = new NameValue("star(" + arg0 + ")");
                    else
                        formula = new NameValue("repeatatleast(" + arg0 + "," + arg1 + ")");
                } else {
                    // Forwarding parameters
                    formula = new NameValue( arg0 + "#DIV#" + arg1);
                }
                return new Derivation.Builder()
                        .withCallable(c)
                        .formula(new ValueFormula<>(formula))
                        .createDerivation();
            }
        };
    }
}
