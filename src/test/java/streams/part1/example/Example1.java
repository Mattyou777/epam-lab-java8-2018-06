package streams.part1.example;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

/**
 * @see <a href="https://habrahabr.ru/company/luxoft/blog/270383">Шпаргалка по Stream-API</a>
 */
@SuppressWarnings({"RedundantStreamOptionalCall", "ResultOfMethodCallIgnored", "unused"})
public class Example1 {

    @Test
    public void operationsOnStreamExample() {
        Stream<Employee> stream1 = getEmployees().stream();
        Stream<Employee> stream2 = stream1.filter(employee -> "Иван".equals(employee.getPerson().getFirstName()));
        Stream<List<JobHistoryEntry>> stream3 = stream2.map(Employee::getJobHistory);
        Stream<JobHistoryEntry> stream4 = stream3.flatMap(Collection::stream);
        Stream<JobHistoryEntry> stream5 = stream4.peek(System.out::println);
        Stream<JobHistoryEntry> stream6 = stream5.distinct();
        Stream<JobHistoryEntry> stream7 = stream6.sorted(Comparator.comparingInt(JobHistoryEntry::getDuration));
        Stream<JobHistoryEntry> stream8 = stream7.parallel();
        Stream<JobHistoryEntry> stream9 = stream8.sequential();
        Stream<JobHistoryEntry> stream10 = stream9.unordered();
        Stream<JobHistoryEntry> stream11 = stream10.skip(1);
        Stream<JobHistoryEntry> stream12 = stream11.limit(10);

        long count = stream12.count();
        Optional<JobHistoryEntry> any = stream12.findAny();
        Optional<JobHistoryEntry> first = stream12.findFirst();
        boolean allDev = stream12.allMatch(entry -> "dev".equals(entry.getPosition()));
        boolean anyQa = stream12.anyMatch(entry -> "qa".equals(entry.getPosition()));
        boolean none = stream12.noneMatch(entry -> "qa".equals(entry.getPosition()));
        stream12.forEach(System.out::println);
        stream12.forEachOrdered(System.out::println);
        Optional<JobHistoryEntry> max = stream12.max(Comparator.comparingInt(JobHistoryEntry::getDuration));
        Optional<JobHistoryEntry> min = stream12.min(Comparator.comparingInt(JobHistoryEntry::getDuration));
        Object[] objects = stream12.toArray();
        JobHistoryEntry[] jobHistoryEntries = stream12.toArray(JobHistoryEntry[]::new);
        Iterator<JobHistoryEntry> iterator = stream12.iterator();
        stream12.reduce(null);
        stream12.collect(null);
    }

    /**
     *            filter map flatMap peek distinct unordered sorted skip limit sequential parallel
     * IMMUTABLE        |   |       |    |        |         |      |    |     |          |
     * CONCURRENT       |   |       |    |        |         |      |    |     |          |
     * DISTINCT         | - |   -   |    |   +    |         |      |    |     |          |
     * NONNULL          | - |   -   |    |        |         |      |    |     |          |
     * ORDERED          |   |       |    |        |    -    |   +  |    |     |          |
     * SORTED           | - |   -   |    |        |         |      |    |     |          |
     * SIZED        -   |   |   -   |    |   -    |         |      |    |     |          |
     * SUBSIZED         |   |       |    |        |         |      |    |     |          |
     */
    @Test
    public void singleUsageStream() {
        List<Employee> employees = getEmployees();
        Stream<Employee> source = getEmployees().stream();



        Stream<Person> personStream = source.map(Employee::getPerson);

        Stream<List<JobHistoryEntry>> anotherStream = source.map(Employee::getJobHistory);


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
