
package net.sf.classifier4j7.vector;

import java.util.HashMap;
import java.util.Map;

public class HashMapTermVectorStorage implements TermVectorStorage {
    private final Map<String, TermVector> storage = new HashMap();

    /**
     * @see net.sf.classifier4j7.vector.TermVectorStorage#addTermVector(java.lang.String, net.sf.classifier4j7.vector.TermVector)
     */
    public void addTermVector(String category, TermVector termVector) {
        storage.put(category, termVector);        
    }

    /**
     * @see net.sf.classifier4j7.vector.TermVectorStorage#getTermVector(java.lang.String)
     */
    public TermVector getTermVector(String category) {
        return storage.get(category);
    }

}
