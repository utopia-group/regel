package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;

import java.lang.reflect.Array;

public class SketchJoinFn extends SemanticFn {
    public DerivationStream call(Example ex, final Callable c) {
        return new SingleDerivationStream() {
            @Override
            public Derivation createDerivation() {
                Formula formula0 = c.child(0).formula;
                Formula formula1 = c.child(1).formula;
                String arg0 = Formulas.getString(formula0);
                String arg1 = Formulas.getString(formula1);

                NameValue formula = new NameValue(arg0 + " " + arg1);
                return new Derivation.Builder()
                        .withCallable(c)
                        .formula(new ValueFormula<>(formula))
                        .createDerivation();
            }
        };
    }
}
