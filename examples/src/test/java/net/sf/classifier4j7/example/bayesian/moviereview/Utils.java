package net.sf.classifier4j7.example.bayesian.moviereview;

import net.sf.classifier4j7.worddatasource.WordsDataSourceException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Utils {
    private static MovieReviewBayesianClassifier classifier;

    static String readFromFile(Path path) throws IOException {
        String str = new String(Files.readAllBytes(path), Charset.defaultCharset());
        return str;
    }

    public static MovieReviewBayesianClassifier buildClassifier() throws IOException, WordsDataSourceException {
        if (classifier == null) {
            classifier = new MovieReviewBayesianClassifier();
            Path positiveReviewPath = Paths.get("src/test/resources/nltk_data/corpora/movie_reviews/pos");
            for (Path path : Files.newDirectoryStream(positiveReviewPath)) {
                classifier.trainPositive(readFromFile(path));
            }
            Path negativeReviewPath = Paths.get("src/test/resources/nltk_data/corpora/movie_reviews/neg");
            for (Path path : Files.newDirectoryStream(negativeReviewPath)) {
                classifier.trainNegative(readFromFile(path));
            }
            classifier.postTraining();
        }
        return classifier;
    }

}
