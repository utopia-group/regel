import os
import argparse
import subprocess
import signal
import csv
from multiprocessing import Process, Pool
from synthesize_benchmark import parse_descriptions

def _parse_args():

    def str2bool(v):
        if isinstance(v, bool):
            return v
        if v.lower() in ('yes', 'true', 't', 'y', '1'):
            return True
        elif v.lower() in ('no', 'false', 'f', 'n', '0'):
            return False
        else:
            raise argparse.ArgumentTypeError('Boolean value expected.')

    parser = argparse.ArgumentParser()

    parser.add_argument("--run_mode", type=str, dest="run_mode", default="0") #run_mode=1 if run benchmarks, 0 otherwise
    parser.add_argument("--trained_model", type=str, dest="trained_model", default='pretrained_models/pretrained_so')
    parser.add_argument("--sketch_num", type=int, dest="sketch_num", default=25)

    parser.add_argument("--benchmark", type=str, dest="benchmark", default="deepregex")
    parser.add_argument("--synth_mode", type=str, dest="synth_mode", default="1")   # run_mode=1 if uses sketch, run_mode=5 if not use sketch
    parser.add_argument("--processnum", type=int, dest="processnum", default=5)
    parser.add_argument("--mem_max", type=int, dest="mem_max", default=20)
    parser.add_argument("--top", type=int, dest="top", default=5)   # return top-k
    parser.add_argument("--timeout", type=int, dest="timeout", default=60)
    parser.add_argument("--max_iter", type=int, dest="max_iter", default=5)


    parser.add_argument("--dir", type=str, dest="dir", default="")
    parser.add_argument("--interact_dir", type=str, dest="interact_dir", default="interactive")
    parser.add_argument("--nl_path", type=str, dest="nl_path")
    parser.add_argument("--example_path", type=str, dest="example_path")
    parser.add_argument("--example_cache_path", type=str, dest="example_cache_path")
    parser.add_argument("--log_path", type=str, dest="log_path")
    parser.add_argument("--sketch_path", type=str, dest="sketch_path")
    parser.add_argument("--dataset_mode", type=str, dest="dataset_mode", default="0")
    parser.add_argument("--save_history", type=str2bool, dest="save_history", default=False)

    args = parser.parse_args()
    args.dir = "{}/{}".format(os.getcwd(), args.dir) if not args.dir == "" else os.getcwd()

    if args.run_mode == "0":
        args.benchmark = "customize"

    args.example_path = '{}/exp/{}/{}/example/{}'.format(args.dir, args.interact_dir, args.benchmark, args.synth_mode)
    args.example_cache_path = '{}/exp/{}/{}/examples_cache'.format(args.dir, args.interact_dir, args.benchmark)
    args.log_path = '{}/exp/{}/{}/logs/{}'.format(args.dir, args.interact_dir, args.benchmark, args.synth_mode)

    args.benchmark_path = '{}/exp/{}/benchmark'.format(args.dir, args.benchmark)
    args.sketch_path = '{}/exp/{}/sketch'.format(args.dir, args.benchmark)
    if args.benchmark == "deepregex":
        args.dataset_mode = "1"

    return args

def evaluate_ex(regex, example, flag):

    out = subprocess.check_output(
        ['java', '-cp', 'resnax/jars/checkExample.jar', 'checkExample.Main', regex, example]
    )

    match = 'true' in str(out)
    if flag == '+':
        return match
    else:
        return not match

