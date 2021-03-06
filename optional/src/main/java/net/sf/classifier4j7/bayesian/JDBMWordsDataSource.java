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

package net.sf.classifier4j7.bayesian;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.MRU;
import jdbm.helper.StringComparator;
import jdbm.recman.CacheRecordManager;
import net.sf.classifier4j7.ICategorisedClassifier;
import net.sf.classifier4j7.model.WordProbability;
import net.sf.classifier4j7.worddatasource.ICategorisedWordsDataSource;
import net.sf.classifier4j7.worddatasource.IWordsDataSource;

import java.io.IOException;
import java.util.Collection;

public class JDBMWordsDataSource implements ICategorisedWordsDataSource, AutoCloseable {
	private RecordManager recordManager = null;
	private BTree tree;

	String dir = ".";
	static String databaseName = "wordprobs";
	static String tableName = "wordprobabilities";

	public JDBMWordsDataSource() {
	}

	public JDBMWordsDataSource(String directory) {
		this.dir = directory;
	}

	@Override
	public void close() {
		if (recordManager != null) {
			try {
				recordManager.commit();
				recordManager.close();
			} catch (IOException e) {
				throw new RuntimeException("Error in JDBM datasource", e);
			}
		}
	}

	public void open() throws IOException {
		recordManager = RecordManagerFactory.createRecordManager(dir + "/" + databaseName);
		CacheRecordManager cacheRecordManager = new CacheRecordManager(recordManager, new MRU(100));

		long recid = recordManager.getNamedObject(tableName);
		if (recid != 0) {
			// already exists
			tree = BTree.load(cacheRecordManager, recid);
		} else {
			// does not exist
			tree = BTree.createInstance(cacheRecordManager, new StringComparator());
			recordManager.setNamedObject(tableName, tree.getRecid());
		}
	}

	/**
	 * @see IWordsDataSource#addMatch(java.lang.String)
	 */
	public void addMatch(String word) {
		addMatch(ICategorisedClassifier.DEFAULT_CATEGORY, word);
	}

	/**
	 * @see IWordsDataSource#addNonMatch(java.lang.String)
	 */
	public void addNonMatch(String word) {
		addNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, word);
	}

	@Override
	public Collection<WordProbability> getAll() {
		return null;
	}

	@Override
	public void removeUnsignificantWordProbabilities() {

	}

	/**
	 * @see ICategorisedWordsDataSource#addMatch(java.lang.String, java.lang.String)
	 */
	public void addMatch(String category, String word) {
		try {
			WordProbability wp = getWordProbability(category, word);
			if (wp == null) {
				wp = new WordProbability(word, 1, 0);
			} else {
				wp.setMatchingCount(wp.getMatchingCount() + 1);
			}
			tree.insert(getKey(category, word), wp, true);
		} catch (IOException e) {
			throw new RuntimeException("Error with JDBM datasource", e);
		}

	}

	/**
	 * @see ICategorisedWordsDataSource#addNonMatch(java.lang.String, java.lang.String)
	 */
	public void addNonMatch(String category, String word) {
		try {
			WordProbability wp = getWordProbability(category, word);
			if (wp == null) {
				wp = new WordProbability(word, 0, 1);
			} else {
				wp.setNonMatchingCount(wp.getNonMatchingCount() + 1);
			}
			tree.insert(getKey(category, word), wp, true);
		} catch (IOException e) {
			throw new RuntimeException("Error with JDBM datasource", e);
		}
	}

	/**
	 * @see IWordsDataSource#getWordProbability(java.lang.String)
	 */
	public WordProbability getWordProbability(String word) {
		return getWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word);
	}

	/**
	 * @see ICategorisedWordsDataSource#getWordProbability(java.lang.String, java.lang.String)
	 */
	public WordProbability getWordProbability(String category, String word) {
		try {
			return (WordProbability) tree.find(getKey(category,word));
		} catch (IOException e) {
			throw new RuntimeException("Error in JDBM datasource", e);
		}
	}

	/**
	 * 
	 * @param category The category, or null for the default
	 * @param word The word, cannot be null
	 * @return the key for the category and word. By default this is "category : word"
	 * @throws IllegalArgumentException if word is null
	 */
	protected String getKey(String category, String word) throws IllegalArgumentException {
		if (word == null) {
			throw new IllegalArgumentException("Word cannot be null");
		}
		StringBuffer result = new StringBuffer("");
		if (category == null) {
			result.append(ICategorisedClassifier.DEFAULT_CATEGORY);
		} else {
			result.append(category);
		}
		result.append(" : "); // space:space
		result.append(word);

		return result.toString();
	}
}
