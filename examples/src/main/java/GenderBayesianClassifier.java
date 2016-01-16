import net.sf.classifier4J.DefaultTokenizer;
import net.sf.classifier4J.ICategorisedClassifier;
import net.sf.classifier4J.ITokenizer;
import net.sf.classifier4J.TokenizerMethod;
import net.sf.classifier4J.bayesian.BayesianClassifier;
import net.sf.classifier4J.bayesian.IWordsDataSource;
import net.sf.classifier4J.bayesian.SimpleWordsDataSource;
import net.sf.classifier4J.bayesian.WordsDataSourceException;

public class GenderBayesianClassifier {
    public static final double TRUE_THRESHOLD = 0.9;
    private BayesianClassifier maleClassifier;
    private BayesianClassifier femaleClassifier;
    private BayesianClassifier nonMatchingClassifier;

    public void train(Iterable<String> maleSamples, Iterable<String> femaleSamples, Iterable<String> nonMatchingSamples) throws GenderException {
        ITokenizer tokenizer = new DefaultTokenizer(TokenizerMethod.SPLIT_ON_WHITESPACE);
        IWordsDataSource maleWordsDataSource = new SimpleWordsDataSource();
        maleClassifier = new BayesianClassifier(maleWordsDataSource, tokenizer);
        maleClassifier.setCaseSensitive(false);

        IWordsDataSource femaleWordsDataSource = new SimpleWordsDataSource();
        femaleClassifier = new BayesianClassifier(femaleWordsDataSource, tokenizer);
        femaleClassifier.setCaseSensitive(false);

        IWordsDataSource nonMatchingWordsDataSource = new SimpleWordsDataSource();
        nonMatchingClassifier = new BayesianClassifier(nonMatchingWordsDataSource, tokenizer);
        nonMatchingClassifier.setCaseSensitive(false);

        try {
            for (String sample : maleSamples) {
                maleClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
                femaleClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
            for (String sample : femaleSamples) {
                maleClassifier.teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
                femaleClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
            for(String sample :nonMatchingSamples){
                nonMatchingClassifier.teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, sample);
            }
        } catch (WordsDataSourceException e){
            throw new GenderException(e);
        }
    }

    public Gender classify(final String text) throws WordsDataSourceException {
        if(nonMatchingClassifier.classify(ICategorisedClassifier.DEFAULT_CATEGORY, text) > TRUE_THRESHOLD){
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
