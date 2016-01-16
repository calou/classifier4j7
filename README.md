# classifier4j7: Bayesian and vector classifier
This project is a fork of [classifier4j](http://classifier4j.sourceforge.net/).

## Bayesian classifier
### Creating a classifier
    ITokenizer tokenizer = new DefaultTokenizer(TokenizerMethod.SPLIT_ON_WHITESPACE);
    IWordsDataSource wordsDataSource = new SimpleWordsDataSource();
    BayesianClassifier classifier = new BayesianClassifier(wordsDataSource, tokenizer);

### Training the classifier

    classifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, "Match");
    classifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, "Reject");
    
### Classify
    double score = classifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text);
or 

    boolean matching = classifier.isMatch(ICategorisedClassifier.DEFAULT_CATEGORY, text);
    