package net.sf.classifier4J.example.bayesian.gender;

import net.sf.classifier4J.bayesian.WordsDataSourceException;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GenderBenchmarkTest {

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(2)
                .threads(5)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class BenchmarkState {
        public GenderBayesianClassifier classifier;

        @Setup(Level.Trial)
        public void initialize() throws GenderException {
            classifier = new GenderBayesianClassifier();
            List<String> maleSamples = getLinesFromFile("gender/male.txt");
            List<String> femaleSamples = getLinesFromFile("gender/female.txt");
            List<String> nonMatchingSamples = getLinesFromFile("gender/non-matchings.txt");
            classifier.train(maleSamples, femaleSamples, nonMatchingSamples);
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
    }

    @Benchmark
    public void classify(BenchmarkState state, Blackhole bh) {
        try {
            bh.consume(state.classifier.classify("Etienne Leroux"));
        } catch (WordsDataSourceException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void classify_longText(BenchmarkState state, Blackhole bh) {
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris vitae nisi at sem facilisis semper ac in est.\n" +
                "Vivamus fermentum semper porta. Nunc diam velit, adipiscing ut tristique vitae, sagittis vel odio. Maecenas convallis ullamcorper ultricies. Curabitur ornare, ligula semper consectetur sagittis, nisi diam iaculis velit, id fringilla sem nunc vel mi. Nam dictum, odio nec pretium volutpat, arcu ante placerat erat, non tristique elit urna et turpis. Quisque mi metus, ornare sit amet fermentum et, tincidunt et orci. Fusce eget orci a orci congue vestibulum. Ut dolor diam, elementum et vestibulum eu, porttitor vel elit. Curabitur venenatis pulvinar tellus gravida ornare. Sed et erat faucibus nunc euismod ultricies ut id justo. Nullam cursus suscipit nisi, et ultrices justo sodales nec. Fusce venenatis facilisis lectus ac semper. Aliquam at massa ipsum. Quisque bibendum purus convallis nulla ultrices ultricies. Nullam aliquam, mi eu aliquam tincidunt, purus velit laoreet tortor, viverra pretium nisi quam vitae mi. Fusce vel volutpat elit. Nam sagittis nisi dui.\n" +
                "Suspendisse lectus leo, consectetur in tempor sit amet, placerat quis neque. Etiam luctus porttitor lorem, sed suscipit est rutrum non. Curabitur lobortis nisl a enim congue semper. Aenean commodo ultrices imperdiet. Vestibulum ut justo vel sapien venenatis tincidunt. Phasellus eget dolor sit amet ipsum dapibus condimentum vitae quis lectus. Aliquam ut massa in turpis dapibus convallis. Praesent elit lacus, vestibulum at malesuada et, ornare et est. Ut augue nunc, sodales ut euismod non, adipiscing vitae orci. Mauris ut placerat justo. Mauris in ultricies enim. Quisque nec est eleifend nulla ultrices egestas quis ut quam. Donec sollicitudin lectus a mauris pulvinar id aliquam urna cursus. Cras quis ligula sem, vel elementum mi. Phasellus non ullamcorper urna.";

        try {
            bh.consume(state.classifier.classify(longText));
        } catch (WordsDataSourceException e) {
            e.printStackTrace();
        }
    }
}
