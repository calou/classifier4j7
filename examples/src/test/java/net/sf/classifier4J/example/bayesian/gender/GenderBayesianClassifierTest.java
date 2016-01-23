package net.sf.classifier4J.example.bayesian.gender;

import net.sf.classifier4J.worddatasource.WordsDataSourceException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
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
        assertEquals(Gender.MALE, GENDER_CLASSIFIER.classify("JEAN-MARIE Leguen"));
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

    @Test
    public void classify_longText() throws Exception{
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris vitae nisi at sem facilisis semper ac in est.\n" +
                "Vivamus fermentum semper porta. Nunc diam velit, adipiscing ut tristique vitae, sagittis vel odio. Maecenas convallis ullamcorper ultricies. Curabitur ornare, ligula semper consectetur sagittis, nisi diam iaculis velit, id fringilla sem nunc vel mi. Nam dictum, odio nec pretium volutpat, arcu ante placerat erat, non tristique elit urna et turpis. Quisque mi metus, ornare sit amet fermentum et, tincidunt et orci. Fusce eget orci a orci congue vestibulum. Ut dolor diam, elementum et vestibulum eu, porttitor vel elit. Curabitur venenatis pulvinar tellus gravida ornare. Sed et erat faucibus nunc euismod ultricies ut id justo. Nullam cursus suscipit nisi, et ultrices justo sodales nec. Fusce venenatis facilisis lectus ac semper. Aliquam at massa ipsum. Quisque bibendum purus convallis nulla ultrices ultricies. Nullam aliquam, mi eu aliquam tincidunt, purus velit laoreet tortor, viverra pretium nisi quam vitae mi. Fusce vel volutpat elit. Nam sagittis nisi dui.\n" +
                "Suspendisse lectus leo, consectetur in tempor sit amet, placerat quis neque. Etiam luctus porttitor lorem, sed suscipit est rutrum non. Curabitur lobortis nisl a enim congue semper. Aenean commodo ultrices imperdiet. Vestibulum ut justo vel sapien venenatis tincidunt. Phasellus eget dolor sit amet ipsum dapibus condimentum vitae quis lectus. Aliquam ut massa in turpis dapibus convallis. Praesent elit lacus, vestibulum at malesuada et, ornare et est. Ut augue nunc, sodales ut euismod non, adipiscing vitae orci. Mauris ut placerat justo. Mauris in ultricies enim. Quisque nec est eleifend nulla ultrices egestas quis ut quam. Donec sollicitudin lectus a mauris pulvinar id aliquam urna cursus. Cras quis ligula sem, vel elementum mi. Phasellus non ullamcorper urna.";

        assertEquals(Gender.FEMALE, GENDER_CLASSIFIER.classify(longText));
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