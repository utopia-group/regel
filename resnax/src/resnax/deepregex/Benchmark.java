package resnax.deepregex;

import resnax.BenchmarkRes;
import resnax.Learner;
import resnax.Main;
import resnax.util.MultiMap;
import resnax.util.SetMultiMap;
import resnax.synthesizer.DSL;
import resnax.Example;

import java.io.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class Benchmark {

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
            int min = 10;
            // create examples
            {

                // skip nl
                {
                    String line = br.readLine();
                    for (; line.isEmpty() || line.startsWith("//"); line = br.readLine())
                        ;
                    for (; line != null && !line.isEmpty(); line = br.readLine()) {
                        if (line.startsWith("//")) continue;
                        break;
                    }
                }

                // examples
                {
                    String line = br.readLine();
                    for (; line.isEmpty() || line.startsWith("//"); line = br.readLine())
                        ;
                    for (; !line.isEmpty(); line = br.readLine()) {
                        if (line.startsWith("//")) continue;
                        int splitIndex = line.lastIndexOf(',');
                        String str = line.substring(1, splitIndex - 1);
                        String type = line.substring(splitIndex + 1);
                        k_max = Integer.max(k_max, str.length());

                        if (type.equals("+")) {
                            if (str.length() < min) {
                                min = str.length();
                            }
                            ret.examples.add(new Example(str, true));
                        } else if (type.equals("-")) ret.examples.add(new Example(str, false));
                        else throw new RuntimeException("example type incorrect");

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

            }

            // main max
            {
                Main.K_MIN = Math.max(1, min);
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
        if (bres != null && bres.succ) {

            System.out.println(bres.program.toOutput() + "`" + bres.regex + "`" + bres.time + "`" + bres.matchGt);
        } else {
            System.out.println("null" + "`" + "null" + "`" + "null" + "`" + "False");
        }
    }

    public void run_interactive() {
        learner = new Learner(new DSL.CFG(""));

        if (Main.OUTPUT_5 == 1) {
            List<BenchmarkRes> bres = learner.learn_output_5(sketch, examples, gt);
            output_interact(bres);
        } else {
            output_interact(learner.learn(sketch, examples, gt));
        }
    }

    public void run() {
//        System.out.println("Running " + name);

        logFirst();

        {
            try {
                learner = new Learner(new DSL.CFG(""));
                BenchmarkRes bres = learner.learn_ablation(sketch, examples, gt);
                output_interact(bres);
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
//                System.out.println("Number of examples used: " + this.examples.size());

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
