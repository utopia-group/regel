ant regex

dataset=$1
modeldir=$2
beam=$3
grammar="dataset/"$4".grammar"

params=$modeldir/params
java -Xmx2g -cp libregex/*:libsempre/*:lib/* -ea regex.TestDemo -languageAnalyzer corenlp.CoreNLPAnalyzer -parser SketchParser -Grammar.inPaths $grammar -SimpleLexicon.inPaths dataset/regex.lexicon -FeatureExtractor.featureDomains rule span context bigram phrase-category -Builder.inParamsPath $params -Parser.beamSize $beam $dataset