import argparse
import os
import csv
import itertools
import subprocess

def _parse_args():

    parser = argparse.ArgumentParser()
    parser.add_argument("--benchmark", type=str, dest="benchmark", default="custom")
    parser.add_argument("--sketch_path", type=str, dest="sketch_path", default="exp/so/sketch")
    parser.add_argument("--input_path", type=str, dest="input_path", default="sempre/dataset")
    parser.add_argument("--model_dir", type=str, dest="model_dir", default='pretrained_models/pretrained_so')
    parser.add_argument("--sempre", type=str, dest="sempre", default="sempre")
    parser.add_argument("--sempre_input_file", type=str, dest="sempre_input_file", default="so.raw.txt")
    parser.add_argument("--sempre_sketch", type=str, dest="sempre_sketch", default="outputs")
    parser.add_argument("--max_sketch", type=int, dest="max_sketch", default=25)
    args = parser.parse_args()

    args.sketch_path = "exp/{}/sketch".format(args.benchmark)
    args.sempre_input_file = "{}/{}.raw.txt".format(args.input_path, args.benchmark)    
    args.sempre_sketch = "{}/outputs/{}".format(args.sempre, args.benchmark)

    if not os.path.exists(args.sketch_path):
        os.system("mkdir -p \"{}\"".format(args.sketch_path))

    if args.benchmark == "deepregex":
        args.model_dir = "pretrained_models/pretrained_turk"

    return args

def output_sketch(args, benchmarks_p):
    
    for benchmark in benchmarks_p:
        b_name = benchmark[0]
        with open("{}/{}".format(args.sketch_path, b_name), "w+") as wf:
            counter = 1
            for sketch in benchmark[1]:
                if counter > args.max_sketch:
                    break
                # print(sketch)
                wf.write("{} {}\n".format(counter, sketch))
                counter += 1


def combine_sketch(args, benchmarks):
    
    benchmarks_p_sketch = []

    for benchmark in benchmarks:
        
        b_name = benchmark[0]
        ins = benchmark[1]

        # create a list
        all_R = []
        all_R_list = []

        for e in ins:
            if "R" in e:
                all_R.append(e)
                all_R_list.append(ins[e][2])
            if e == "sketch":
                sketch = ins[e]
        
        p_sketches = []
        p_sketches_rank = []

        if "R0" in all_R:
            for r0 in all_R_list[0]:
                p_sketches.append(r0[1])
            benchmarks_p_sketch.append((b_name, p_sketches))
            continue
        
        

        for e in itertools.product(*all_R_list):
            
            sketch_tmp = sketch
            rank = 0
            for i in range(len(all_R)):
                
                R = all_R[i]
                if sketch_tmp.count(R) > 1:
                    sketch_tmp = sketch_tmp.replace(R, "sketch({},{})".format(e[i][1], R[1:]))
                else:    
                    sketch_tmp = sketch_tmp.replace(R, e[i][1])
                # sketch_tmp = sketch_tmp.replace(R, e[i][1])
                rank += e[i][0]
            
            p_sketches.append(sketch_tmp)
            p_sketches_rank.append(rank)

        # print(p_sketches_rank)

        # sort p_sketches according to p_sketches_rank
        p_sketches = [x for _,x in sorted(zip(p_sketches_rank, p_sketches))]
        benchmarks_p_sketch.append((b_name, p_sketches))

    return benchmarks_p_sketch


def read_sketch(args, benchmarks):
    
    for benchmark in benchmarks:
        
        b_name = benchmark[0]
        ins = benchmark[1]

        for e in ins:
            if "R" in e:
                try:
                    with open('{}/{}-{}'.format(args.sempre_sketch, b_name, e)) as f:
                        if e == "R0":
                            max_count = 25
                        else:
                            max_count = 5
                        
                        counter = 0
                        for row in f:
                            if counter >= max_count:
                                break
                            s = row.rstrip("\n").split(" ",1)
                            ins[e][2].append((int(s[0]),s[1]))
                            
                            counter += 1
                except FileNotFoundError:
                    ins[e][2].append((1, "?"))


def run_sempre(args):

    if not os.path.exists(args.sempre_sketch):
        os.system("mkdir -p {}".format(args.sempre_sketch))

    cwd = os.getcwd()
    
    sempre_path = 'sempre'
    os.chdir(sempre_path)

    subprocess.run(['python3', 'py_scripts/test.py', '--dataset', args.benchmark, '--model_dir', args.model_dir, '--topk', str(args.max_sketch)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    os.chdir(cwd)
    

def write_raw_file(args, benchmarks):

    with open(args.sempre_input_file, "w+") as  raw_input:
        raw_input.write("{}\t{}\t{}\n".format("#","NL","Sketch"))
        for (b_num, instance) in benchmarks:
            for key in instance:

                if key.startswith("R"):
                    raw_input.write("{}-{}\t{}\t{}\n".format(b_num, key, instance[key][0], instance[key][1]))

def read_input_fold(args, input_fold):

    """
    Read format: [(benchmark_name,{Sketch:"", R: (NL, GT, Predict)}]
    """

    benchmarks = []
    for _, _, files in os.walk(input_fold):
        for benchmark in files:
            if benchmark.startswith("."):
                continue
            with open('{}/{}'.format(input_fold,benchmark)) as fd:
                rd = csv.reader(fd)
                instance = {}
                for row in rd:
                    if row[0] == "Sketch":
                        instance['sketch'] = row[1]
                    if row[0].startswith("R"):
                        instance[row[0]] = (row[1],row[2],[])
                benchmarks.append((benchmark, instance))
        break
    
    return benchmarks

def run_so_format2(args):
    args.benchmark = "_tmp_so"
    args.sempre_input_file = "{}/{}.raw.txt".format(args.input_path, args.benchmark)    
    args.sempre_sketch = "{}/outputs/{}".format(args.sempre, args.benchmark)

    input_2_fold = "{}/nl-pldi".format(args.input_path)
    benchmarks = read_input_fold(args, input_2_fold)
    write_raw_file(args, benchmarks)
    run_sempre(args)
    read_sketch(args, benchmarks)
    benchmarks_p = combine_sketch(args, benchmarks)
    output_sketch(args, benchmarks_p)

    os.system("rm -r \"{}\"".format(args.sempre_sketch))

def run(args):
    run_sempre(args)
    os.system("cp -r \"{}\"/* \"{}\"".format(args.sempre_sketch, args.sketch_path))
    # os.system("rm -r \"{}\"".format(args.sempre_sketch))

def main():
    args = _parse_args()
    run(args)
    if args.benchmark == "so":
        run_so_format2(args)


if __name__ == "__main__":
    main()