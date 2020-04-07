import os, sys, signal
import argparse
from multiprocessing import Process, Pool
from shlex import split
import subprocess
import time


def _parse_args():
    parser = argparse.ArgumentParser(description="resnax")
    # ---- benchmark specific arguments ------
    parser.add_argument("--benchmark", type=str, dest="benchmark", default="deepregex")
    parser.add_argument("--log_name", type=str, dest="log_name", default="log")
    parser.add_argument("--sketch", type=str, dest='sketch', default="sketch")
    parser.add_argument("--dataset_mode", type=str, dest="dataset_mode", default="0")
    parser.add_argument("--mem_max", type=int, dest="mem_max", default=20)
    parser.add_argument("--allow_missing_sketch", type=bool, dest="allow_missing_sketch", default=True)
    # 1: normal
    # 2: prune
    # 4: pure-enumeration
    # 5: example-only
    parser.add_argument("--synth_mode", type=int, dest="synth_mode", default=1)

    # ----- benchmark independent arguments
    parser.add_argument("--java_path", type=str, dest="java_path", default="~/")
    parser.add_argument("--parallel", type=bool, dest='parallel', default=True)
    parser.add_argument("--z3libpath", type=str, dest="z3libpath", default="resnax/lib")
    parser.add_argument("--cpath", type=str, dest="cpath", default="resnax/jars/resnax.jar:resnax/lib/*")
    parser.add_argument("--timeout", type=int, dest="timeout", default=10)  # if timeout == -1 then no timeout
    parser.add_argument("--main", type=str, dest="main", default="resnax.Main")

    # ------ only apply to so
    parser.add_argument("--benchmarknum", type=str, dest="benchmarknum", default="")
    parser.add_argument("--processnum", type=int, dest="processnum", default=5)

    args = parser.parse_args()

    args.dir = os.getcwd()
    args.java_path = "{}/{}".format(args.dir, "resnax")

    args.sketch_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, args.sketch)
    args.benchmark_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, "benchmark")
    args.log_path = "{}/exp/{}/{}".format(args.dir, args.benchmark, args.log_name)

    if args.benchmark == "deepregex":
        args.dataset_mode = "1"

    return args

class Run:
    def __init__(self, benchmark, args):
        self.benchmark = benchmark
        self.args = args

    def parse_java_command(self, sketch, benchmark):

        bpath = '{}/{}'.format(self.args.benchmark_path, benchmark)

        if self.args.benchmark == "so":

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

        elif self.args.benchmark == "deepregex":

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

    def run(self, sketch):
        print(sketch[0], "Started")
        cmd = self.parse_java_command(sketch, self.benchmark)
        try:
            output = str(subprocess.check_output(cmd, shell=True, timeout=self.args.timeout))
            # print(output)
            print(sketch[0], "Finished")
        except subprocess.TimeoutExpired:
            print(sketch[0], "Time out")

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
            
            if args.parallel:
                worker = Run(benchmark, args)
                try:
                    with Pool(args.processnum) as p:
                        p.map(worker.run, sketches)

                except subprocess.CalledProcessError as e:
                    print("timeout raised")

            else:
                for sk in sketches:
                    print(sk)
                    worker = Run(benchmark, args)
                    worker.run(sk)
                    
        os.system('mv \"{0}\" \"{0}\"1'.format(args.log_path))


if __name__ == '__main__':
    os.system("ant -buildfile resnax/build.xml clean")
    os.system("ant -buildfile resnax/build.xml resnax")
    main()
    print("exp.py ends")
