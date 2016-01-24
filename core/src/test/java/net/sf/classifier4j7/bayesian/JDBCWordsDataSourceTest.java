package net.sf.classifier4j7.bayesian;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.classifier4j7.worddatasource.JDBCWordsDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class JDBCWordsDataSourceTest {
    private JDBCWordsDataSource dataSource;
    private DataSource jdbcDataSource;

    @Before
    public void setUp() throws Exception {
        final String url = "jdbc:h2:file:./target/wp;INIT=RUNSCRIPT FROM 'classpath:create_word_probability_table.sql'";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("sa");
        config.setPassword("");
        jdbcDataSource = new HikariDataSource(config);
        dataSource = new JDBCWordsDataSource(jdbcDataSource);

        // Removing all
        Connection conn = jdbcDataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM word_probability");
    }

    @Test
    public void addMatch_shouldInsert() throws Exception {
        dataSource.addMatch("cat", "word");
        verifyCount("cat", "word", 1, 0);
    }

    @Test
    public void addMatch_shouldInsertMultiple() throws Exception {
        dataSource.addMatch("cat", "word");
        dataSource.addMatch("cat", "word1");
        dataSource.addMatch("cat", "word1");
        verifyCount("cat", "word", 1, 0);
        verifyCount("cat", "word1", 2, 0);
    }

    @Test
    public void addNonMatch_shouldInsert() throws Exception {
        dataSource.addNonMatch("cat", "word");
        verifyCount("cat", "word", 0, 1);
    }


    @Test
    public void add_successive() throws Exception {
        dataSource.addNonMatch("cat", "word");
        dataSource.addNonMatch("cat", "word");
        dataSource.addMatch("cat", "word");
        dataSource.addNonMatch("cat", "word");
        verifyCount("cat", "word", 1, 3);
    }

    public void verifyCount(String category, String word, int matchCount, int nonMatchCount) throws SQLException {
        Connection connection = jdbcDataSource.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM word_probability where word='" + word + "' AND category='" + category + "'");

        rs.next();
        assertEquals(matchCount, rs.getInt("match_count"));
        assertEquals(nonMatchCount, rs.getInt("nonmatch_count"));
    }
}