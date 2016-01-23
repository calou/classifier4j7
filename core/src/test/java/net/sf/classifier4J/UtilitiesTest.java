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

import net.sf.classifier4J.tokenizer.DefaultTokenizer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class UtilitiesTest {

    private String sentence = "Hello there hello again and hello again.";

    @Test
    public void testGetWordFrequency() {
        // standard test
        Map result = Utilities.getWordFrequency(sentence);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get("hello"));
        assertEquals(3, result.get("hello"));
        //assertEquals(new Integer(1), result.get("there"));
        //assertEquals(new Integer(1), result.get("and"));
        assertEquals(2, result.get("again"));

        // test case sensitivity
        result = Utilities.getWordFrequency(sentence, true);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertNotNull(result.get("hello"));
        assertEquals(2, result.get("hello"));
        assertEquals(1, result.get("Hello"));
        //assertEquals(new Integer(1), result.get("there"));
        //assertEquals(new Integer(1), result.get("and"));
        assertEquals(2, result.get("again"));

        // test without a stop word provider
        result = Utilities.getWordFrequency(sentence, false, new DefaultTokenizer(), null);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertNotNull(result.get("hello"));
        assertEquals(3, result.get("hello"));
        assertEquals(1, result.get("there"));
        assertEquals(1, result.get("and"));
        assertEquals(2, result.get("again"));
    }

    @Test
    public void testGetUniqueWords() {
        String[] result = Utilities.getUniqueWords(null);
        assertNotNull(result);
        assertEquals(0, result.length);

        String[] input = {"one", "one", "one", "two", "three"};
        result = Utilities.getUniqueWords(input);
        assertThat(result).containsExactly("one", "three", "two");

        String[] words = new DefaultTokenizer().tokenize(sentence.toLowerCase());
        result = Utilities.getUniqueWords(words);
        assertEquals(4, result.length);
    }

    @Test
    public void testCountWords() {
        assertEquals(3, Utilities.countWords("word", new String[]{"word", "word", "word", "notword", "z", "a"}));
        assertEquals(3, Utilities.countWords("word", new String[]{"word", "word", "word"}));
        assertEquals(0, Utilities.countWords("word", new String[]{}));
        assertEquals(0, Utilities.countWords("word", new String[]{"notword", "z", "a"}));
    }

    @Test
    public void testGetSentences() {
        String[] result = Utilities.getSentences(null);
        assertNotNull(result);
        assertEquals(0, result.length);

        String sentence1 = "This is sentence one";
        String sentence2 = "This is sentence two";
        String someSentences = sentence1 + "... " + sentence2 + "..";
        result = Utilities.getSentences(someSentences);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(sentence1, result[0].trim());
        assertEquals(sentence2, result[1].trim());

        someSentences = sentence1 + "! " + sentence2 + ".";
        result = Utilities.getSentences(someSentences);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(sentence1, result[0].trim());
        assertEquals(sentence2, result[1].trim());

        someSentences = sentence1 + "? " + sentence2 + ".";
        result = Utilities.getSentences(someSentences);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(sentence1, result[0].trim());
        assertEquals(sentence2, result[1].trim());
    }

    @Test
    public void testGetString() throws Exception {
        assertEquals(sentence, Utilities.getString(
                new ByteArrayInputStream(
                        sentence.getBytes())));
    }
}
