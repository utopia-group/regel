import os
import argparse
import numpy as np
from os.path import join
import shutil
import math

from preprocess import fit_for_exs
from postprocess import read_pred_file
# convert raw format to sth suitable for sempre


def _parse_args():
    parser = argparse.ArgumentParser(description="so preprocess")
    parser.add_argument("action", help="action")
    parser.add_argument("--exppath", type=str, dest="exppath", default="./exp")
    parser.add_argument("--infile", type=str, dest="infile", default="./demo/so.raw.txt")
    parser.add_argument("--outfile", type=str, dest="outfile", default="./demo/so.ready.txt")
    parser.add_argument("--dataset", type=str, dest="dataset", default="so")
    parser.add_argument("--seed", type=int, dest="seed", default=666)
    parser.add_argument("--nfold", type=int, dest="nfold", default=5)
    parser.add_argument("--beam", type=int, dest="beam", default=200)
    parser.add_argument("--max_iter", type=int, dest="max_iter", default=5)
    parser.add_argument("--target", type=int, dest="target", default=-1)

    
    args = parser.parse_args()
    return args

def read_ready_file(filename):
    with open(filename) as f:
        lines = f.readlines()
    lines = [x.strip() for x in lines]
    line_fields = [x.split("\t") for x in lines]
    d = []
    for fields in line_fields:
        d.append({"id": fields[0], "nl": fields[1], "sketch": fields[2]})
    return d

def makedir_f(dir):
    if os.path.exists(dir):
        shutil.rmtree(dir)
    os.mkdir(dir)

def get_exs_formula(ex):
    return '(example (utterance "{0}") (targetValue (name "{1}")))'.format(fit_for_exs(ex["nl"]), ex["sketch"])

def make_splits(args):
    exs = read_ready_file(args.outfile)
    for ex in exs:
        ex["formula"] = get_exs_formula(ex)

    num_exs = len(exs)
    perms = np.random.permutation(num_exs)
    num_left = num_exs
    print(perms)
    for fold_id in range(args.nfold):
        fold_prefix = os.path.join("./regex/data", args.dataset + "s" + str(fold_id))
        makedir_f(fold_prefix)

        num_this_fold = int(round(num_left * 1.0 / (args.nfold - fold_id)))
        print(num_this_fold)
        start_idx = num_exs - num_left
        num_left = num_left - num_this_fold
        end_idx = start_idx + num_this_fold
        if fold_id == args.nfold - 1:
            end_idx = num_exs
        test_idx = perms[start_idx:end_idx].tolist()
        train_idx = perms[:start_idx].tolist() + perms[end_idx:].tolist()
        print(test_idx)

        lines = []
        for i in train_idx:
            lines.append(exs[i]["formula"] + "\n")
        with open(join(fold_prefix, "regex.examples") , "w") as f:
            f.writelines(lines) 
        
        lines = []
        for i in test_idx:
            lines.append(exs[i]["id"] + "\t" + exs[i]["nl"] + "\n")
        with open(join(fold_prefix, "src-test.txt"), "w") as f:
            f.writelines(lines)

def exp(args):
    for fold_id in range(args.nfold):
        dataset_id = args.dataset + "s" + str(fold_id)

        cmd = "sh ./demo/exp.sh {0} {1} {2} {3}".format(args.beam, dataset_id, "popl.grammar", args.max_iter)
        print(cmd)
        os.system(cmd)
    
    preds = []
    for fold_id in range(args.nfold):
        fold_prefix = os.path.join("./regex/data", args.dataset + "s" + str(fold_id))
        fold_pred_file = join(fold_prefix, str(args.beam)+"-pred-test.txt")
        fold_preds = read_pred_file(fold_pred_file)
        print(len(fold_preds))
        preds.extend(fold_preds)

    preds.sort(key=lambda x: int(x["id"]))
    print(len(preds))
    # merge results
    prefix = join("./regex/data/", args.dataset)
    pred_file = join(prefix, str(args.beam)+"-pred-test.txt")
    with open(pred_file, "w") as f:
        for pred in preds:
            f.write(pred["id"] + "\t" +str(len(pred["derivations"])) + "\n")
            for deriv in pred["derivations"]:
                f.write(deriv + "\n")

def single(args):
    exs = read_ready_file(args.outfile)
    for ex in exs:
        ex["formula"] = get_exs_formula(ex)
    target = args.target
    num_exs = len(exs)
    perms = np.random.permutation(num_exs)
    print(perms)
    print("Target", target)
    dataset_id = args.dataset + "s" + str(target)

    cmd = "sh ./demo/exp_test.sh {0} {1} {2} {3}".format(args.beam, dataset_id, "popl.grammar", args.max_iter)
    print(cmd)
    os.system(cmd)
    
    preds = []
    for fold_id in range(args.nfold):
        fold_prefix = os.path.join("./regex/data", args.dataset + "s" + str(fold_id))
        fold_pred_file = join(fold_prefix, str(args.beam)+"-pred-test.txt")
        fold_preds = read_pred_file(fold_pred_file)
        print(len(fold_preds))
        preds.extend(fold_preds)

    preds.sort(key=lambda x: int(x["id"]))
    print(len(preds))
    # merge results
    prefix = join("./regex/data/", args.dataset)
    pred_file = join(prefix, str(args.beam)+"-pred-test.txt")
    with open(pred_file, "w") as f:
        for pred in preds:
            f.write(pred["id"] + "\t" +str(len(pred["derivations"])) + "\n")
            for deriv in pred["derivations"]:
                f.write(deriv + "\n")



def main():
    args = _parse_args()
    print(args)
    np.random.seed(args.seed)

    if args.action == "split":
        make_splits(args)
    
    if args.action == "exp":
        exp(args)

    if args.action == "single":
        single(args)

if __name__ == "__main__":
    main()
