
package net.sf.classifier4J.vector;


public interface TermVectorStorage {
    void addTermVector(String category, TermVector termVector);
    TermVector getTermVector(String category);
}
