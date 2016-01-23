package net.sf.classifier4J;

import net.sf.classifier4J.stopword.IStopWordProvider;

public class NoStopWordProvider implements IStopWordProvider {
    @Override
    public boolean isStopWord(String word) {
        return false;
    }

    @Override
    public String[] getStopWords() {
        return new String[0];
    }
}
