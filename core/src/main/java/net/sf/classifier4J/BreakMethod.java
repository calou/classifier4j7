package net.sf.classifier4J;


public enum BreakMethod {
    /**
     * Use a the "\W" (non-word characters) regexp to split the string passed to classify
     */
    WORD("\\W"),

    /**
     * Use a the "\s" (whitespace) regexp to split the string passed to classify
     */
    WHITESPACE("\\s");

    private String regexp;

    BreakMethod(String regexp){
        this.regexp = regexp;
    }

    public String getRegexp(){
        return regexp;
    }
}
