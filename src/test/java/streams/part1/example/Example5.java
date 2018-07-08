package streams.part1.example;

import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

public class Example5 {

    @Test
    public void test() {
        IntStream intStream = IntStream.of(2, 1, 3, 5, 7, 1, 2, 3);
        Stream<String> stringStream = intStream
                                               .filter(value -> value < 5)
                                               .peek(System.out::println)
                                               .distinct()
                                               .sorted() // 1 2 3
                                               .map(value -> value + 1)
                                               .mapToObj(String::valueOf);




        String collect = stringStream.collect(joining(" "));
    }
}
