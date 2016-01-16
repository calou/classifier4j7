package net.sf.classifier4J.example.bayesian.gender;

import net.sf.classifier4J.bayesian.WordsDataSourceException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class GenderBayesianClassifierTest {
    private static GenderBayesianClassifier GENDER_CLASSIFIER;

    @BeforeClass
    public static void setupClass() throws GenderException {
        GENDER_CLASSIFIER = new GenderBayesianClassifier();
        List<String> maleSamples = getLinesFromFile("gender/male.txt");
        List<String> femaleSamples = getLinesFromFile("gender/female.txt");
        List<String> nonMatchingSamples = getLinesFromFile("gender/non-matchings.txt");

        long start = System.currentTimeMillis();
        GENDER_CLASSIFIER.train(maleSamples, femaleSamples, nonMatchingSamples);
        long duration = System.currentTimeMillis() - start;
        System.out.println("Learning phase duration : " + duration + "ms\n");
    }

    private static List<String> getLinesFromFile(String filename) throws GenderException {
        List<String> samples = new ArrayList<>();
        try{
            Path path = Paths.get(GenderBayesianClassifierTest.class.getClassLoader().getResource(filename).toURI());
            try (BufferedReader br = Files.newBufferedReader(path, Charset.defaultCharset())) {
                String line;
                while ((line = br.readLine()) != null) {
                    samples.add(line);
                }
            } catch (IOException e) {
                throw new GenderException(e);
            }
        }catch (URISyntaxException e){
            throw new GenderException(e);
        }
        return samples;
    }

    @Test
    public void classify_shouldReturnUndefinedForTextContainingNonMatchings() throws WordsDataSourceException {
        assertEquals(Gender.MALE, GENDER_CLASSIFIER.classify("Luke Skywalker"));
        assertEquals(Gender.UNDEFINED, GENDER_CLASSIFIER.classify("Luke Skywalker company"));
    }

    @Test
    public void classify_withComposedMixedName() throws Exception {
        assertEquals(Gender.MALE, GENDER_CLASSIFIER.classify("Jean-Marie Leguen"));
        assertEquals(Gender.FEMALE, GENDER_CLASSIFIER.classify("Marie-Georges Buffet"));
        assertEquals(Gender.FEMALE, GENDER_CLASSIFIER.classify("Marie-Georges Laurent"));
    }

    @Test
    public void classifyMale() throws Exception {
        String[] names = {
                "John McEnroe", "Sébastien Lemoine", "Ludovic Lemoine", "Incredible Tom", "Ugly Bob"
            };
        verifyGender(names, Gender.MALE);
    }

    @Test
    public void classifyFemale() throws Exception {
        String[] names = {"Audrey Lemoine", "Audrey Laurent", "Corinne Laurent"};
        verifyGender(names, Gender.FEMALE);
    }

    @Test
    public void classifyUndefined() throws Exception {
        String[] names = {"nimp", "Big looser"};
        verifyGender(names, Gender.UNDEFINED);
    }

    @Test
    public void classifyLongUndecisiveFemale() throws Exception {
        String[] names = {
                "Audrey Marc Lucy Patrick Jeanne Etienne Patricia François Sara"
        };
        verifyGender(names, Gender.FEMALE);
    }

    private void verifyGender(String[] names, Gender gender) throws WordsDataSourceException {
        long start = System.nanoTime();
        for (String name : names) {
            assertEquals(name + " is not recognized as " + gender.toString(), gender, GENDER_CLASSIFIER.classify(name));
        }
        long durationInNanoSecond = System.nanoTime() - start;
        final double duration = (durationInNanoSecond / names.length) / 1000000.0;
        System.out.println("Duration for gender "+gender.toString().toLowerCase()+": " + duration + " ms");
    }
}