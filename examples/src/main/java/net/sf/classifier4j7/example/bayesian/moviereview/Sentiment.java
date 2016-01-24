package net.sf.classifier4j7.example.bayesian.moviereview;

public enum Sentiment {
    POSITIVE("P"), NEGATIVE("N"), NEUTRAL(null);

    private String str;

    private Sentiment(String str){
        this.str = str;
    }
}
