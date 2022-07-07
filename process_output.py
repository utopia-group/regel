import os, sys
import argparse


def _parse_args():
    parser = argparse.ArgumentParser(description="resnax_process_output")
    parser.add_argument("--log_folder", type=str, dest='log_path', default="log1")
    parser.add_argument("--log_path", type=str, dest="res_path", default="exp/so")
    parser.add_argument("--output_name", type=str, dest="output_name", default="results")

    args = parser.parse_args()
    return args


def main():
    args = _parse_args()
    print(args)
    args.java_path = os.getcwd()
    log_directory = "{}/{}/{}".format(args.java_path, args.res_path, args.log_path)
    output_directory = "{}/{}/{}/{}.csv".format(args.java_path, args.res_path, args.log_path, args.output_name)
    exclude = ["log"]
    with open(output_directory, "a") as results:
        # write legends
        results.write("name" + "," + "benchmark" + "," + "rank" + "," + "gt" + "," + "sketch" + "," + "result" + "," + "time" + "," + "matchGT" + "\n")
        for root, dirs, files in os.walk(log_directory, topdown=True): 
            dirs[:] = [d for d in dirs if d not in exclude]
            # print(dirs)
            for log in files:
                if "." not in log:
                    print(log)
                    name_split = log.split("-")
                    results.write(log + "," + name_split[0] + "," + name_split[1] + ",")
                    with open(log_directory + "/" + log) as fp:
                        gt = ""
                        sketch = ""
                        learned = ""
                        time = ""
                        matchGt = ""
                        line = fp.readline()
                        while line:
                            if "Sketch" in line:
                                idx = line.find(":")
                                sketch = (line[(idx + 1):len(line)]).strip()
                            elif "Learned program" in line:
                                idx = line.find(":")
                                learned = (line[(idx + 1):len(line)]).strip()
                            elif "GT program" in line:
                                idx = line.find(":")
                                gt = (line[(idx + 1):len(line)]).strip()
                            elif "Match gt" in line:
                                idx = line.find(":")
                                matchGt = (line[(idx + 1):len(line)]).strip()   
                            elif "Total time" in line:
                                idx = line.find(":")
                                time = (line[(idx + 1):len(line)]).strip()
                            line = fp.readline()
                        # print(sketch)
                        # print(learned)
                        # print(time)
                        results.write("\"" + gt + "\"" + "," + "\"" + sketch + "\"" + "," + "\"" + learned + "\"" + "," + time + "," + matchGt + "\n")


if __name__ == '__main__':
    main()
