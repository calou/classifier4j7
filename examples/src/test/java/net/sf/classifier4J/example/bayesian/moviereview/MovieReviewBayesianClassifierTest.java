package net.sf.classifier4J.example.bayesian.moviereview;

import net.sf.classifier4J.worddatasource.WordsDataSourceException;
import net.sf.classifier4J.example.bayesian.gender.GenderException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class MovieReviewBayesianClassifierTest {
    private static MovieReviewBayesianClassifier CLASSIFIER;

    @BeforeClass
    public static void setupClass() throws GenderException, IOException, WordsDataSourceException {
        CLASSIFIER = new MovieReviewBayesianClassifier();

        long start = System.currentTimeMillis();
        CLASSIFIER = Utils.buildClassifier();
        long duration = System.currentTimeMillis() - start;
        System.out.println("Learning phase duration : " + duration + "ms\n");
    }

    @Test
    public void classifyPositive() throws Exception {
        String[] names = {
                "This is a great movie",
                "I love this movie",
                "This movie is one of the best I've ever seen"
        };
        verifySentiment(names, Sentiment.POSITIVE);
    }

    @Test
    public void classifyNegative() throws Exception {
        String[] names = {
                "Bad movie",
                "Horrible movie",
                "I didn't like this movie",
                "This movie is one of the worst I've ever seen"
        };
        verifySentiment(names, Sentiment.NEGATIVE);
    }
    
    private void verifySentiment(String[] names, Sentiment sentiment) throws WordsDataSourceException {
        for (String name : names) {
            assertEquals(name + " is not recognized as " + sentiment.toString(), sentiment, CLASSIFIER.classify(name));
        }
    }
}