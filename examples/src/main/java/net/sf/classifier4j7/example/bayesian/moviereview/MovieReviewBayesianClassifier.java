package net.sf.classifier4j7.example.bayesian.moviereview;

import net.sf.classifier4j7.CommonsLangTokenizer;
import net.sf.classifier4j7.ICategorisedClassifier;
import net.sf.classifier4j7.NoStopWordProvider;
import net.sf.classifier4j7.bayesian.BayesianClassifier;
import net.sf.classifier4j7.tokenizer.ITokenizer;
import net.sf.classifier4j7.tokenizer.NGramTokenizer;
import net.sf.classifier4j7.worddatasource.SimpleWordsDataSource;
import net.sf.classifier4j7.worddatasource.WordsDataSourceException;

public class MovieReviewBayesianClassifier {
    public static final double TRUE_THRESHOLD = 0.7;
    private final BayesianClassifier positiveClassifier;
    private final BayesianClassifier negativeClassifier;

    public MovieReviewBayesianClassifier(BayesianClassifier positiveClassifier, BayesianClassifier negativeClassifier) {
        this.positiveClassifier = positiveClassifier;
        this.negativeClassifier = negativeClassifier;
    }

    public MovieReviewBayesianClassifier() {
        ITokenizer tokenizer = new NGramTokenizer(3, new CommonsLangTokenizer(), new NoStopWordProvider());
        this.positiveClassifier = new BayesianClassifier(new SimpleWordsDataSource(), tokenizer);
        this.negativeClassifier = new BayesianClassifier(new SimpleWordsDataSource(), tokenizer);
        positiveClassifier.setCaseSensitive(false);
        negativeClassifier.setCaseSensitive(false);
    }

    public void trainNegative(String sample) throws WordsDataSourceException {
        positiveClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
        negativeClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
    }

    public void trainPositive(String sample) throws WordsDataSourceException {
        positiveClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
        negativeClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
    }

    public void postTraining() {
        positiveClassifier.getWordsDataSource().removeUnsignificantWordProbabilities();
        negativeClassifier.getWordsDataSource().removeUnsignificantWordProbabilities();
    }

    public Sentiment classify(final String text) throws WordsDataSourceException {
        final double positiveScore = positiveClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text);
        if (positiveScore > TRUE_THRESHOLD) {
            return Sentiment.POSITIVE;
        } else {
            final double negativeScore = negativeClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text);
            return negativeScore > TRUE_THRESHOLD ? Sentiment.NEGATIVE : Sentiment.NEUTRAL;
        }
    }
}
