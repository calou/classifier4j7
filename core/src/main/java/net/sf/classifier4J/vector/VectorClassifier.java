
package net.sf.classifier4J.vector;

import net.sf.classifier4J.*;
import net.sf.classifier4J.stopword.DefaultStopWordsProvider;
import net.sf.classifier4J.stopword.IStopWordProvider;
import net.sf.classifier4J.tokenizer.DefaultTokenizer;
import net.sf.classifier4J.tokenizer.ITokenizer;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class VectorClassifier extends AbstractCategorizedTrainableClassifier {
    public static double DEFAULT_VECTORCLASSIFIER_CUTOFF = 0.80d;

    private ITokenizer tokenizer;
    private IStopWordProvider stopWordsProvider;
    private TermVectorStorage storage;

    public VectorClassifier(TermVectorStorage storage) {
        tokenizer = new DefaultTokenizer();
        stopWordsProvider = new DefaultStopWordsProvider();
        this.storage = storage;
        setMatchCutoff(DEFAULT_VECTORCLASSIFIER_CUTOFF);
    }

    public VectorClassifier() {
        this(new HashMapTermVectorStorage());
    }
    
    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#classify(java.lang.String, java.lang.String)
     */
    public double classify(String category, String input) throws ClassifierException {
        // Create a map of the word frequency from the input
        Map<String, Integer> wordFrequencies = Utilities.getWordFrequency(input, false, tokenizer, stopWordsProvider);
        TermVector tv = storage.getTermVector(category);
        if (tv == null) {
            return 0;
        } else {
            int[] inputValues = generateTermValuesVector(tv.getTerms(), wordFrequencies);
            return VectorUtils.cosineOfVectors(inputValues, tv.getValues());
        }        
    }

    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#isMatch(java.lang.String, java.lang.String)
     */
    public boolean isMatch(String category, String input) throws ClassifierException {
        return (getMatchCutoff() < classify(category, input));
    }

    /**
     * @see net.sf.classifier4J.ITrainable#teachMatch(java.lang.String, java.lang.String)
     */
    public void teachMatch(String category, String input) throws ClassifierException {
        // Create a map of the word frequency from the input
        Map<String, Integer> wordFrequencies = Utilities.getWordFrequency(input, false, tokenizer, stopWordsProvider);
        
        // get the numTermsInVector most used words in the input
        int numTermsInVector = 25;
        Set<String> mostFrequentWords = Utilities.getMostFrequentWords(numTermsInVector, wordFrequencies);

        String[] terms = mostFrequentWords.toArray(new String[mostFrequentWords.size()]);
        Arrays.sort(terms);
        int[] values = generateTermValuesVector(terms, wordFrequencies);

        storage.addTermVector(category, new TermVector(terms, values));
    }

    /**
     * @param terms
     * @param wordFrequencies
     * @return
     */
    protected int[] generateTermValuesVector(String[] terms, Map<String, Integer> wordFrequencies) {
        int[] result = new int[terms.length];
        for (int i = 0; i < terms.length; i++) {
            Integer value = wordFrequencies.get(terms[i]);
            if (value == null) {
                result[i] = 0;
            } else {
                result[i] = value.intValue();
            }
            
        }        
        return result;
    }

    /**
     * @see net.sf.classifier4J.ITrainable#teachNonMatch(java.lang.String, java.lang.String)
     */
    public void teachNonMatch(String category, String input) throws ClassifierException {
        // this is not required for the VectorClassifier
    }
}
