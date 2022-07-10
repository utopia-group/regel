package resnax;

import resnax.so.Benchmark;
import resnax.so.Test;

import java.util.Arrays;

public class Main {

  // some run config
  public final static int RUN_BENCHMARK = 1;
  public final static int RUN_BENCHMARK_TEST = 0;
  public final static int RUN_PROGRAM_TEST = 0;
  public final static int RUN_EVALUATE_TEST = 0;

  // depth constraint
  public static int DEPTH_LIMIT = 100;

  // some flags
  // 0 = NOT ENABLED, 1 = ENABLED
  public static int PRUNED_SUBSUMPTION_ENABLED = 1;
  public static int NOT_PRUNED_SUBSUMPTION_ENABLED = 1;
  public static int PRUNING_ENABLED = 1;
  public static int SYMBOLOC_ENABLED = 1;
  public static int FTA_ENABLED = 0;
  public static int REPEAT_OPTION = 0;      // 0 = NOTHING, 1 = EXCLUDE REPEAT
  public final static int DEBUG = 0;
  public final static int PRINT = 0;
  public final static int SPECIAL_PRINT = 0;
  public final static int SOLVER_DEBUG = 0;
  public final static int WRITE_PRUNED_FILES = 0;

  // interactive mode params
  public static int INTERACT = 0;
  public static int OUTPUT_5 = 0;
    public static int OUTPUT_5_SIZE = 1;
  public static boolean syntaticErrorSketch = false;
  public static String regex;
  public static int MODE = 0;


  public static int USE_PATTERN_LIB = 0;
  public static boolean AUTOMATON_MINIMIZE = false;
  public static int PRUNE_ONLY_LEAF = 1;
  public static boolean REPEATATLEAST_1_CONSTRAIN = true;

  // some constants
  public static int K_MIN = 1;
  public static int K_MAX = 10;     // Benchmark.java might modify it
  public static int SUBSUMPTION_DEPTH = 2;
  public static int STRINGBUILDER_CAPACITY = 80;
  public static int MIN_NUM_TERMINAL_PRUNING = 0;

  // some function cost
  // n = 10
  public static double CONCAT_COST = 76.54;
  public static double OPTIONAL_COST = 76.58;
  public static double REPEAT_COST = 76.62;
  public static double REPEATAL_COST = 76.66;
  public static double REPEATR_COST = 76.70;
  public static double STAR_COST = 76.74;
  public static double OR_COST = 76.78;
  public static double AND_COST = 76.82;
  public static double NOT_COST = 76.86;
  public static double SW_COST = 76.90;
  public static double EW_COST = 76.94;
  public static double CONTAIN_COST = 76.98;
  public static double OTHER_COST = 77.0;

  // some temrinal costs
  public static double TERMINAL_COST = 6.0;
  public static double TERMINAL_ANY = 6.0;
  public static double TERMINAL_ALPHANUM = 6.1;
  public static double TERMINAL_LET_NUM = 6.2;
  public static double TERMINAL_NUM19_DOT_COMMA = 6.3;
  public static double TERMINAL_CAP_LOW = 6.4;
  public static double TERMINAL_OTHERS = 6.5;

  // some extra cost
  public static double SPECIAL_REPEATATLEAST_1 = 1.0;     // TODO: NOT SURE IF THIS COST IS CORRECT
  public static double EXTRA_REPEAT_COST = 1000.0;
  public static int EXTRA_REPEAT_THRESHOLD = 3;    // maximum amount of extra repeat (repeat generated not by rf) allowed   // TODO: used be 2, change to 3 to adopt PLDI
  public static double CONSECUTIVE_REPEAT_COST = 1000.0;
  public static double APPLIED_TERMINAL_COST = 1000.0;

  public static double MORE_THAN_ONE_NOT = 1000.0;     // more than one not under the same branch
  public static double REPEAT_WITHIN_NOT = 1000.0;       // only increase cost during symbol expansion

  public static double SW_EW_CONTAIN_IN_REPEAT = 1000.00;  // sw,ew,contain in a repeat operatror
  public static double NOT_NOT_CONTAIN_SW_EW_PATTERN = 2000.0;

  public static double NOT_TERMINAL_PATTERN = 1000.0;

  // some measurements

  public static int skipPruningCount;

  public static int totalStatesCount;
  public static int addedStatesCount;
  public static int approximatedEvalCount;
  public static int prunedStatesCount;
  public static int polledStatesCount;
  public static int evaluatedCount;
  public static int subsumeAvoidPruning;      // this is an estimated count, should be more than this number

  public static int solverNonLinearCount;
  public static int solverLinearCount;

  public static double synthesizeTime;
  public static double solverNonLinearRunningTime;
  public static double solverLinearRunningTime;
  public static double pruningConstructionTime;
  public static double pruningEvaluateTime;
  public static double subsumeGenerateTime;
  public static double subsumeCheckingTime;

  public static double solveTime;
  public static double enumerateTime;
  public static double evaluateTime;


  public static boolean succ;
  public static String leanredProgram;
  public static boolean matchGT;

  public static int subsumePrunedHit;
  public static int subsumeNotPrunedHit;

  public static void main(String[] args) {
    MODE = Integer.parseInt(args[0]);

    if (MODE == 0) resnax.so.Run.run(Arrays.copyOfRange(args, 1, args.length));
    if (MODE == 1) resnax.deepregex.Run.run(Arrays.copyOfRange(args, 1, args.length));
  }

  // mode 1 = nl + io + p + sc
  // mode 2 = nl + io + p
  // mode 3 = nl + io + sc
  // mode 4 = nl + io
  // mode 5 = io + p + sc
  public static void checkMode(String mode) {
    switch (mode) {
      case "1":
        resnax.Main.PRUNING_ENABLED = 1;
        resnax.Main.SYMBOLOC_ENABLED = 1;
        break;
      case "2":
        resnax.Main.PRUNING_ENABLED = 1;
        resnax.Main.SYMBOLOC_ENABLED = 0;
        break;
      case "3":
        resnax.Main.PRUNING_ENABLED = 0;
        resnax.Main.SYMBOLOC_ENABLED = 1;
        break;
      case "4":
        resnax.Main.PRUNING_ENABLED = 0;
        resnax.Main.SYMBOLOC_ENABLED = 0;
        break;
      case "5":
        resnax.Main.PRUNING_ENABLED = 1;
        resnax.Main.SYMBOLOC_ENABLED = 1;
        if (INTERACT == 0) OUTPUT_5 = 1;
        break;
      case "6":
        Main.FTA_ENABLED = 1;
        break;
      default:
        assert false : "Run mode not accepted";
    }

  }








}
