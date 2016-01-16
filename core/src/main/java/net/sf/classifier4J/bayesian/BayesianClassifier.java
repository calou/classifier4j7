/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote
 *    products derived from this software without prior written
 *    permission. For written permission, please contact
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called
 *    "Classifier4J", nor may "Classifier4J" appear in their names
 *    without prior written permission. For written permission, please
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package net.sf.classifier4J.bayesian;

import net.sf.classifier4J.*;
import net.sf.classifier4J.util.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>A implementation of {@link net.sf.classifier4J.IClassifier} based on Bayes'
 * theorem (see http://www.wikipedia.org/wiki/Bayes_theorem).</p>
 * <p/>
 * <p>The basic usage pattern for this class is:
 * <ol>
 * <li>Create a instance of {@link net.sf.classifier4J.bayesian.IWordsDataSource}</li>
 * <li>Create a new instance of BayesianClassifier, passing the IWordsDataSource
 * to the constructor</li>
 * <li>Call {@link net.sf.classifier4J.IClassifier#classify(java.lang.String) }
 * or {@link net.sf.classifier4J.IClassifier#isMatch(java.lang.String) }
 * </ol>
 * </p>
 * <p/>
 * <p>For example:<br>
 * <tt>
 * IWordsDataSource wds = new SimpleWordsDataSource();<br>
 * IClassifier classifier = new BayesianClassifier(wds);<br>
 * System.out.println( "Matches = " + classifier.classify("This is a sentence") );
 * </tt>
 * </p>
 *
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class BayesianClassifier extends AbstractCategorizedTrainableClassifier {

    private IWordsDataSource wordsData;
    private ITokenizer tokenizer;
    private IStopWordProvider stopWordProvider;

    private boolean isCaseSensitive = false;

    /**
     * Default constructor that uses the SimpleWordsDataSource & a DefaultTokenizer
     * (set to BREAK_ON_WORD_BREAKS).
     */
    public BayesianClassifier() {
        this(new SimpleWordsDataSource(), new DefaultTokenizer(TokenizerMethod.SPLIT_BY_WORD));
    }

    /**
     * Constructor for BayesianClassifier that specifies a datasource. The
     * DefaultTokenizer (set to BREAK_ON_WORD_BREAKS) will be used.
     *
     * @param wd a {@link net.sf.classifier4J.bayesian.IWordsDataSource}
     */
    public BayesianClassifier(IWordsDataSource wd) {
        this(wd, new DefaultTokenizer(TokenizerMethod.SPLIT_BY_WORD));
    }

    /**
     * Constructor for BayesianClassifier that specifies a datasource & tokenizer
     *
     * @param wd        a {@link net.sf.classifier4J.bayesian.IWordsDataSource}
     * @param tokenizer a {@link net.sf.classifier4J.ITokenizer}
     */
    public BayesianClassifier(IWordsDataSource wd, ITokenizer tokenizer) {
        this(wd, tokenizer, new DefaultStopWordsProvider());
    }

    /**
     * Constructor for BayesianClassifier that specifies a datasource, tokenizer
     * and stop words provider
     *
     * @param wd        a {@link net.sf.classifier4J.bayesian.IWordsDataSource}
     * @param tokenizer a {@link net.sf.classifier4J.ITokenizer}
     * @param swp       a {@link net.sf.classifier4J.IStopWordProvider}
     */
    public BayesianClassifier(IWordsDataSource wd, ITokenizer tokenizer, IStopWordProvider swp) {
        this.wordsData = wd;
        this.tokenizer = tokenizer;
        this.stopWordProvider = swp;
    }

    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#isMatch(java.lang.String, java.lang.String)
     */
    public boolean isMatch(String category, String input) throws WordsDataSourceException {
        return isMatch(category, tokenizer.tokenize(input));
    }

    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#classify(java.lang.String, java.lang.String)
     */
    public double classify(String category, String input) throws WordsDataSourceException {
        try {
            return classify(category, tokenizer.tokenize(input));
        } catch (NullPointerException e) {
            return throwIllegalArgumentException(category);
        }
    }

    private double throwIllegalArgumentException(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        } else {
            throw new IllegalArgumentException("input cannot be null");
        }
    }

    public void teachMatch(String category, String input) throws WordsDataSourceException {
        try {
            teachMatch(category, tokenizer.tokenize(input));
        } catch (NullPointerException e) {
            throwIllegalArgumentException(category);
        }
    }

    public void teachNonMatch(String category, String input) throws WordsDataSourceException {
        try {
            teachNonMatch(category, tokenizer.tokenize(input));
        } catch (NullPointerException e) {
            throwIllegalArgumentException(category);
        }
    }

    protected boolean isMatch(String category, String input[]) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        checkCategoriesSupported(category);
        double matchProbability = classify(category, input);
        return (matchProbability >= cutoff);
    }

    protected double classify(String category, String words[]) throws WordsDataSourceException {
        List<WordProbability> wps = calcWordsProbability(category, words);
        return normalizeSignificance(calculateOverallProbability(wps));
    }

    protected void teachMatch(String category, String words[]) throws WordsDataSourceException {
        boolean categorized = isCategorized();

        for (String word : words) {
            if (isClassifiableWord(word)) {
                if (categorized) {
                    ((ICategorisedWordsDataSource) wordsData).addMatch(category, transformWord(word));
                } else {
                    wordsData.addMatch(transformWord(word));
                }
            }
        }
    }

    protected void teachNonMatch(String category, String words[]) throws WordsDataSourceException {
        boolean categorized = isCategorized();

        for (String word : words) {
            if (isClassifiableWord(word)) {
                if (categorized) {
                    ((ICategorisedWordsDataSource) wordsData).addNonMatch(category, transformWord(word));
                } else {
                    wordsData.addNonMatch(transformWord(word));
                }
            }
        }
    }

    private boolean isCategorized() {
        return wordsData instanceof ICategorisedWordsDataSource;
    }

    /**
     * Allows transformations to be done to word.
     * This implementation transforms the word to lowercase if the classifier
     * is in case-insenstive mode.
     *
     * @param word
     * @return the transformed word
     * @throws IllegalArgumentException if a null is passed
     */
    protected String transformWord(String word) {
        if (word != null) {
            return isCaseSensitive ? word : word.toLowerCase();
        } else {
            throw new IllegalArgumentException("Null cannot be passed");
        }
    }

    /**
     * NOTE: Override this method with care. There is a good chance it will be removed
     * or have signature changes is later versions.
     * <p/>
     * <br />
     *
     * @todo need an option to only use the "X" most "important" words when calculating overall probability
     * "important" is defined as being most distant from NEUTAL_PROBABILITY
     */
    protected double calculateOverallProbability(List<WordProbability> wps) {
        if (wps == null || wps.isEmpty()) {
            return IClassifier.NEUTRAL_PROBABILITY;
        } else {
            // we need to calculate xy/(xy + z)
            // where z = (1-x)(1-y)

            // firstly, calculate z and xy
            double z = 0d;
            double xy = 0d;
            for (WordProbability wp : wps) {
                z = z == 0 ? (1 - wp.getProbability()) : z * (1 - wp.getProbability());
                xy = xy == 0 ? wp.getProbability() : xy * wp.getProbability();
            }
            final double numerator = xy;
            final double denominator = xy + z;
            return numerator / denominator;
        }
    }

    private List<WordProbability> calcWordsProbability(String category, String[] words) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannont be null");
        }
        boolean categorise = isCategorized();
        checkCategoriesSupported(category);
        if (words == null) {
            return Collections.emptyList();
        } else {
            List<WordProbability> wps = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                if (isClassifiableWord(words[i])) {
                    final WordProbability wp;
                    if (categorise) {
                        wp = ((ICategorisedWordsDataSource) wordsData).getWordProbability(category, transformWord(words[i]));
                    } else {
                        wp = wordsData.getWordProbability(transformWord(words[i]));
                    }
                    if (wp != null) {
                        wps.add(wp);
                    }
                }
            }
            return wps;
        }
    }

    private void checkCategoriesSupported(String category) {
        // if the category is not the default
        if (!ICategorisedClassifier.DEFAULT_CATEGORY.equals(category)) {
            // and the data source does not support categories
            if (!isCategorized()) {
                // throw an IllegalArgumentException
                throw new IllegalArgumentException("Word Data Source does not support non-default categories.");
            }
        }
    }

    private boolean isClassifiableWord(String word) {
        /*
        if (word == null || "".equals(word) || stopWordProvider.isStopWord(word)) {
            return false;
        } else {
            return true;
        }
        */
        return word != null && !"".equals(word) && !stopWordProvider.isStopWord(word);
    }

    protected static double normalizeSignificance(double sig) {
        if (Double.compare(IClassifier.UPPER_BOUND, sig) < 0) {
            return IClassifier.UPPER_BOUND;
        } else if (Double.compare(IClassifier.LOWER_BOUND, sig) > 0) {
            return IClassifier.LOWER_BOUND;
        } else {
            return sig;
        }
    }

    /**
     * @return true if the classifier is case sensitive, false otherwise
     * (false by default)
     */
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    /**
     * @param b True if the classifier should be case sensitive, false otherwise
     */
    public void setCaseSensitive(boolean b) {
        isCaseSensitive = b;
    }

    /**
     * @return the {@link net.sf.classifier4J.bayesian.IWordsDataSource} used
     * by this classifier
     */
    public IWordsDataSource getWordsDataSource() {
        return wordsData;
    }

    /**
     * @return the {@link net.sf.classifier4J.ITokenizer} used
     * by this classifier
     */
    public ITokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * @return the {@link net.sf.classifier4J.IStopWordProvider} used
     * by this classifier
     */
    public IStopWordProvider getStopWordProvider() {
        return stopWordProvider;
    }

    public String toString() {
        return new ToStringBuilder(this).append("IWordsDataSource", wordsData).append("ITokenizer", tokenizer).append("IStopWordProvider", stopWordProvider).toString();
    }

}
