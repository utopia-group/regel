package resnax.deepregex;

public class Run {

    public static void run(String[] args) {
        setArgs();
        if (resnax.Main.RUN_BENCHMARK == 1) runBenchmark(args);
        if (resnax.Main.RUN_BENCHMARK_TEST == 1 || resnax.Main.RUN_PROGRAM_TEST == 1) runTest(args);
    }

    private static void setArgs() {

        // some flags
        // 0 = not enabled, 1 = enabled
        resnax.Main.DEPTH_LIMIT = 100;
        resnax.Main.PRUNED_SUBSUMPTION_ENABLED = 0;
        resnax.Main.NOT_PRUNED_SUBSUMPTION_ENABLED = 0;
        resnax.Main.PRUNING_ENABLED = 1;
        resnax.Main.SYMBOLOC_ENABLED = 1;
        resnax.Main.REPEAT_OPTION = 0;

        resnax.Main.PRUNE_ONLY_LEAF = 1;
        resnax.Main.REPEATATLEAST_1_CONSTRAIN = false;

        resnax.Main.K_MIN = 1;
        resnax.Main.K_MAX = 10;
        resnax.Main.SUBSUMPTION_DEPTH = 2;
        resnax.Main.STRINGBUILDER_CAPACITY = 80;
        resnax.Main.MIN_NUM_TERMINAL_PRUNING = 0;

        resnax.Main.CONCAT_COST = 76.54;
        resnax.Main.OPTIONAL_COST = 76.58;
        resnax.Main.REPEAT_COST = 76.62;
        resnax.Main.REPEATAL_COST = 76.66;
        resnax.Main.REPEATR_COST = 76.70;
        //  resnax.Run.STAR_COST = 76.74;
        resnax.Main.STAR_COST = 76.58;
        resnax.Main.OR_COST = 76.78;
        resnax.Main.AND_COST = 76.82;
        resnax.Main.NOT_COST = 76.86;
        resnax.Main.SW_COST = 76.90;
        resnax.Main.EW_COST = 76.94;
        resnax.Main.CONTAIN_COST = 76.98;
        resnax.Main.OTHER_COST = 77.0;

        // some temrinal costs
        resnax.Main.TERMINAL_COST = 6.0;
        resnax.Main.TERMINAL_ANY = 6.0;
        resnax.Main.TERMINAL_ALPHANUM = 6.1;
        resnax.Main.TERMINAL_LET_NUM = 6.2;
        resnax.Main.TERMINAL_NUM19_DOT_COMMA = 6.3;
        resnax.Main.TERMINAL_CAP_LOW = 6.4;
        resnax.Main.TERMINAL_OTHERS = 6.5;

        // some extra cost
        resnax.Main.SPECIAL_REPEATATLEAST_1 = 1.0;
        resnax.Main.EXTRA_REPEAT_COST = 1000.0;
        resnax.Main.EXTRA_REPEAT_THRESHOLD = 1;    // maximum amount of extra repeat (repeat generated not by rf) allowed
        resnax.Main.CONSECUTIVE_REPEAT_COST = 1000.0;
        resnax.Main.APPLIED_TERMINAL_COST = 1000.0;

        resnax.Main.MORE_THAN_ONE_NOT = 1000.0;     // more than one not under the same branch
        resnax.Main.REPEAT_WITHIN_NOT = 0.0;       // only increase cost during symbol expansion

        resnax.Main.SW_EW_CONTAIN_IN_REPEAT = 0.0;  // sw,ew,contain in a repeat operatror
        resnax.Main.NOT_NOT_CONTAIN_SW_EW_PATTERN = 0.0;

        resnax.Main.NOT_TERMINAL_PATTERN = 0.0;
    }

    private static void runBenchmark(String[] args) {

        assert args.length == 6;

        String benchmarkPath = args[0];
        String logPath = args[1];
        String sketch = args[2];
        String index = args[3];
        resnax.Main.INTERACT = Integer.parseInt(args[5]);
        resnax.Main.checkMode(args[4]);

        Benchmark benchmark = Benchmark.read(benchmarkPath, logPath, sketch, index);
        if (resnax.Main.INTERACT == 0)
            benchmark.run_interactive();
        else
            benchmark.run();

    }

    private static void runTest(String[] args) {

    }

    public static String getConfig() {
        return "";
    }

}
