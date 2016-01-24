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

import net.sf.classifier4j7.ICategorisedClassifier;
import net.sf.classifier4j7.model.WordProbability;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * <p>A JDBC based datasource. It requires a table of the following structure (tested in MySQL 4):
 * <p/>
 * <pre>
 * CREATE TABLE word_probability (
 * 	word			VARCHAR(255) NOT NULL,
 * 	category		VARCHAR(20) NOT NULL,
 * 	match_count		INT DEFAULT 0 NOT NULL,
 * 	nonmatch_count	INT DEFAULT 0 NOT NULL,
 * 	PRIMARY KEY(word, category)
 * )
 * </pre>
 * <p/>
 * </p>
 * <p>It will truncate any word longer than 255 characters to 255 characters</p>
 *
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class JDBCWordsDataSource implements ICategorisedWordsDataSource {
    private static final String UPDATE_MATCH_QUERY = "UPDATE word_probability SET match_count = match_count + 1 WHERE word = ? AND category = ?";
    private static final String UPDATE_NON_MATCH_QUERY = "UPDATE word_probability SET nonmatch_count = nonmatch_count + 1 WHERE word = ? AND category = ?";
    private static final String INSERT_QUERY = "INSERT INTO word_probability (word, category, match_count, nonmatch_count) VALUES (?, ?, ?, ?)";

    private DataSource dataSource;

    /**
     * Create a JDBCWordsDataSource using the DEFAULT_CATEGORY ("DEFAULT")
     *
     * @param ds The connection manager to use
     */
    public JDBCWordsDataSource(DataSource ds) throws WordsDataSourceException {
        this.dataSource = ds;
    }

    public WordProbability getWordProbability(String category, String word) throws WordsDataSourceException {
        int matchingCount = 0;
        int nonMatchingCount = 0;

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("SELECT match_count, nonmatch_count FROM word_probability WHERE word = ? AND category = ?");
            ps.setString(1, word);
            ps.setString(2, category);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                matchingCount = rs.getInt("match_count");
                nonMatchingCount = rs.getInt("nonmatch_count");
            }
            rs.close();
            return new WordProbability(word, matchingCount, nonMatchingCount);

        } catch (SQLException e) {
            throw new WordsDataSourceException("Problem obtaining WordProbability from database", e);
        }
    }

    public WordProbability getWordProbability(String word) throws WordsDataSourceException {
        return getWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word);
    }

    private void updateWordProbability(String category, String word, boolean isMatch) throws WordsDataSourceException {
        // truncate word at 255 characters
        if (word.length() > 255) {
            word = word.substring(0, 254);
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement selectStatement = conn.prepareStatement("SELECT 1 FROM word_probability WHERE word = ? AND category = ?");
            selectStatement.setString(1, word);
            selectStatement.setString(2, category);
            ResultSet rs = selectStatement.executeQuery();
            if (!rs.next()) {
                PreparedStatement insertStatement = conn.prepareStatement(INSERT_QUERY);
                // word is not in table
                // insert the word
                insertStatement.setString(1, word);
                insertStatement.setString(2, category);
                insertStatement.setInt(3, isMatch ? 1 : 0);
                insertStatement.setInt(4, isMatch ? 0 : 1);
                insertStatement.execute();
            } else {
                String query = isMatch ? UPDATE_MATCH_QUERY : UPDATE_NON_MATCH_QUERY;
                PreparedStatement updateStatement = conn.prepareStatement(query);
                // update the word count
                updateStatement.setString(1, word);
                updateStatement.setString(2, category);
                updateStatement.execute();
            }

        } catch (SQLException e) {
            throw new WordsDataSourceException("Problem updating WordProbability", e);
        }
    }

    public void addMatch(String category, String word) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        updateWordProbability(category, word, true);
    }

    public void addMatch(String word) throws WordsDataSourceException {
        updateWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word, true);
    }

    public void addNonMatch(String category, String word) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        updateWordProbability(category, word, false);
    }

    public void addNonMatch(String word) throws WordsDataSourceException {
        updateWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word, false);
    }

    @Override
    public Collection<WordProbability> getAll() {
        //TODO
        return null;
    }

    @Override
    public void removeUnsignificantWordProbabilities() {
        //TODO
    }
}
