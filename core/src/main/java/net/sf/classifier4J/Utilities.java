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

package net.sf.classifier4J;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class Utilities {

    public static Map<String, Integer> getWordFrequency(String input) {
        return getWordFrequency(input, false);
    }

    public static Map<String, Integer> getWordFrequency(String input, boolean caseSensitive) {
        return getWordFrequency(input, caseSensitive, new DefaultTokenizer(), new DefaultStopWordsProvider());
    }

    /**
     * Get a Map of words and Integer representing the number of each word
     * 
     * @param input The String to get the word frequency of
     * @param caseSensitive true if words should be treated as separate if they have different case
     * @param tokenizer a junit.framework.TestCase#run()
     * @param stopWordsProvider
     * @return
     */
    public static Map<String, Integer> getWordFrequency(String input, boolean caseSensitive, ITokenizer tokenizer, IStopWordProvider stopWordsProvider) {
        String convertedInput = caseSensitive ? input : input.toLowerCase();

        // tokenize into an array of words
        String[] words = tokenizer.tokenize(convertedInput);
        Arrays.sort(words);

        String[] uniqueWords = getUniqueWords(words);

        Map<String, Integer> result = new HashMap<>();
        for (String w : uniqueWords) {
            // no stop word provider, so add all words
            // add only words that are not stop words
            if (stopWordsProvider == null || (isWord(w) && !stopWordsProvider.isStopWord(w))) {
                result.put(w, countWords(w, words));
            }
        }
        return result;
    }

    private static String[] findWordsWithFrequency(Map<String, Integer> wordFrequencies, Integer frequency) {
        if (wordFrequencies == null || frequency == null) {
            return new String[0];
        } else {
            List<String> results = new ArrayList<>();
            Iterator it = wordFrequencies.keySet().iterator();

            while (it.hasNext()) {
                String word = (String) it.next();
                if (frequency.equals(wordFrequencies.get(word))) {
                    results.add(word);
                }
            }
            return results.toArray(new String[results.size()]);
        }
    }    
    
    public static Set<String> getMostFrequentWords(int count, Map<String, Integer> wordFrequencies) {
        Set<String> result = new LinkedHashSet();
        int freq = Collections.max(wordFrequencies.values());
        while (result.size() < count && freq > 0) {
            // this is very icky
            String words[] = findWordsWithFrequency(wordFrequencies, freq);
            result.addAll(Arrays.asList(words));
            freq--;
        }
        return result;
    }

    
    private static boolean isWord(String word) {
        return word != null && !word.trim().equals("");
    }

    /**
     * Find all unique words in an array of words
     * 
     * @param input an array of Strings
     * @return an array of all unique strings. Order is not guarenteed
     */
    public static String[] getUniqueWords(String[] input) {
        if (input == null) {
            return new String[0];
        } else {
            Set<String> result = new TreeSet<>();
            for (int i = 0; i < input.length; i++) {
                result.add(input[i]);
            }
            return result.toArray(new String[result.size()]);
        }
    }

    /**
     * Count how many times a word appears in an array of words
     * 
     * @param word The word to count
     * @param words non-null array of words 
     */
    public static int countWords(String word, String[] words) {
        // find the index of one of the items in the array.
        // From the JDK docs on binarySearch:
        // If the array contains multiple elements equal to the specified object, there is no guarantee which one will be found. 
        int itemIndex = Arrays.binarySearch(words, word);

        // iterate backwards until we find the first match
        while (itemIndex > 0 && words[itemIndex].equals(word)) {
            itemIndex--;
        }

        // now itemIndex is one item before the start of the words
        int count = 0;
        while (itemIndex < words.length && itemIndex >= 0) {
            if (words[itemIndex].equals(word)) {
                count++;
            }
            itemIndex++;
            if (itemIndex < words.length && !words[itemIndex].equals(word)) {
                break;
            }
        }
        return count;
    }

    /**
     * 
     * @param input a String which may contain many sentences
     * @return an array of Strings, each element containing a sentence
     */
    public static String[] getSentences(String input) {
        // split on a ".", a "!", a "?" followed by a space or EOL
        return input == null ? new String[0] : input.split("(\\.|!|\\?)+(\\s|\\z)");
    }

    /**
     * Given an inputStream, this method returns a String. New lines are 
     * replaced with " "
     */
    public static String getString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(" ");
        }
        reader.close();
        return sb.toString().trim();
    }
}
