package resnax.so;

import resnax.BenchmarkRes;
import resnax.Learner;
import resnax.Main;
import resnax.util.MultiMap;
import resnax.util.SetMultiMap;
import resnax.synthesizer.DSL;
import resnax.synthesizer.Example;

import java.io.*;
import java.util.*;

@SuppressWarnings("Duplicates") public class Benchmark {

  public static MultiMap<String, Character> appliedTerminalsMap = new SetMultiMap<>();

  private Learner learner;

  public String name;
  public String sketch;
  public String gt;
  public List<String> appliedTerminalsNoCost = new ArrayList<>();
  public List<String> appliedTerminalsCost = new ArrayList<>();
  public List<Example> examples = new ArrayList<>();

  // this argument is "only" used for extended version 2
  public String benchmarkNum;
  public String benchmarkPath;

  public DSL.CFG g;

  public String logPath;
  public String csvPath;

  public static Benchmark read(String benchmark, String sketch) {
      return read(benchmark, null, sketch, null);
  }

    public static Benchmark read(String filePath, String logPath, String sketch, String index) {
    Benchmark ret = new Benchmark();

    try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {

      // create benchmark name
      {
        int splitIndex = filePath.lastIndexOf('/');
        ret.benchmarkPath = filePath.substring(0, splitIndex + 1);

        String benchmarkName = filePath.substring(splitIndex + 1);
        if (index != null) {
          ret.name = benchmarkName + "-" + index;
        } else ret.name = benchmarkName;

      }

      int k_max = 0;
      // create examples
      {

        // skip nl
        {
          String line = br.readLine();
          for (; line.isEmpty() || line.startsWith("//"); line = br.readLine())
            ;
          for (; line != null && !line.isEmpty(); line = br.readLine()) {
            // if (line.startsWith("//")) continue;
            ;
          }
        }

        // so examples
        {
          String line = br.readLine();
          for (; line.isEmpty() || line.startsWith("//"); line = br.readLine())
            ;
          for (; !line.isEmpty(); line = br.readLine()) {
            if (line.startsWith("//")) continue;
            int splitIndex = line.lastIndexOf(',');
            String str = line.substring(1, splitIndex - 1);
            String type = line.substring(splitIndex + 1);
//            System.out.println(str + ": " + type);
            k_max = Integer.max(k_max, str.length());

            if (type.equals("+")) ret.examples.add(new Example(str, true));
            else if (type.equals("-")) ret.examples.add(new Example(str, false));
            else throw new RuntimeException("example type incorrect");

            for (int i = 0; i < str.length(); i++) {
              char chari = str.charAt(i);
              if (Character.isDigit(chari)) appliedTerminalsMap.put("num", chari);
              else if (Character.isLowerCase(chari)) appliedTerminalsMap.put("low", chari);
              else if (Character.isUpperCase(chari)) appliedTerminalsMap.put("cap", chari);
              else appliedTerminalsMap.put("special", chari);
            }

          }
        }


        // ground truth
        ret.gt = null;
        {
          String line = br.readLine();
          for (; line.isEmpty() || line.startsWith("//"); line = br.readLine())
            ;
          for (; line != null && !line.isEmpty(); line = br.readLine()) {
            if (line.startsWith("//")) continue;
            ret.gt = line;
            break;
          }
        }
        if (ret.gt.equals("na")) ret.gt = null;

      }

      // applied terminals

      {
        Collection<Character> numTerminals = appliedTerminalsMap.get("num");
        Collection<Character> lowerTerminals = appliedTerminalsMap.get("low");
        Collection<Character> upperTerminals = appliedTerminalsMap.get("cap");
        Collection<Character> specialTerminals = appliedTerminalsMap.get("special");

        ret.appliedTerminalsCost.add("<any>");

        if (numTerminals != null) {
          ret.appliedTerminalsNoCost.add("<num>");
          ret.appliedTerminalsNoCost.add("<num1-9>");
        }

        if (lowerTerminals != null || upperTerminals != null) {
          ret.appliedTerminalsNoCost.add("<let>");
        }

        if (specialTerminals != null) {
          for (Character c : specialTerminals) {
            ret.appliedTerminalsNoCost.add("<" + c + ">");
          }
        }

        if (lowerTerminals != null) ret.appliedTerminalsNoCost.add("<low>");

        if (upperTerminals != null) ret.appliedTerminalsNoCost.add("<cap>");

        if (numTerminals != null) {
          if (numTerminals.size() < 5) {
            for (Character c : numTerminals) {
              ret.appliedTerminalsNoCost.add("<" + c + ">");
            }
          } else {
            for (Character c : numTerminals) {
              ret.appliedTerminalsCost.add("<" + c + ">");
            }
          }
        }

        if (lowerTerminals != null) {
          if (lowerTerminals.size() < 10) {
            for (Character c : lowerTerminals) {
              ret.appliedTerminalsNoCost.add("<" + c + ">");
            }
          } else {
            for (Character c : lowerTerminals) {
              ret.appliedTerminalsCost.add("<" + c + ">");
            }
          }
        }

        if (upperTerminals != null) {
          if (upperTerminals.size() < 10) {
            for (Character c : upperTerminals) {
              ret.appliedTerminalsNoCost.add("<" + c + ">");
            }
          } else {
            for (Character c : upperTerminals) {
              ret.appliedTerminalsCost.add("<" + c + ">");
            }
          }
        }

      }

      // main max
      {

        Main.K_MAX = k_max;
//        System.out.println("kmax:" + Main.K_MAX);

      }

      // create the log file path
      if (logPath != null) {
        {
          ret.sketch = sketch;
          ret.logPath = logPath + "/" + ret.name;
          ret.csvPath = logPath + "results.csv";
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException();
    }

    return ret;
  }

  public void output_interact(List<BenchmarkRes> bres) {
      if (Main.succ) {

        String print = "";

        for (int i = 0; i < bres.size(); i++) {

          BenchmarkRes br = bres.get(i);
          print += br.program.toOutput() + "`" + br.regex + "`" + br.time + "`" + br.matchGt;

          if ((i + 1) >= bres.size()) {
            break;
          } else {
            print += "SO";
          }
        }

        System.out.println(print);


      } else {
        System.out.println("null" + "`" + "null" + "`" + "null" + "`" + "False");
      }

  }

  public void output_interact(BenchmarkRes bres) {
    if (Main.succ) {

      System.out.println(bres.program.toOutput() + "`" + bres.regex + "`" + bres.time + "`" + bres.matchGt);
    } else {
      System.out.println("null" + "`" + "null" + "`" + "null" + "`" + "False");
    }
  }

  public void run_interactive() {
    learner = new Learner(new DSL.CFG("", appliedTerminalsNoCost, appliedTerminalsCost));

    if (Main.OUTPUT_5 == 1) {
      List<BenchmarkRes> bres = learner.learn_output_5(sketch, examples, gt);
      output_interact(bres);
    } else {
      output_interact(learner.learn(sketch, examples, gt));
    }
  }

  public void run() {
    System.out.println("Running " + name);

    logFirst();

    {
      try {
        learner = new Learner(new DSL.CFG("", appliedTerminalsNoCost, appliedTerminalsCost));
        learner.learn_ablation(sketch, examples, gt);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    logAfter();
  }

  public void logFirst() {

    try (FileWriter fw = new FileWriter(logPath, false); BufferedWriter bw = new BufferedWriter(fw)) {

      bw.write(Run.getConfig() + "\n");

      bw.write("Sketch: " + sketch + "\n");
      bw.write("false" + "\n");
      bw.write("Total number of examples: " + examples.size() + "\n");

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }


  public void logAfter() {
    try (FileWriter fw = new FileWriter(logPath, true);
        BufferedWriter bw = new BufferedWriter(fw);

    ) {

      bw.write("=================================" + "\n");
      bw.write(Main.succ + "\n");

      if (Main.succ) {

        bw.write("Sketch: " + sketch + "\n");
        System.out.println("Number of examples used: " + this.examples.size());

        bw.write("Learned program: " + Main.leanredProgram + "\n");
        bw.write("GT program: " + this.gt + "\n");
        bw.write("Match gt: " + Main.matchGT + "\n");
        bw.write("Total time: " + Main.synthesizeTime + "\n");
        bw.write("Pruning construction time: " + Main.pruningConstructionTime + "\n");
        bw.write("Pruning evaluate time: " + Main.pruningEvaluateTime + "\n");
        bw.write("Subsume generate time: " + Main.subsumeGenerateTime + "\n");
        bw.write("Subsume checking time: " + Main.subsumeCheckingTime + "\n");
        bw.write("Linear solving time: " + Main.solverLinearRunningTime + "\n");
        bw.write("Non linear solving time: " + Main.solverNonLinearRunningTime + "\n");

        bw.write("Number of linear formula: " + Main.solverLinearCount + "\n");
        bw.write("Number of non-linear formula: " + Main.solverNonLinearCount + "\n");
        bw.write("Number of total states: " + Main.totalStatesCount + "\n");
        bw.write("Number of pruned states: " + Main.prunedStatesCount + "\n");
        bw.write("Number of Skipped pruning: " + Main.skipPruningCount + "\n");
        bw.write("Number of evaluated states: " + Main.evaluatedCount + "\n");
        bw.write("Number of polled states: " + Main.polledStatesCount + "\n");
        bw.write("Number of subsume pruned program hit: " + Main.subsumePrunedHit + "\n");
        bw.write("Number of subsume not pruned program hit: " + Main.subsumeNotPrunedHit + "\n");
        bw.write("Number of approximated eval called: " + Main.approximatedEvalCount + "\n");

      } else {

        bw.write("FAILED" + "\n");
      }

    } catch (Exception exception) {
      exception.printStackTrace();
    }

  }

}
