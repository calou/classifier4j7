package net.sf.classifier4j7.example.bayesian.gender;

import net.sf.classifier4j7.CommonsLangTokenizer;
import net.sf.classifier4j7.ICategorisedClassifier;
import net.sf.classifier4j7.bayesian.BayesianClassifier;
import net.sf.classifier4j7.worddatasource.SimpleWordsDataSource;
import net.sf.classifier4j7.worddatasource.WordsDataSourceException;
import net.sf.classifier4j7.tokenizer.ITokenizer;

public class GenderBayesianClassifier {
    public static final double TRUE_THRESHOLD = 0.9;
    private final BayesianClassifier maleClassifier;
    private final BayesianClassifier femaleClassifier;
    private final BayesianClassifier nonMatchingClassifier;

    public GenderBayesianClassifier(BayesianClassifier maleClassifier, BayesianClassifier femaleClassifier, BayesianClassifier nonMatchingClassifier) {
        this.maleClassifier = maleClassifier;
        this.femaleClassifier = femaleClassifier;
        this.nonMatchingClassifier = nonMatchingClassifier;
    }

    public GenderBayesianClassifier() {
        ITokenizer tokenizer = new CommonsLangTokenizer();
        this.maleClassifier = new BayesianClassifier(new SimpleWordsDataSource(), tokenizer);
        this.femaleClassifier = new BayesianClassifier(new SimpleWordsDataSource(), tokenizer);
        this.nonMatchingClassifier = new BayesianClassifier(new SimpleWordsDataSource(), tokenizer);
        maleClassifier.setCaseSensitive(false);
        femaleClassifier.setCaseSensitive(false);
        nonMatchingClassifier.setCaseSensitive(false);
    }

    public void train(Iterable<String> maleSamples, Iterable<String> femaleSamples, Iterable<String> nonMatchingSamples) throws GenderException {

        try {
            for (String sample : maleSamples) {
                maleClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
                femaleClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
            for (String sample : femaleSamples) {
                maleClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
                femaleClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
            for (String sample : nonMatchingSamples) {
                nonMatchingClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
        } catch (WordsDataSourceException e) {
            throw new GenderException(e);
        }
    }

    public Gender classify(final String text) throws WordsDataSourceException {
        if (nonMatchingClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text) > TRUE_THRESHOLD) {
            return Gender.UNDEFINED;
        }
        String trimmedText = text.trim();
        Gender gender = classifyText(trimmedText);
        if (gender.equals(Gender.UNDEFINED)) {
            if (trimmedText.contains(" ")) {
                String firstPart = trimmedText.substring(0, trimmedText.lastIndexOf(" "));
                return classifyText(firstPart);
            } else {
                return Gender.UNDEFINED;
            }
        } else {
            return gender;
        }
    }

    private Gender classifyText(String text) throws WordsDataSourceException {
        final double maleScore = maleClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text);
        if (maleScore > TRUE_THRESHOLD) {
            return Gender.MALE;
        } else {
            final double femaleScore = femaleClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text);
            if (femaleScore > TRUE_THRESHOLD) {
                return Gender.FEMALE;
            } else {
                return Gender.UNDEFINED;
            }
        }
    }
}
