import os
import argparse
import shutil
from os.path import join

# convert raw format to sth suitable for sempre


def _parse_args():
    parser = argparse.ArgumentParser(description="so preprocess")
    parser.add_argument("--infile", type=str, dest="infile", default="./demo/so.raw.txt")
    parser.add_argument("--outfile", type=str, dest="outfile", default="./demo/so.ready.txt")
    parser.add_argument("--exp_path", type=str, dest="exp_path", default="/Users/xiye/WorkSpace/DevSpace/resnax/exp/")
    parser.add_argument("--dataset", type=str, dest="dataset", default="so")
    parser.add_argument("--beam", type=int, dest="beam", default=500)
    parser.add_argument("--maxcnt", type=int, dest="maxcnt", default=25)

    args = parser.parse_args()
    args.infile = join("demo", "{}.raw.txt".format(args.dataset))
    args.outfile = join("demo", "{}.ready.txt".format(args.dataset))
    args.outpath = join(args.exp_path, "demo" + args.dataset)
    return args

def tricky_process_sketch(id, x, dataset):
    if dataset != "so":
        return x
    list_id = ["3", "52", "77"]
    if id not in list_id:
        return x
    src_string = 'concat(<e>,concat(<n>,concat(<u>,concat(<m>,concat(<c>,concat(<o>,concat(<n>,concat(<s>,concat(<t>,<s>)))))))))'  
    dst_strings = ['or(<&>,or(<|>,or(<.>,or(<-lrb->,<-rrb->))))', 'or(<_>,or(<->,or(<+>,or(<-lrb->,or(<-rrb->,or(</>,<\\\\>))))))', 'or(<~>,or(<!>,or(<@>,or(<#>,or(<$>,or(<->,<_>))))))']
    idx = list_id.index(id)
    x = x.replace(src_string, dst_strings[idx])
    return x

def process_sketch(x, dataset):
    if "turk" in dataset:
        y = x.replace("<!>", "<m0>")
        y = y.replace("<@>", "<m1>")
        y = y.replace("<#>", "<m2>")
        y = y.replace("<$>", "<m3>")
    else:
        y = x.replace("<space>", "< >")
        y = y.replace("<-lrb->", "<(>")
        y = y.replace("<-rrb->", "<)>")
        y = y.replace("\\\\", "\\")
        while y.find("upper") != -1:
            pos = y.find("upper")
            end_pos = pos + 5
            nex_tok = y[end_pos]
            y = y[:pos] + nex_tok.upper() + y[(end_pos + 1):]
    return y

def read_pred_file(filename):
    preds = []
    with open(filename) as f:
        while True:
            line = f.readline()
            if not line:
                break
            line = line.strip()
            line = line.split("\t")
            id = line[0]
            num_deriv = int(line[1])
            # print(id)
            derivs = []
            for i in range(num_deriv):
                line = f.readline().strip()
                derivs.append(line)
            preds.append({"id": id, "derivations": derivs})
    return preds

def write_derivs(filename, derivs):
    lines = [str(x[0]) + " " + x[1] + "\n" for x in derivs]
    with open(filename, "w") as f:
        f.writelines(lines)

def read_ready_data(filename):
    with open(filename) as f:
        lines = f.readlines()
    lines = [x.strip() for x in lines]
    line_fields = [x.split("\t") for x in lines]
    d = {}
    for fields in line_fields:
        d[fields[0]] = fields[2]
    return d

