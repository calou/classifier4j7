
package net.sf.classifier4j7.vector;


public interface TermVectorStorage {
    void addTermVector(String category, TermVector termVector);
    TermVector getTermVector(String category);
}
