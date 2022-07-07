ant regex

beam=$1
dataset=$2
grammar=$3
maxiter=$4

modeldir="models/b_"$beam"_ds_"$dataset
grammardir="demo/"$grammar

params=$modeldir/params

rm -rf $modeldir

java -Xmx32g -cp libregex/*:libsempre/*:lib/* -ea regex.Train -languageAnalyzer corenlp.CoreNLPAnalyzer -parser SketchParser -Grammar.inPaths $grammardir -SimpleLexicon.inPaths demo/popl.lexicon -Parser.beamSize $beam -Parser.dumpAllFeatures true -FeatureExtractor.featureDomains rule span context bigram phrase-category  -Dataset.inPaths train:regex/data/$dataset/regex.examples -Learner.batchSize 60 -Learner.maxTrainIters $maxiter  $modeldir
java -Xmx2g -cp libregex/*:libsempre/*:lib/* -ea regex.TestDemo -languageAnalyzer corenlp.CoreNLPAnalyzer -parser SketchParser -Grammar.inPaths $grammardir -SimpleLexicon.inPaths demo/popl.lexicon -FeatureExtractor.featureDomains rule span context bigram phrase-category -Builder.inParamsPath $params -Parser.beamSize $beam $dataset