def eval_match(args):
    id_gt_map = read_ready_data(args.outfile)
    prefix = join("./regex/data/", args.dataset)
    pred_file = join(prefix, str(args.beam)+"-pred-test.txt")
    ids = []
    cnt_total = 0
    cnt_coverage = 0
    cnt_coverage_top = 0
    covered = set()
    covered_top = set()
    id_set = set()
    with open(pred_file) as f:
        while True:
            line = f.readline()
            if not line:
                break
            line = line.strip()
            line = line.split("\t")
            id = line[0]
            ids.append(id)
            num_deriv = int(line[1])
            derivs = []
            for i in range(num_deriv):
                line = f.readline().strip()
                line = line.split("\t")
                sketch = (line[0])
                rank = int(line[1]) + 1
                derivs.append((rank, sketch))
            derivs.sort(key=lambda x:x[0])
            derivs = [x[1] for x in derivs]

            cnt_total += 1
            flag = False
            flag_top = False
            id_set.add(int(id))   
            if id_gt_map[id] in derivs:
                flag = True
                cnt_coverage += 1
                covered.add(int(id))
            if id_gt_map[id] in derivs[:args.maxcnt]:
                cnt_coverage_top += 1
                flag_top = True
                covered_top.add(int(id))
            print(id, flag_top, flag)
    
    def format_func(x):
        x = list(x)
        x.sort()
        return x
    print("Data: {}, coverage: {}, top {} coverage: {}".format(cnt_total, cnt_coverage, args.maxcnt, cnt_coverage_top))
    print("Covered Top {}: {}".format(args.maxcnt, format_func(covered_top)))
    print("Covered: {}".format(format_func(covered)))
    print("Coverd but not in top: {}".format(format_func(covered - covered_top)))
    print("Uncovered: {}".format(format_func(id_set - covered)))


def make_sketch(args):
    prefix = join("./regex/data/", args.dataset)
    
    pred_file = join(prefix, str(args.beam)+"-pred-test.txt")
    ids = []
    with open(pred_file) as f:
        while True:
            line = f.readline()
            if not line:
                break
            line = line.strip()
            line = line.split("\t")
            id = line[0]
            ids.append(id)
            num_deriv = int(line[1])
            print(id)
            derivs = []
            for i in range(num_deriv):
                line = f.readline().strip()
                line = line.split("\t")
                sketch = tricky_process_sketch(id, line[0], args.dataset)
                sketch = process_sketch(sketch, args.dataset)
                rank = int(line[1]) + 1
                derivs.append((rank, sketch))
            derivs.sort(key=lambda x:x[0])
            derivs = derivs[:args.maxcnt]
            write_derivs(join(args.outpath, "sketch", id), derivs)
    return ids

def make_bemchmark(ids, args):

    # tricky
    if args.dataset == "so":
        src_prefix = join(args.exp_path, args.dataset, "benchmark")
        dst_prefix = join(args.output_path, args.dataset, "benchmark")
    else:
        return

    for id in ids:
        shutil.copyfile(join(src_prefix, id), join(dst_prefix, id))

def makedir_f(dir):
    if os.path.exists(dir):
        shutil.rmtree(dir)
    # os.mkdir(dir)
    os.system("mkdir -p \"{}\"".format(dir))

def postprecess(args):
    # clear 
    makedir_f(join(args.outpath, "benchmark"))
    makedir_f(join(args.outpath, "sketch"))
    ids = make_sketch(args)
    # make_bemchmark(ids, args)
    # eval_match(args)

def make_sketch_file(pred_file, sketch_dir, dataset, k):
    makedir_f(sketch_dir)
    with open(pred_file) as f:
        while True:
            line = f.readline()
            if not line:
                break
            line = line.strip()
            line = line.split("\t")
            id = line[0]
            num_deriv = int(line[1])
            derivs = []
            for i in range(num_deriv):
                line = f.readline().strip()
                line = line.split("\t")
                sketch = tricky_process_sketch(id, line[0], dataset)
                sketch = process_sketch(sketch, dataset)
                rank = int(line[1]) + 1
                derivs.append((rank, sketch))
            derivs.sort(key=lambda x:x[0])
            derivs = derivs[:k]
            write_derivs(join(sketch_dir, id), derivs)


def main():
    args = _parse_args()
    print(args)
    postprecess(args)

if __name__ == "__main__":
    main()
