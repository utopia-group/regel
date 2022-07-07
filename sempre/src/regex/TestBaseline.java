package regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.stanford.nlp.sempre.*;
import fig.basic.LogInfo;
import fig.basic.Pair;
import fig.exec.Execution;

public class TestBaseline implements Runnable {

  public String dataset = null;
  public int beam = -1;

  Integer countCoverage(Derivation deriv) {
    // base
//    System.out.println(deriv.cat + " " + deriv.children.size() + " " + Formulas.getString(deriv.formula) + " " + deriv.end + " " + deriv.start);

    if (deriv.cat.equals("$Skip")) {
//      System.out.println("ret " + 0);
      return 0;
    }


    if (deriv.children.size() == 0) {
//      System.out.println("ret " + (deriv.end - deriv.start));
        return (deriv.end - deriv.start) + 1000;
//        return 1;
    }


    List<Derivation> children = deriv.children;
    Integer x = new Integer(1000);
    for (int i = 0; i < children.size(); ++i) {
      x = x + countCoverage(children.get(i));
    }
//    System.out.println("ret " + x);
    return x;
  }

  List<Derivation> coverageBasedSort(List<Derivation> derivs) {
    List<Pair<Integer, Integer>> counts = new ArrayList<>();

    for (int i = 0; i < derivs.size(); i++) {
      Integer numCovered = countCoverage(derivs.get(i));
      counts.add(new Pair<>(numCovered, Integer.valueOf(i)));
    }
    counts.sort(new Comparator<Pair<Integer, Integer>>() {
      @Override
      public int compare(final Pair<Integer, Integer> o1, final Pair<Integer, Integer> o2) {
        return o2.getFirst().intValue() - o1.getFirst().intValue();
      }
    });
    List<Derivation> orderedDerivs = new ArrayList<>();
    for (int i = 0; i < counts.size(); i++) {
      Pair<Integer, Integer> pair = counts.get(i);
      System.out.println(i + " " + pair.getFirst() + " " + Formulas.getString(derivs.get(pair.getSecond()).formula));
      orderedDerivs.add(derivs.get(pair.getSecond()));
    }
    return  orderedDerivs;
  }

  public void runPrediction(
      //
      Master master, Session session,
      //
      String srcFilePath,
      //
      String specFilePath,
      //
      String outputFilePath) {


    try (PrintWriter outputFile = new PrintWriter(outputFilePath)) {
      try (BufferedReader srcFile = new BufferedReader(new FileReader(new File(srcFilePath)))) {

        for (String lineStr = srcFile.readLine(); lineStr != null;) {

          String[] fields = lineStr.split("\t", 2);
          String id = fields[0];
          String utterance = fields[1];


          int indent = LogInfo.getIndLevel();
          int order = 0;
          try {

            Map<String, Integer> derivToCount = new HashMap<>();
            Map<String, Integer> derivToOrder = new HashMap<>();
            Map<Integer, String> orderToDeriv = new HashMap<>();
            Map<Integer, Double> orderToScore = new HashMap<>();

            String topPred = "";
            {

              Master.Response response = master.processQuery(session, utterance);

              List<Derivation> derivs = response.ex.getPredDerivations();
              derivs = coverageBasedSort(derivs);
              for (int i = 0; i < derivs.size(); i++) {

                String derivString = ((derivs.get(i)).value).toString();
                String subDeriv = "";
                if (derivString.contains("\"")) {
                  subDeriv = derivString.split("\"")[1];
                } else {
                  subDeriv = derivString.split(" ")[1];
                  subDeriv = subDeriv.substring(0, subDeriv.indexOf(")"));
                }

                // update derivation-to-count map
                if (derivToCount.containsKey(subDeriv)) {
                  derivToCount.put(subDeriv, derivToCount.get(subDeriv) + 1);
                } else {
                  derivToCount.put(subDeriv, 1);
                }

                if (!derivToOrder.containsKey(subDeriv)) {
                  derivToOrder.put(subDeriv, order);
                }
                orderToScore.put(order, derivs.get(i).getScore());
                orderToDeriv.put(order, subDeriv);
                // update top prediction
                if (order == 0) {
                  topPred = subDeriv;
                }
                order += 1;
              }
            }

            if ((derivToCount.isEmpty()) || (topPred.equals(""))) {

              lineStr = srcFile.readLine();

              if (lineStr == null || lineStr.isEmpty())
                break;

              continue;
            }

            {
              outputFile.println(id + "\t" + orderToDeriv.size());
              for (int i = 0; i < orderToDeriv.size(); i++) {
                String v = orderToDeriv.get(i);
                outputFile.println(v + "\t" + i + "\t" + orderToScore.get(i));
              }
            }

            lineStr = srcFile.readLine();

            if (lineStr == null || lineStr.isEmpty())
              break;

          } catch (Throwable t) {
            System.out.println("Excpetion at the outer try");
            while (LogInfo.getIndLevel() > indent)
              LogInfo.end_track();
            t.printStackTrace();

          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {

    // parse our arguments
    {
      this.beam = Parser.opts.beamSize;
    }

    // run prediction
    {
      Builder builder = new Builder();
      builder.build();

      Master master = new Master(builder);

      Session session = master.getSession("stdin");

      // // test on train set
      // {
      //   String srcFilePath = "regex/data/" + this.dataset + "/src-train.txt";
      //   String specFilePath = "regex/data/" + this.dataset + "/spec-train.txt";
      //   String outputFilePath = "regex/data/" + this.dataset + "/" + this.beam + "-pred-train.txt";
      //   runPrediction(master, session, srcFilePath, specFilePath, outputFilePath);
      // }

      // test on test set
      {
        String srcFilePath = "regex/data/" + this.dataset + "/src-test.txt";
        String specFilePath = "regex/data/" + this.dataset + "/spec-test.txt";
        String outputFilePath = "regex/data/" + this.dataset + "/" + this.beam + "-pred-test.txt";
        runPrediction(master, session, srcFilePath, specFilePath, outputFilePath);
      }

    }

  }

  public static void main(String[] args) {

    TestBaseline t = new TestBaseline();

    // parse our own arguments
    {
      String dataset = args[args.length - 1];
      t.dataset = dataset;
    }

    // parse sempre arguments and run
    {
      String[] args1 = new String[args.length - 1];
      System.arraycopy(args, 0, args1, 0, args.length - 1);
      Execution.run(args1, "TestBaseline", t, Master.getOptionsParser());
    }

  }

}
