import os, sys, signal
import argparse
from multiprocessing import Process, Pool
from shlex import split
import subprocess
import time


def _parse_args():
    parser = argparse.ArgumentParser(description="resnax")
    # ---- benchmark specific arguments ------
    parser.add_argument("--benchmark", type=str, dest="benchmark", default="deepregex", help="benchmark set to run, can be so, deepregex, or any name as long as there exists a benchmark folder in exp")
    parser.add_argument("--log_name", type=str, dest="log_name", default="log", help="the name of the log folder")
    parser.add_argument("--sketch", type=str, dest='sketch', default="sketch", help="the name of the sketch folder")
    parser.add_argument("--dataset_mode", type=str, dest="dataset_mode", default="0")
    parser.add_argument("--mem_max", type=int, dest="mem_max", default=20)
    parser.add_argument("--top", type=int, dest="top", default=25, help="the top n synthesized results to print")
    parser.add_argument("--allow_missing_sketch", type=bool, dest="allow_missing_sketch", default=True)
    parser.add_argument("--benchmark_to_run", type=str, dest="benchmark_to_run", default="all", help="if specify to 'all' then it will run all the benchmark files in the folder, otherwise it should be a comma-separated string with benchmark id")
    # 1: normal
    # 2: prune
    # 4: pure-enumeration
    # 5: example-only
    parser.add_argument("--synth_mode", type=int, dest="synth_mode", default=1, help="set to 1 to synthesize with nl and example, set to 5 to synthesize with example only")

    # ----- benchmark independent arguments
    parser.add_argument("--java_path", type=str, dest="java_path", default="~/")
    parser.add_argument("--parallel", type=bool, dest='parallel', default=True)
    parser.add_argument("--z3libpath", type=str, dest="z3libpath", default="resnax/lib")
    parser.add_argument("--cpath", type=str, dest="cpath", default="resnax/jars/resnax.jar:resnax/lib/*")
    parser.add_argument("--timeout", type=int, dest="timeout", default=10)  # if timeout == -1 then no timeout
    parser.add_argument("--main", type=str, dest="main", default="resnax.Main")

    # ------ only apply to so
    parser.add_argument("--processnum", type=int, dest="processnum", default=5, help="the number of process to parallel run the synthesizer")

    args = parser.parse_args()

    args.dir = os.getcwd()
    args.java_path = "{}/{}".format(args.dir, "resnax")

    args.sketch_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, args.sketch)
    args.benchmark_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, "benchmark")
    args.log_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, args.log_name)

    if args.benchmark == "deepregex":
        args.dataset_mode = "1"

    if args.benchmark_to_run == "all":
        args.benchmark_to_run = []
    else:
        args.benchmark_to_run = args.benchmark_to_run.split(",")

    return args

class Run:
    def __init__(self, benchmark, args):
        self.benchmark = benchmark
        self.args = args

    def parse_java_command(self, sketch, benchmark):

        bpath = '{}/{}'.format(self.args.benchmark_path, benchmark)

        java_command = 'exec java -Xmx{}G -Djava.library.path={} -cp {} -ea {} {} \"{}\" \"{}\" \"{}\" {} {} {}'.format(
                    str(self.args.mem_max),
                    self.args.z3libpath,
                    self.args.cpath,
                    self.args.main,
                    self.args.dataset_mode,
                    bpath,
                    self.args.log_path,
                    sketch[1],
                    str(sketch[0]),
                    str(self.args.synth_mode),
                    1
            )

        return java_command

    def parse_normal(self, output, sketch):

        op = output.rsplit("`")

        record = {}
        record["b"] = self.benchmark
        record["rank"] = sketch[0]
        record["sketch"] = sketch[1]
        if "null" in op[0] or op[0] == "":
            record["p"] = "null"
            record["cost"] = 999999.0
            record["time"] = 999999.0
            record["regex"] = "null"
            record["gt"] = "false"
        else:
            record["p"] = op[0]
            record["cost"] = float(op[0].split(": ")[1])
            if record["cost"] == 0.0:
                record["time"] = 0.0
            else:
                record["time"] = float(op[2])
            record["regex"] = op[1]
            record["gt"] = op[3]


        # print(record)
        return record

    def run(self, sketch):
        print(sketch[0], "Started")
        cmd = self.parse_java_command(sketch, self.benchmark)
        try:
            output = str(subprocess.check_output(cmd, shell=True, timeout=self.args.timeout))
            #print(output)
            print(sketch[0], "Finished")
            return self.parse_normal(output[2:-3], sketch)
        except subprocess.TimeoutExpired:
            print(sketch[0], "Time out")
            record = {}
            record["b"] = self.benchmark
            record["rank"] = sketch[0]
            record["sketch"] = sketch[1]
            record["p"] = "timeout"
            record["cost"] = 999999.0
            record["time"] = self.args.timeout
            record["regex"] = "null"
            record["gt"] = "false"
            return record

def main():
    # given a set of sketch, invoke several process
    # if one process returns, kill the rest and return.
    # parse some argumetns
    args = _parse_args()
    # print(args)

    # read benchmark files
    # print(args.log_path)
    os.system("rm -r \"{}\"".format(args.log_path))
    os.system("mkdir -p \"{}\"".format(args.log_path))
    print("directory_path:" + args.benchmark_path)
    exclude = ["benchmark"]
    for root, dirs, files in os.walk(args.benchmark_path, topdown=True):
        dirs[:] = [d for d in dirs if d not in exclude]
        for benchmark in files:
            # print(benchmark)
            if benchmark.startswith("."):
                continue

            if len(args.benchmark_to_run) > 0 and benchmark not in args.benchmark_to_run:
                continue

            # read sketch for the current benchmark
            sketches = []
            if args.synth_mode == 5:
                sketches.append(("b", "?"))
            else:
                sketch_path = '{}/{}'.format(args.sketch_path, benchmark)
                print("benchmark #: {}".format(benchmark))

                try:
                    sketch_file = open(sketch_path).readlines()
                except Exception:
                    print('missing sketch {}'.format(benchmark))
                    if args.allow_missing_sketch:
                        continue
                    else:
                        assert False

                for line in sketch_file:
                    indx = line.find(" ")
                    rank = line[0:indx]
                    sketch = (line[(indx + 1):len(line)]).strip()
                    if not sketch == "null":
                            sketches.append((rank, sketch))

            print("sketch:{}".format(sketches))

            worker = Run(benchmark, args)
            with Pool(args.processnum) as p:
                results = p.map(worker.run, sketches)

            if args.benchmark == "so":
                results = so_sort(results)
            elif args.benchmark == "deepregex":
                results = deepregex_sort(results)

            # find top-k
            top = results[0:args.top]
            print([item['p'] for item in top])


        os.system('mv \"{0}\" \"{0}\"1'.format(args.log_path))

def deepregex_sort(results):
    # first sort by rank
    results = sorted(results, key = lambda i: i["rank"])

    # print(results)
    results = sorted(results, key = lambda i: i["time"])

    # print(results)

    return results

def so_sort(results):
    results = sorted(results, key = lambda i: i["time"])
    return results

if __name__ == '__main__':
    os.system("ant -buildfile resnax/build.xml clean")
    os.system("ant -buildfile resnax/build.xml resnax")
    main()
    print("exp.py ends")
