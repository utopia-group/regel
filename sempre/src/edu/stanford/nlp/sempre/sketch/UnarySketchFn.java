package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;

/**
 * Identity function.
 *
 * @author Percy Liang
 */
public class UnarySketchFn extends SemanticFn {
  public DerivationStream call(Example ex, final Callable c) {
    return new SingleDerivationStream() {
      @Override
      public Derivation createDerivation() {
        Formula formula0 = c.child(0).formula;
        String arg0 = Formulas.getString(formula0);
        String[] programs = arg0.split(" ");
        for (int i =0; i < programs.length; i++) {
          for (int j = i + 1; j < programs.length; j++) {
            if (programs[i].equals(programs[j])) {
              return null;
            }
          }
        }
        StringBuilder out = new StringBuilder();
        out.append("?{");
        for (int i = 0; i < programs.length; i++) {
          if (i > 0) out.append(",");
          out.append(programs[i]);
        }
        out.append("}");

        NameValue formula = new NameValue(out.toString());

        return new Derivation.Builder()
                .withCallable(c)
                .formula(new ValueFormula<>(formula))
                .createDerivation();
      }
    };
  }
}
