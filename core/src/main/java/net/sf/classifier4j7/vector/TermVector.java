
package net.sf.classifier4j7.vector;

import java.io.Serializable;


public class TermVector implements Serializable {
    private final String terms[];
    private final int values[];
    
    public TermVector(String[] terms, int[] values) {        
        this.terms = terms;
        this.values = values;
    }
    
    public String[] getTerms() {
        return terms.clone();
    }
    
    public int[] getValues() {
        return values.clone();
    }
    
    public String toString() {
        StringBuilder results = new StringBuilder("{");

        for (int i = 0; i < terms.length; i++) {
            results.append("[");
            results.append(terms[i]);
            results.append(", ");
            results.append(values[i]);
            results.append("] ");
        }
        results.append("}");
        
        return results.toString();
    }
}
