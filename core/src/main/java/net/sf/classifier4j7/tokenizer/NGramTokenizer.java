package net.sf.classifier4j7.tokenizer;


import net.sf.classifier4j7.stopword.DefaultStopWordsProvider;
import net.sf.classifier4j7.stopword.IStopWordProvider;

import java.util.*;

public class NGramTokenizer implements ITokenizer{
    private int ngramLength;
    private ITokenizer baseTokenizer;
    private IStopWordProvider stopWordProvider;

    public NGramTokenizer(int ngramLength) {
        this(ngramLength, new DefaultTokenizer(), new DefaultStopWordsProvider());
    }

    public NGramTokenizer(int ngramLength, ITokenizer baseTokenizer, IStopWordProvider stopWordProvider) {
        this.ngramLength = ngramLength;
        this.baseTokenizer = baseTokenizer;
        this.stopWordProvider = stopWordProvider;
    }

    @Override
    public String[] tokenize(String input) {
        Set<String> tokens = new HashSet<>();

        final String[] originalTokens = baseTokenizer.tokenize(input);
        List<String> selectedTokens = new ArrayList<>(originalTokens.length);
        for(String t: originalTokens){
            if(t.length() > 1 ) {
                selectedTokens.add(t);
            }
        }

        for(int i = 0; i < selectedTokens.size(); i++){
            final String str = selectedTokens.get(i);
            tokens.add(str);
            StringBuilder sb = new StringBuilder(str);
            for(int j = i+1; j < i + ngramLength; j++){
                if(j < selectedTokens.size()){
                    sb.append(" ").append(selectedTokens.get(j));
                    tokens.add(sb.toString());
                }
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }
}
