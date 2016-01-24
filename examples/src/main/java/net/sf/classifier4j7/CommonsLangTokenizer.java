package net.sf.classifier4j7;

import net.sf.classifier4j7.tokenizer.ITokenizer;
import org.apache.commons.lang3.text.StrTokenizer;

public class CommonsLangTokenizer implements ITokenizer {
    @Override
    public String[] tokenize(String input) {
        StrTokenizer tokenizer = new StrTokenizer(input);
        return tokenizer.getTokenArray();
    }
}
