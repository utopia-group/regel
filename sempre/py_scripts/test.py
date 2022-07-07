import os
import argparse
from os.path import join
from preprocess import preprocess
from preprocess import prepare_dataset
from postprocess import read_pred_file, make_sketch_file
def _parse_args():
    parser = argparse.ArgumentParser(description="so preprocess")
    parser.add_argument("--dataset", type=str, dest="dataset", default="so")
    parser.add_argument("--model_dir", type=str, dest="model", default="models/demo")

    parser.add_argument("--beam", type=int, dest="beam", default=200)
    parser.add_argument("--max_iter", type=int, dest="max_iter", default=5)
    parser.add_argument("--target", type=int, dest="target", default=-1)
    parser.add_argument("--topk", type=int, dest="topk", default=25)
    args = parser.parse_args()
    args.infile = "dataset/{}.raw.txt".format(args.dataset)
    args.outfile = "dataset/{}.ready.txt".format(args.dataset)
    args.grammar = "turk" if "deepregex" in args.dataset else "regex"
    return args

def test(args):
    cmd = "sh ./py_scripts/test.sh {0} {1} {2} {3}".format(args.dataset, '"' + args.model + '"', args.beam, args.grammar)
    print(cmd)
    os.system(cmd)

    prefix = os.path.join("./regex/data", args.dataset)
    pred_file = join(prefix, str(args.beam)+"-pred-test.txt")
    output_dir =  os.path.join("outputs/", args.dataset)
    make_sketch_file(pred_file, output_dir, args.dataset, args.topk)

def main():
    args = _parse_args()
    print(args)

    preprocess(args)
    prepare_dataset(args)
    test(args)

if __name__ == "__main__":
    main()