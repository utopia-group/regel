ant regex

dataset=$1
modelname=$2
beam=$3
maxiter=$4
grammar="dataset/"$5".grammar"

# params=$modeldir/params
rm -rf $modelname

java -Xmx32g -cp libregex/*:libsempre/*:lib/* -ea regex.Train -languageAnalyzer corenlp.CoreNLPAnalyzer -parser SketchParser -Grammar.inPaths $grammar -SimpleLexicon.inPaths dataset/regex.lexicon -Parser.beamSize $beam -Parser.dumpAllFeatures true -FeatureExtractor.featureDomains rule span context bigram phrase-category  -Dataset.inPaths train:regex/data/$dataset/regex.examples -Learner.batchSize 100 -Learner.maxTrainIters $maxiter  $modelname
