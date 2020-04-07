# REGEL: Regular Expression Generation from Examples and Langauage

This is the code repository for the paper ["Multi-modal Synthesis of Regular Expressions"](https://arxiv.org/abs/1908.03316).

## Prerequisites

Before runing the code for parsing your own language or reproducing experimental results, please first set up the [Sempre](https://github.com/percyliang/sempre) tool following the instructions below:

```shell
cd sempre
./pull-dependencies core
./pull-dependencies corenlp
./pull-dependencies freebase
./pull-dependencies tables
```

This repository also requires the following:

- [Z3](https://github.com/Z3Prover/z3). Make sure you have Z3 installed with the Java binding. 
- `ant` to compile the java files.
- `python3 ` 3.7
- `java` 1.8.0

## Benchmarks

### Existing Benchmarks

This repository includes two benchmarks domain: 

- StackOverflow (`$benchmark_domain = "so"`)
- DeepRegex (`$benchmark_domain = "deepregex"`).

The benchmarks (including natural language, examples and ground truth) are under `exp/$benchmark_domain/benchmark`.

We include the set of sketches we used under `exp/$benchmark_domain/sketch`

### Generate your own benchmarks

To generate your own benchmarks, you need to do the following:

#### Prepare a benchmark file

Create a new folder `exp/$your_benchmark_domain`

Inside `exp/$your_benchmark_domain/benchmark`, create benchmark files look like the following:

```
// natural language
# natural language description goes here

// examples
# write example in the format "$example_string$",$sign$ where sign can be
#	1) + to indicate it's a positive example
#	2) - to indicate it's a negative example

// gt
# ground truth in the dsl
```

The sample benchmarks can be found under the so and deepregex dataset.

#### Prepare a test/train set file for parser

The procedure to create such a file is under "Train Sketch Parser" -> "Prepare Train Set File". The procedure creating a test set file is same as the one creating a train set file except you fill the `Sketch` field with `null`.

## Sketch Generation

**Note:** if you only runs the `so` or `deepregex` benchmarks, you don't need to generate sketch unless you trained a new model. 

After having a set of benchmarks to generate sketch, run the following script:

```shell
python parse_benchmark.py --benchmark $your_benchmark_domain --model_dir $trained_model --max_sketch $number_of_sketch_generated_per_benchmark
```

The generated sketches will be put under `exp/$your_benchmark_domain/sketch`

##### `$trained_model`

We provided the pre-trained model for the two benchmarks datasets:

`so`: `pretrained_models/pretrained_so`

`deepregex`:`pretrained_models/pretrained_turk`

## Sketch Completion

To get the instantiations of the sketches that satisfy the given examples, invoke the following command:

```shell
python exp.py --benchmark $your_benchmark_domain --log_name $log_folder_name --sketch $sketch_folder_name --mem_max $max_memeory_allowed --synth_mode $synthesis_mode --processnum $number_of_process_allowed	--timeout $timeout_for_each_benchmark
```

##### `$synthesis_mode`

The synthesizer can be runned in the following mode:

`1`: enables all Regel Functionalities

`2`: enables Regel with pruning using over and under-approxmiation only

`4`: enumeration with no pruning techniques

`5`: run Regel with no sketches

##### `$number_of_process_allowed`

Regel tries to instantiate multiple sketches at parallel. Let `number_of_process_allowed = 1` if you wants to disable the parallel functionality. Otherwise, instantiate this parameter with an argument greater than `1`. The default value is `5`.

#### Output Processing

The script outputs to the `$log_folder_name` where a single file in the folder corresponds to the output of a single benchmark. To process the output in batch and generate a `csv` output, invoke the following script:

```shell
python process_output.py --log_folder $log_folder_name --log_path $path_to_log_folder --output_name $output_file_name
```

The output file will be inside the log folder as `$output_file_name.csv`

## Interactive Mode

We also provide a way to run Regel interactively (i.e. allowing users to interact with Regel by providing examples to refine the synthesis results).

### Run Interactive Mode with Benchmark Set

```shell
python interactive.py --run_mode 1 --benchmark $your_benchmark_domain --synth_mode $synthesis_mode --process_num $number_of_process_allowed --mem_max $max_memory_allowed --top $top_k_results_allowed --timeout $timeout_for_each_benchmark --max_iter $max_iter --save_history $save_history
```

##### `$top_k_results_allowed`

In interactive Regel, we only show user the first k finished sketch results. 

##### `$save_history`

The interactive Regel allows you to stop at any point working and continue from where you left last time. Set this to `True` if you wants to enable this functionality. 

##### `$max_iter`

The maximum of interaction allowed for each benchmark. 

#### Additional examples

For benchmark domains `so` and `deepregex`, we provide a set of additional examples to further refine the results automatically. These additional examples are stored in `interactive/$your_benchmark_domain/examples_cache`. All the furture addtional examples you entered will also stored inside this directory.

#### The workflow of the interactive script:

For each benchmark:

1. Regel reads the benchmarks and sketches and invoke the synthesizer
2. Rank the outputs and get the `$top_k_results_allowed` results
3. Check if any of the results returned matches the ground truth
4. If matches ground truth, the script automatically goes to the next benchmark
5. If does not match the ground truth, the script first find examples in the `examples_cache` that matches the synthesized regex but not the ground truth regex (this will be a negative example) or matches the ground truth regex but not the synthesized regex (this will be a positive example) and uses this example as the one to refine the synthesizer
6. If there does not exist a example in the `examples_cache` that matches the criteria, the script will ask the user to enter two additional examples and indicate whether they are positive and negative examples
7. Regel will run again using the updated examples

### Run Interactive Mode with Arbituary Natural Language and Examples ("Customize" Mode)

```shell
python interactive.py --run_mode 0 --synth_mode $synthesis_mode --process_num $number_of_process_allowed --mem_max $max_memory_allowed --top $top_k_results_allowed --timeout $timeout_for_each_benchmark --max_iter $max_iter --skecth_num $number_of_sketch_per_benchmark --save_history $save_history
```

#### The workflow of the interactive script (customize mode):

1. Enter a file name for your benchmark
2. Enter the natural language
3. Enter the examples
4. Indicating whether the example entered is a positive or negative example: use + to indicate it is a positive example, and - to indicate it is a negative example

The benchmark file created will be saved at `exp/customize/benchmark/$benchmark_file_name`

5. Regel will generate `$number_of_sketch_per_benchmark` number of sketches.

The sketch file created will be saved at `exp/customize/sketch/$benchmark_file_name`

6. Regel will run the synthesizer, and return either `$top_k_results_allowed` number of results or indicate it times out. 
7. Regel will ask you if there is any correct regex returned. Enter `y` if there is and enter the correct regex index.
8. If you enter `n` for the last question, Regel will ask you to enter two additional examples to disambiguate the regexes
9. Enter the examples in the same way as you enter the initial examples. After getting the additional examples, Regel will rerun the synthesizer and return the updated synthesis result. 

### Output

The output of execution is saved at `interactive/$your_benchmark_domain/logs/$synthesis_mode/raw_output.csv`.

If you uses `customize` mode, `$your_benchmark_domain = 'customize'`

## Train Sketch Parser

We have provided pretrained model that is ready to use in `sempre/pretrained`. We as well provide a examples procedure of how to train a model on **StackOverflow** dataset. 

**Prepare Train Set File**

We show a example train set file in `data/so.raw.txt`. It is a TSV file with three fields (`ID`, `NL`, and `Sketch`). Please prepare your train set file in this format, and put it in the `sempre/dataset/` directory with the name `*dataset-name*.raw.txt`.

**Train Parse Script**

To train a model, call

`python py_scripts/train.py *dataset-name* *model-dir*` 

E.g. `python py_scripts/train.py so models/so`

This command will preproces the training data (to fit the form required by `Sempre`. The processed data form will be stored in `regex/data/so`) and train a model that will be saved in the `*model-dir*` directory.

**Parse Script**

To parse a set of language descriptions using trained model, call

`python pyscripts/parse.py *dataset-name* *model-dir* *topk*`, where `*topk*` is the desired number of sketches.

The parsed sketches will be in `ouputs/*dataset-name*`, where each file contains the sketches for a single benchmark.



