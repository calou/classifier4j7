package net.sf.classifier4j7.tokenizer;

public enum TokenizerMethod {
    /**
     * Use a the "\W" (non-word characters) regexp to split the string passed to classify
     */
    SPLIT_BY_WORD("\\W"),

    /**
     * Use a the "\s" (whitespace) regexp to split the string passed to classify
     */
    SPLIT_ON_WHITESPACE("\\s");

    private String regexp;

    TokenizerMethod(String regexp){
        this.regexp = regexp;
    }

    public String getRegexp(){
        return regexp;
    }
}
