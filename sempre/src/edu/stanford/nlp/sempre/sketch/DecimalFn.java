package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;
import fig.basic.LispTree;
import fig.basic.LogInfo;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;

public class DecimalFn extends SemanticFn {
    // Which child derivation to select and return.
    int position0 = -1;
    int position1 = -1;

    public DecimalFn() { }

    public DecimalFn(int position0, int position1) {
        LispTree tree = LispTree.proto.newList();
        tree.addChild("SelectFn");
        tree.addChild(position0 + "");
        tree.addChild(position1 + "");
        init(tree);
    }

    public void init(LispTree tree) {
        super.init(tree);
        this.position0 = Integer.valueOf(tree.child(1).value);
        this.position1 = Integer.valueOf(tree.child(2).value);
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
                    String[] args = (arg0 + " " + arg1).split(" ");
                    if (args[position0].equals(args[position1])) {
                        formula = new NameValue("sep(?{" + args[position0]+ "},<.>)");
                    } else {
                        formula = new NameValue("sep(?{" + args[position0] + "," + args[position1] + "},<.>)");
                    }
                } else {
                    // Forwarding parameters
                    formula = new NameValue( arg0 + " " + arg1);
                }
                return new Derivation.Builder()
                        .withCallable(c)
                        .formula(new ValueFormula<>(formula))
                        .createDerivation();
            }
        };
    }
}
