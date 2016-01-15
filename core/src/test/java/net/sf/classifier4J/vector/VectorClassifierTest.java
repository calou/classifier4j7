
package net.sf.classifier4J.vector;

import net.sf.classifier4J.ClassifierException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;


public class VectorClassifierTest {

    private String sentence1 = "hello there is this a long sentence yes it is blah. blah hello";

    @Test
    public void testClassifyStringString() throws ClassifierException {
        TermVectorStorage storage = new HashMapTermVectorStorage();
        VectorClassifier vc = new VectorClassifier(storage);

        String category = "test";
        vc.teachMatch(category, sentence1);
        assertEquals(0.852d, vc.classify(category, "hello blah"), 0.001);
        assertEquals(0.301d, vc.classify(category, "sentence"), 0.001);
        assertEquals(0.0d, vc.classify(category, "bye"), 0.001);
        assertEquals(0.0d, vc.classify("does not exist", "bye"), 0.001);
    }

    @Test
    public void testIsMatchStringString() throws ClassifierException {
        TermVectorStorage storage = new HashMapTermVectorStorage();
        VectorClassifier vc = new VectorClassifier(storage);
        String category = "test";
        vc.teachMatch(category, sentence1);
        assertTrue(vc.isMatch(category, "hello blah"));
        assertFalse(vc.isMatch(category, "sentence"));
        assertFalse(vc.isMatch(category, "bye"));
    }

    @Test
    public void testTeachMatchStringString() throws ClassifierException {
        TermVectorStorage storage = new HashMapTermVectorStorage();
        VectorClassifier vc = new VectorClassifier(storage);
        String category = "test";
        vc.teachMatch(category, sentence1);
        TermVector tv = storage.getTermVector(category);
        assertThat(tv.getTerms()).containsExactly("blah", "hello", "long", "sentence", "yes");
        assertThat(tv.getValues()).containsExactly(2, 2, 1, 1, 1);

        assertEquals("{[blah, 2] [hello, 2] [long, 1] [sentence, 1] [yes, 1] }", tv.toString());
    }
}
