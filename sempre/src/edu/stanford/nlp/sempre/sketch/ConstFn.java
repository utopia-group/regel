package edu.stanford.nlp.sempre.sketch;

import edu.stanford.nlp.sempre.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ConstFn extends SemanticFn {
    public DerivationStream call(Example ex, final Callable c) {
        return new SingleDerivationStream() {
            @Override
            public Derivation createDerivation() {
                List<String> tokens = ex.getTokens().subList(c.getStart() + 1, c.getEnd() - 1);
                if (tokens.contains("leftquoatation")){
                    return null;
                }
                StringBuilder builder = new StringBuilder();
                for (String tok: tokens) {
                    builder.append(tok);
                }
                String arg0 = builder.toString();
                NameValue formula = new NameValue(transform(arg0));
                return new Derivation.Builder()
                        .withCallable(c)
                        .formula(new ValueFormula<>(formula))
                        .createDerivation();
            }
        };
    }


    String transform(String arg0) {
        int pos = 0;
        int maxLength = arg0.length();
        ArrayList<String> splits = new ArrayList<>();
        while (pos < maxLength) {
            if(arg0.startsWith("upper",pos)) {
                splits.add("upper" + (arg0.charAt(pos + 5)));
                pos += 6;
            } else if (arg0.startsWith("-lrb-", pos)) {
                splits.add("-lrb-");
                pos += 5;
            } else if (arg0.startsWith("-rrb-", pos)) {
                splits.add("-rrb-");
                pos += 5;
            } else if (arg0.startsWith("-lcb-", pos)) {
                splits.add("{");
                pos += 5;
            } else if (arg0.startsWith("-rcb-", pos)) {
                splits.add("}");
                pos += 5;
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
        return  id;
    }
}
