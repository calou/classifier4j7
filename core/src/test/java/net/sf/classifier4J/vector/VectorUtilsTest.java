package net.sf.classifier4J.vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VectorUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testScalarProduct1() {
        VectorUtils.scalarProduct(new int[]{1, 2, 3}, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScalarProduct2() {
        VectorUtils.scalarProduct(null, new int[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScalarProduct3() {
        VectorUtils.scalarProduct(new int[]{1}, new int[]{1, 2, 3});
    }

    @Test
    public void testScalarProduct() {
        assertEquals(3, VectorUtils.scalarProduct(new int[]{1, 1, 1}, new int[]{1, 1, 1}));
        assertEquals(6, VectorUtils.scalarProduct(new int[]{1, 1, 1}, new int[]{1, 2, 3}));
        assertEquals(14, VectorUtils.scalarProduct(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        assertEquals(0, VectorUtils.scalarProduct(new int[]{0, 0, 0}, new int[]{1, 2, 3}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVectorLength() {
        VectorUtils.vectorLength(null);
    }

    @Test
    public void testScalarProduct4() {
        assertEquals(Math.sqrt(2), VectorUtils.vectorLength(new int[]{1, 1}), 0.001d);
        assertEquals(Math.sqrt(3), VectorUtils.vectorLength(new int[]{1, 1, 1}), 0.001d);
        assertEquals(Math.sqrt(12), VectorUtils.vectorLength(new int[]{2, 2, 2}), 0.001d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCosineOfVectors() {
        VectorUtils.cosineOfVectors(new int[]{1, 2, 3}, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCosineOfVectors2() {
        VectorUtils.cosineOfVectors(null, new int[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCosineOfVectors3() {
        VectorUtils.cosineOfVectors(new int[]{1}, new int[]{1, 2, 3});
    }

    @Test
    public void testCosineOfVectors4() {
        int[] one = new int[]{1, 1, 1};
        int[] two = new int[]{1, 1, 1};
        assertEquals(1d, VectorUtils.cosineOfVectors(one, two), 0.001);
    }
}