package net.sf.classifier4J;

public abstract class AbstractCategorizedTrainableClassifier extends AbstractClassifier implements ITrainableClassifier {

    /**
     * @see net.sf.classifier4J.IClassifier#classify(java.lang.String)
     */
    public double classify(String input) throws ClassifierException {
        return classify(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

    public void teachMatch(String input) throws ClassifierException {
        teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

    public void teachNonMatch(String input) throws ClassifierException {
        teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

}
