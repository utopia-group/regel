package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;
import fig.basic.LispTree;

public class NotFn extends SemanticFn {
    // Which child derivation to select and return.
    int position0 = -1;

    public NotFn() { }

    public NotFn(int position0) {
        LispTree tree = LispTree.proto.newList();
        tree.addChild("SelectFn");
        tree.addChild(position0 + "");
        init(tree);
    }

    public void init(LispTree tree) {
        super.init(tree);
        this.position0 = Integer.valueOf(tree.child(1).value);
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
                    formula = new NameValue("not(" + args[position0] + ")");
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