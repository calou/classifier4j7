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

package net.sf.classifier4j7.worddatasource;

import net.sf.classifier4j7.model.WordProbability;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Nick Lothian
 * @author Peter Leschev
 *  
 */
public class SimpleWordsDataSource implements IWordsDataSource, Serializable {
    private static final int DEFAULT_MAP_SIZE = 32;

    private final Map<String, WordProbability> map;

    public SimpleWordsDataSource(int initialSize){
        map = new HashMap<>(initialSize, 0.6f);
    }

    public SimpleWordsDataSource(){
        this(DEFAULT_MAP_SIZE);
    }

    public final void setWordProbability(WordProbability wp) {
        map.put(wp.getWord(), wp);
    }

    /**
     * @see IWordsDataSource#getWordProbability(java.lang.String)
     */
    public WordProbability getWordProbability(String word) {
        return map.get(word);
    }

    public Collection<WordProbability> getAll() {
        return map.values();
    }

    @Override
    public void removeUnsignificantWordProbabilities() {
        List<String> unsignificantWords = new ArrayList<>();
        for(Map.Entry<String, WordProbability> entry : map.entrySet()){
            String key = entry.getKey();
            if(key.length() < 2){
                unsignificantWords.add(key);
                continue;
            }

            WordProbability wordProbability = entry.getValue();
            final double probability = wordProbability.getProbability();
            if(probability < 0.55 && probability > 0.45){
                unsignificantWords.add(key);
                continue;
            }

        }
        for(String str: unsignificantWords){
            map.remove(str);
        }
    }

    /**
     * @see IWordsDataSource#addMatch(java.lang.String)
     */
    public void addMatch(String word) {
        WordProbability wp = map.get(word);
        if (wp == null) {
            wp = new WordProbability(word, 1, 0);
        } else {
            wp.incrementMatchingCount();
        }
        setWordProbability(wp);
    }

    /**
     * @see IWordsDataSource#addNonMatch(java.lang.String)
     */
    public void addNonMatch(String word) {
        WordProbability wp = map.get(word);
        if (wp == null) {
            wp = new WordProbability(word, 0, 1);
        } else {
            wp.incrementNonMatchingCount();
        }
        setWordProbability(wp);
    }
}
