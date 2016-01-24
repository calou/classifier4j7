package net.sf.classifier4j7.tokenizer;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NGramTokenizerTest {
    private ITokenizer tokenizer;

    @Before
    public void setUp(){
        tokenizer = new NGramTokenizer(3);
    }

    @Test
    public void tokenize(){
        String sentence ="a1 a2 a3 a4 a5 a6";
        String[] tokens = tokenizer.tokenize(sentence);
        assertThat(tokens).hasSize(15);
        assertThat(tokens).contains("a1", "a2", "a3", "a4", "a5", "a6");
        assertThat(tokens).contains("a1 a2", "a2 a3", "a3 a4", "a4 a5", "a5 a6");
        assertThat(tokens).contains("a1 a2 a3", "a2 a3 a4", "a3 a4 a5", "a4 a5 a6");
    }
}