package net.sf.classifier4j7;

public abstract class AbstractCategorizedTrainableClassifier extends AbstractClassifier implements ITrainableClassifier {

    /**
     * @see net.sf.classifier4j7.IClassifier#classify(java.lang.String)
     */
    @Override
    public double classify(String input) throws ClassifierException {
        return classify(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

    @Override
    public void teachMatch(String input) throws ClassifierException {
        teachMatch(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

    @Override
    public void teachNonMatch(String input) throws ClassifierException {
        teachNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, input);
    }

}
