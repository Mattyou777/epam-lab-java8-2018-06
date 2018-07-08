package streams.part2.example;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Example1 {

    @Test
    public void combineFullNamesOfFirstThreePersonsToStringUsingJava7() {
        List<Employee> source = getEmployees().subList(0, 3);

        StringBuilder builder = new StringBuilder();
        for (Employee employee : source) {
            builder.append(employee.getPerson().getFullName())
                   .append(", ");
        }
        builder.setLength(builder.length() - 2);

        assertEquals("Иван Мельников, Александр Дементьев, Дмитрий Осинов", builder.toString());
    }

    @Test
    public void combineFullNamesOfFirstThreePersonsToStringUsingStringJoin() {
        List<Employee> source = getEmployees().subList(0, 3);

        List<String> fullNames = new ArrayList<>();
        for (Employee employee : source) {
            fullNames.add(employee.getPerson().getFullName());
        }
        String result = String.join(", ", fullNames);

        assertEquals("Иван Мельников, Александр Дементьев, Дмитрий Осинов", result);
    }

    // ""
    // {Иван Мельников, Александр Дементьев, Дмитрий Осинов}
    // reduce (identity + op)
    // ,Иван Мельников
    // ,Иван Мельников, Александр Дементьев
    // ,Иван Мельников, Александр Дементьев, Дмитрий Осинов

    @Test
    public void reducePersonsToStringUsingStringConcatenation() {
        Stream<Employee> source = getEmployees().parallelStream();

        String result = source.limit(3)
                              .map(Employee::getPerson)
                              .map(Person::getFullName)
                              .reduce("", (left, right) -> left + ", " + right);

        assertEquals("Иван Мельников, Александр Дементьев, Дмитрий Осинов", result);
    }

    @Test
    public void collectPersonsToStringUsingStringBuilder() {
        Stream<Employee> source = getEmployees().stream();

        StringBuilder result = source.limit(3)
                              .map(Employee::getPerson)
                              .map(Person::getFullName)
                              .collect(StringBuilder::new,
                                      (builder, name) -> builder.append(name).append(", "),
                                       StringBuilder::append);

        assertEquals("Иван Мельников, Александр Дементьев, Дмитрий Осинов", result.toString());
    }

    @Test
    public void simpleStringJoinerWithDelimiter() {
        StringJoiner joiner = new StringJoiner("-");

        joiner.add("1").add("2").add("3");

        assertEquals("1-2-3", joiner.toString());
    }

    @Test
    public void stringJoinerWithPrefixDelimiterAndPostfix() {
        StringJoiner joiner = new StringJoiner("-", "[", "]");

        joiner.add("1").add("2").add("3");

        assertEquals("[1-2-3]", joiner.toString());
    }

    @Test
    public void mergeStringJoiners() {
        StringJoiner left = new StringJoiner("-", "[", "]");
        StringJoiner right = new StringJoiner("-", "[", "]");

        left.add("1").add("2");
        right.add("3").add("4");

        left.merge(right);

        StringJoiner last = new StringJoiner("-", "[", "]");
        left.merge(last);

        assertEquals("[1-2-3-4]", left.toString());
    }


    @Test
    public void collectPersonToStringUsingLambdasStringJoiner() {
        String result = IntStream.range(0, 10)
                                 .filter(value -> value < 5)
                                 .parallel()
                                 .mapToObj(String::valueOf)
                                 .collect(() -> new StringJoiner(", "), StringJoiner::add, StringJoiner::merge)
                                 .toString();

        assertEquals("0, 1, 2, 3, 4", result);
    }

    private static class IntCommaStringJoiner implements Collector<Integer, StringJoiner, String> {

        @Override
        public Supplier<StringJoiner> supplier() {
            return () -> new StringJoiner(", ");
        }

        @Override
        public BiConsumer<StringJoiner, Integer> accumulator() {
            return (joiner, value) -> joiner.add(String.valueOf(value));
        }

        @Override
        public BinaryOperator<StringJoiner> combiner() {
            return StringJoiner::merge;
        }

        @Override
        public Function<StringJoiner, String> finisher() {
            return StringJoiner::toString;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    @Test
    public void collectPersonToStringUsingCustomStringJoiner() {
        String result = IntStream.range(0, 1000)
                                 .boxed()
                                 .parallel()
                                 .collect(new IntCommaStringJoiner());

        assertEquals("0, 1, 2, 3, 4", result);
    }

    @Test
    public void collectPersonToStringUsingJoiningCollector() {
        String result = IntStream.range(0, 5)
                                 .boxed()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining(", "));

        assertEquals("0, 1, 2, 3, 4", result);
    }

    @Test
    public void collectPersonToStringUsingJoiningCollectorWithPrefixAndPostfix() {
        Stream<Employee> source = getEmployees().stream();

        String result = source.limit(3)
                              .map(Employee::getPerson)
                              .map(Person::getFullName)
                              .collect(Collectors.joining(", ", "[", "]"));

        assertEquals("[Иван Мельников, Александр Дементьев, Дмитрий Осинов]", result);
    }

    public static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }
}
