import os
from os.path import join
import subprocess

def read_sketch_file(filename):
    with open(filename) as f:
        lines = f.readlines()
        sketches = [x.rstrip().split(' ', 1)[1] for x in lines]
    return sketches
# desps: list of natural language description
# model_dir, model_file: relative path
# k: num desired
def parse_descriptions(desps, model_dir, k):
    # tempory_dataset_path
    cwd = os.getcwd()

    # make a temp dataset file
    tmp = '_tmp'
    tmp_raw_file = join('sempre/dataset', '{}.raw.txt'.format(tmp))

    with open(tmp_raw_file, 'w') as f:
        f.write('#\tNL\tsketch\n')
        for i, d in enumerate(desps):
            f.write('{}\t{}\tnull\n'.format(i, d))

    sempre_path = 'sempre'
    os.chdir(sempre_path)
    
    sketch_dir = join('outputs', tmp)
    if not os.path.exists(sketch_dir):
        os.system("mkdir -p {}".format(sketch_dir))

    subprocess.run(['python3', 'py_scripts/test.py', '--dataset', '_tmp', '--model_dir', model_dir, '--topk', str(k)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    os.chdir(cwd)

    sketch_dir = join('sempre/outputs', tmp)
    sketches = []
    for i in range(len(desps)):
        fname = join(sketch_dir, str(i))
        sketches.append(read_sketch_file(fname))

    return sketches


if __name__ == "__main__":
    print(parse_descriptions(['regular expression that allow a "A" after at least 3 digits'], 'pretrained_models/pretrained_so', 25))