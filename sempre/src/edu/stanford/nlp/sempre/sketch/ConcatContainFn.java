package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;
import fig.basic.LispTree;

public class ConcatContainFn  extends SemanticFn{
    // Which child derivation to select and return.
    int position0 = -1;
    int position1 = -1;

    public ConcatContainFn() { }

    public ConcatContainFn(int position0, int position1) {
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

    public DerivationStream call(Example ex, final SemanticFn.Callable c) {
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
                    formula = new NameValue("concat(" + args[position0] + ",contain(" + args[position1] + "))");
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

