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
package net.sf.classifier4J.demo;

import net.sf.classifier4J.*;
import net.sf.classifier4J.bayesian.BayesianClassifier;
import net.sf.classifier4J.bayesian.JDBMWordsDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class Analyser {

    static JDBMWordsDataSource wds;

    private static IClassifier setupClassifier(ITokenizer tokenizer) throws SQLException, IOException {
        wds = new JDBMWordsDataSource(Trainer.DB_DIRECTORY);
        wds.open();
        return new BayesianClassifier(wds, tokenizer);
    }

    /**
     * @returns Words Per Second
     */
    public static double useClassifier(ITokenizer tokenizer,
                                       IClassifier classifier,
                                       InputStream inputStream) throws IOException, ClassifierException {
        String contents = Utilities.getString(inputStream);
        int length = tokenizer.tokenize(contents).length;
        long startTime = System.currentTimeMillis();

        double matchProb = classifier.classify(contents) * 100;
        System.out.println("Match probability : " + matchProb +" %");

        long endTime = System.currentTimeMillis();
        double time = (double) (endTime - startTime) / (double) 1000;

        if (Double.compare(time, 0) == 0) {
            time = 1;
        }

        double wordsPerSecond = length / time;
        System.out.println("Processed " + wordsPerSecond + " words/s");
        return wordsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("This program reads in a single file and classifies it as a match or not.");
        System.out.println("It should be run after the Trainer program.");

        InputStream input = new FileInputStream("src/main/resources/analyser/french.txt");
        ITokenizer tokenizer = new DefaultTokenizer();
        IClassifier classifier = setupClassifier(tokenizer);

        useClassifier(tokenizer, classifier, input);

        wds.close();
    }
}
