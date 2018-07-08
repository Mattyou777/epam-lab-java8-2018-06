package streams.part1.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.stream.Stream;

public class Example6 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Path path = Paths.get(Example6.class.getResource("example6_data.txt").toURI());
        try (Stream<String> source = Files.lines(path)) {
            IntSummaryStatistics result = source.flatMap(line -> Arrays.stream(line.split("\\s+")))
                                                .map(word -> word.replaceAll("[^а-яёА-ЯЁ]+", ""))
                                                .map(String::toLowerCase)
                                                .distinct()
                                                .mapToInt(String::length)
                                                .summaryStatistics();

            System.out.println(result.getCount());
            System.out.println(result.getAverage());
        }
    }



}
