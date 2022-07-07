package resnax.deepregex;

public class Test {

    public static String benchmark_path = "../exp/deepregex/benchmark";
    public static String sketch_path = "../exp/deepregex/sketch";

    public static void benchmarkTest() {
        Benchmark b = Benchmark.read(Test.benchmark_path + "/" + "1", "?");
        b.run();
    }
}
