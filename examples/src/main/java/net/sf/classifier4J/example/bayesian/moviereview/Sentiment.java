package net.sf.classifier4J.example.bayesian.moviereview;

public enum Sentiment {
    POSITIVE("P"), NEGATIVE("N"), NEUTRAL(null);

    private String str;

    private Sentiment(String str){
        this.str = str;
    }
}