class Parallel():

    def __init__(self, arguments, benchmark):

        self.arguments = arguments
        self.benchmark = benchmark
        self.timeout = self.arguments.timeout
        self.java_path = "{}/".format(arguments.dir)
        self.z3libpath = "resnax/lib"
        self.cpath =  "resnax/jars/resnax.jar:resnax/lib/*"
        self.main =  "resnax.Main"
        self.mem_max = "20"

        self.bpath = '{}/{}'.format(self.arguments.example_path, self.benchmark)

    def parse_java_command(self, sketch):

        if self.arguments.run_mode == "0":
            java_command = 'exec java -Xmx{}G -Djava.library.path={} -cp {} -ea {} {} \"{}\" \"{}\" \"{}\" {} {} {}'.format(
                    str(self.mem_max),
                            self.z3libpath,
                            self.cpath,
                            self.main,
                            self.arguments.dataset_mode,
                            self.bpath,
                            self.arguments.log_path,
                            sketch[1],
                            str(sketch[0]),
                            str(self.arguments.synth_mode),
                            0,
                    )
        else:
            if self.arguments.benchmark == "deepregex":
                java_command = 'exec java -Xmx{}G -Djava.library.path={} -cp {} -ea {} {} \"{}\" \"{}\" \"{}\" {} {} {}'.format(
                        str(self.mem_max),
                                self.z3libpath,
                                self.cpath,
                                self.main,
                                self.arguments.dataset_mode,
                                self.bpath,
                                self.arguments.log_path,
                                sketch[1],
                                str(sketch[0]),
                                str(self.arguments.synth_mode),
                                0
                    )

            if self.arguments.benchmark == "so":
                java_command = 'exec java -Xmx{}G -Djava.library.path={} -cp {} -ea {} {} \"{}\" \"{}\" \"{}\" {} {} {}'.format(
                    str(self.mem_max),
                            self.z3libpath,
                            self.cpath,
                            self.main,
                            self.arguments.dataset_mode,
                            self.bpath,
                            self.arguments.log_path,
                            sketch[1],
                            str(sketch[0]),
                            str(self.arguments.synth_mode),
                            0
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
            op0_split = op[0].split(": ")
            record["p"] = op0_split[0]
            record["cost"] = float(op0_split[1])
            if record["cost"] == 0.0:
                record["time"] = 0.0
            else:
                record["time"] = float(op[2])
            record["regex"] = op[1]
            record["gt"] = op[3]


        return record

    def parse_five(self, output, sketch):
        op = output.rsplit("SO")

        out = []

        for l in op:
            out.append(self.parse_normal(l, sketch))

        return out


    def run(self, sketch):

        cmd = self.parse_java_command(sketch)

        # print("cmd:", cmd)
        try:
            output = str(subprocess.check_output(cmd, shell=True, timeout=self.timeout))

            # print("output:", output)

            if self.arguments.synth_mode == "5" :
                return self.parse_five(output[2:-3], sketch)
            else:
                return self.parse_normal(output[2:-3], sketch)

        except (subprocess.TimeoutExpired, subprocess.CalledProcessError) as e:
            record = {}
            record["b"] = self.benchmark
            record["rank"] = sketch[0]
            record["sketch"] = sketch[1]
            record["p"] = "timeout"
            record["cost"] = 999999.0
            record["time"] = self.timeout
            record["regex"] = "null"
            record["gt"] = "false"

            if self.arguments.synth_mode == "5" :
                return [record]
            else:
                return record


class Run():
    def __init__(self, args):
        self.args = args
        self.history_file = '{}/history'.format(self.args.log_path)

    def read_history_file(self):

        if os.path.exists(self.history_file):
            history = [line.strip() for line in open(self.history_file).readlines()]
            return history
        else:
            return []

    def write_history_file(self, new_history):

        open(self.history_file,'w').writelines(['{}\n'.format(item) for item in new_history])

    def read_sketch(self, b):

        sketch = []
        sk = open('{}/{}'.format(self.args.sketch_path ,b)).readlines()

        for lines in sk:
            if len(lines) == 0:
                continue
            lines_split = lines.split(" ", 1)
            sketch.append((lines_split[0], lines_split[1].rstrip()))

        return sketch

    def write_new_example(self, b, example_lines, gt_lines, nl=""):

        example = open('{}/{}'.format(self.args.example_path, b), 'w')

        if not nl == "":
            example.write('// natural language\n{}\n\n'.format(nl))

        for e in example_lines:
            if len(e) == 0:
                continue
            example.write('{}\n'.format(e))

        example.write('\n')

        for g in gt_lines:
            example.write('{}\n'.format(g))

        example.close()

    def read_benchmark(self, b):

        read_b_path = '{}/{}'.format(self.args.benchmark_path, b)

        if not os.path.exists(read_b_path):
            return None

        ex =[line.rstrip() for line in  open(read_b_path )]

        nl_idx = -1
        example_idx = -1
        gt_idx = -1
        for i in range(len(ex)):
            if ex[i].startswith("//"):
                if nl_idx == -1:
                    nl_idx = i
                elif example_idx == -1:
                    example_idx = i
                else:
                    gt_idx = i
                    break

        if example_idx == -1 or gt_idx == -1:
            assert False

        nl = ex[(nl_idx) + 1]
        example = ex[(nl_idx + 2):gt_idx]
        gt = ex[gt_idx:]

        return nl, (example, gt)

    def write_benchmark(self, b, nl, ex, gt=None):
        write_b_path = '{}/{}'.format(self.args.benchmark_path, b)

        with open(write_b_path, "w") as f:
            f.write("// natural language\n{}\n\n".format(nl))
            f.write("// example\n{}\n\n".format('\n'.join(["\"{}\",{}".format(e['ex'], e['sign']) for e in ex])))
            if gt is not None:
                f.write("// gt\n{}".format(gt))
            else:
                f.write("// gt\n{}".format("na"))

    def copy_benchmark_to_iteractive(self, b):
        os.system('cp \"{0}/{2}\" \"{1}/{2}\"'.format(self.args.benchmark_path, self.args.example_path, b))

    def read_example_cache(self, b):
        example_cache = []
        example_cache_file = '{}/{}'.format(self.args.example_cache_path, b)
        if os.path.exists(example_cache_file):
            with open(example_cache_file) as tsv:
                for line in csv.reader(tsv, delimiter="\t"):
                    example_cache.append({'ex': line[0], 'sign': line[1]})

        return example_cache

    def write_example_cache(self, b, cache):

        with open('{}/{}'.format(self.args.example_cache_path, b),'w') as tsv:
            for item in cache:
                tsv.write('{}\t{}\t\n'.format(item["ex"], item["sign"]))

    def deepregex_sort(self, results):
        # first sort by rank
        results = sorted(results, key = lambda i: i["rank"])
        results = sorted(results, key = lambda i: i["time"])

        return results

    def so_sort(self, results):
        results = sorted(results, key = lambda i: i["time"])
        return results

    #'iter,benchmark,rank,sketch,result,cost,time,matchGT\n'
    def write_output(self, output):

        output_file = '{}/raw_output.csv'.format(self.args.log_path)
        new_file = os.path.exists(output_file)
        with open(output_file,'a') as of:
            # if new_file:
                # of.write('iter,benchmark,rank,sketch,result,cost,time,matchGT\n')
            for item in output:
                of.write('{},{},{},\"{}\",\"{}\",{},{},{}\n'.format(item["iter"],item["b"],item["rank"],item["sketch"],item["p"],item["cost"],item["time"],item["gt"]))

    def regex_print(self, results):
        for i in range(len(results)):
            print("[{}] {}".format(i, results[i]['p']))

    def test_benchmark(self, b, nl, sketch, ex, cache):

        print("Running benchmark {}".format(b))
        cache_chosen = []

        output = []

        for i in range(self.args.max_iter):
            print("NL:{}".format(nl))
            print("Example:{}".format('\n'.join(ex[0])))

            print("Running the synthesizer...")

            worker = Parallel(self.args, b)

            with Pool(self.args.processnum) as p:
                results = p.map(worker.run, sketch)

            if self.args.synth_mode == "5":
                results = results[0]
            # print(results)

            if self.args.benchmark == "so" or self.args.benchmark == "customize":
                results = self.so_sort(results)
            elif self.args.benchmark == "deepregex":
                results = self.deepregex_sort(results)

            # find top 5
            top = results[0:self.args.top]
            top = [dict(item, **{'iter':i}) for item in top]

            if self.args.run_mode == "0":

                if top[0]['time'] >= self.args.timeout:
                    print("Regel times out. Cannot solve this problem.")
                    output.extend(top)
                    break

                print("Regel gives the following output:")
                self.regex_print(top)
                print()

                question1 = input("Any correct regex (y/n)? ")
                if question1 == "y":
                    while True:
                        question2 = input("enter correct regex number: ")
                        try:
                            value = int(question2)
                            if value >= len(top):
                                print("regex index is not correct")
                                continue
                            break
                        except ValueError:
                            print("you should enter a number")
                    top[int(question2)]['gt'] = 'true'
                    output.extend(top)
                    break

                elif question1 == "n":
                    output.extend(top)

                    new_example = []
                    print("Please enter two examples to disambiguate the regexes: ")
                    for i in range(2):
                        while True:
                            e = input("example: ")
                            sign = input("+/-: ")
                            confirm = input('Please enter \'y\' if this is the example: \"{}\",{}: '.format(e, sign))

                            if "y" in confirm:
                                new_example.append({"ex": e, "sign": sign})
                                break
                    cache.extend(new_example)

                    for item in new_example:
                        ex[0].append('\"{}\",{}'.format(item["ex"], item["sign"]))

                    self.write_new_example(b, ex[0], ex[1], nl=nl)
                    print(cache)

            else:

                print([item['p'] for item in top])
                output.extend(top)
                if 'true' in [item['gt'] for item in top]:
                    print("Success")
                    break
                elif top[0]['time'] >= self.args.timeout:
                    print("Timeout")
                    break
                elif "null" in top[0]['p']:
                    print("Failed")
                    break
                else:

                    new_example = []

                    # first find any example in the cache could use to dispute any regex in top_5
                    for r in [item['regex'] for item in top]:
                        if r == "null" or r == "":
                            break
                        for e in cache:
                            if not evaluate_ex(r, e["ex"], e["sign"]):
                                if  e["ex"] not in cache_chosen:
                                    new_example.append(e)
                                    cache_chosen.append(e["ex"])

                                if len(new_example) >= 2:
                                    break
                        if len(new_example) >= 2:
                            break

                    # if no, enter example
                    if len(new_example) < 2:
                        new_example.append({"ex": input("first example: "), "sign": input("+/-? ")})
                        new_example.append({"ex": input("second example: "), "sign": input("+/-? ")})
                        cache.extend(new_example)

                    for item in new_example:
                        ex[0].append('\"{}\",{}'.format(item["ex"], item["sign"]))

                    self.write_new_example(b, ex[0], ex[1], nl=nl)

                    # print(cache)


        # write cache at the end
        if len(cache) > 0:
            self.write_example_cache(b, cache)

        return output

    def run_customize(self):
        overall_output = []

        try :
            while True:
                print("=================")
                print("Enter filename below. Press 'Enter' to quit and save the results. ")
                file_name = input("id: ")
                if file_name == "":
                    break
                nl = input("nl: ").rstrip()

                examples = []
                print("Enter examples below. Press 'Enter' if finish. ")
                while True:
                    ex = input("example: ").rstrip()
                    if ex == "":
                        break
                    sign = input("+ or - (enter '+' for positive example, '-' for negative one) ? ").rstrip()
                    examples.append({"ex": ex, "sign": sign})
                    cache = self.read_example_cache(file_name)

                print("generating sketches...")
                sketch = list(enumerate(parse_descriptions([nl], self.args.trained_model, self.args.sketch_num)[0],1))
                # print("sketches: {} ".format(sketch))

                self.write_benchmark(file_name, nl, examples)
                self.copy_benchmark_to_iteractive(file_name)

                # format examples to be use in the future
                examples_gt = (["// examples"] + ["\"{}\",{}".format(e['ex'], e['sign']) for e in examples] ,["// gt", "na"])

                overall_output.extend(self.test_benchmark(file_name, nl, sketch, examples_gt, cache))

        except:
            self.write_output(overall_output)
            return

        self.write_output(overall_output)
        return

    def run_exp(self, benchmarksnum):

        history = self.read_history_file()
        overall_output = []

        try:
            for i in benchmarksnum:

                if str(i).startswith('.'):
                    continue

                if str(i) in history:
                    continue

                # read sketches and nl
                if self.args.synth_mode == "1":
                    sketch = self.read_sketch(i)
                elif self.args.synth_mode == "5":
                    sketch = [("b","?")]

                binfo = self.read_benchmark(str(i))
                if binfo is None:
                    continue
                else:
                    nl, initial_ex = binfo

                # print(sketch, nl, initial_ex)

                self.copy_benchmark_to_iteractive(str(i))
                # read examples cache
                cache = self.read_example_cache(i)

                overall_output.extend(self.test_benchmark(str(i), nl, sketch, initial_ex, cache))

                history.append(str(i))

        except:
            if self.args.save_history: self.write_history_file(history)
            self.write_output(overall_output)
            return

        if self.args.save_history: self.write_history_file(history)
        self.write_output(overall_output)

        return

def prepare_folder(args):
    if not os.path.exists(args.example_path):
        os.system('mkdir -p \"{}\"'.format(args.example_path))
        print("{} created".format(args.example_path))
    if not os.path.exists(args.example_cache_path):
        os.system('mkdir -p \"{}\"'.format(args.example_cache_path))
        print("{} created".format(args.example_cache_path))
    if not os.path.exists(args.log_path):
        os.system('mkdir -p \"{}\"'.format(args.log_path))
        print("{} created".format(args.log_path))
    if args.run_mode == "0" and not os.path.exists(args.benchmark_path):
        os.system('mkdir -p \"{}\"'.format(args.benchmark_path))
        print("{} created".format(args.benchmark_path))

def interactive():

    args = _parse_args()
    signal.signal(signal.SIGINT, signal.default_int_handler)

    # print(args)

    prepare_folder(args)
    run = Run(args)

    if args.run_mode == "0":
        run.run_customize()
    else:
        if args.benchmark == "deepregex":
            benchmarks = [f for f in os.listdir(args.benchmark_path) if os.path.isfile(os.path.join(args.benchmark_path, f))]
            run.run_exp(list(benchmarks))

        elif args.benchmark == "so":
            benchmarks = [f for f in os.listdir(args.benchmark_path) if os.path.isfile(os.path.join(args.benchmark_path, f))]
            run.run_exp(benchmarks)

        else:
            benchmarks = [f for f in os.listdir(args.benchmark_path) if os.path.isfile(os.path.join(args.benchmark_path, f)) and not f.startswith(".")]
            run.run_exp(benchmarks)



if __name__ == "__main__":
    os.system("ant -buildfile resnax/build.xml clean")
    os.system("ant -buildfile resnax/build.xml resnax")
    interactive()
    print("finished")
