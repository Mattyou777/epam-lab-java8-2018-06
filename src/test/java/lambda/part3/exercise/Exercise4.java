package lambda.part3.exercise;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Exercise4 {

  private static class LazyCollectionHelper<T, R> {

    private List<T> source;
    private Queue<R> queue;
    private Consumer<T> consumer;

    private LazyCollectionHelper(List<T> source, Queue<R> queue, Consumer<T> consumer) {
      this.source = source;
      this.queue = queue;
      this.consumer = consumer;
    }

    public static <T> LazyCollectionHelper<T, T> from(List<T> list) {
      Queue<T> nextQueue = new LinkedList<>();
      return new LazyCollectionHelper<>(list, nextQueue, nextQueue::offer);
    }

    public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
      Queue<U> nextQueue = new LinkedList<>();
      return new LazyCollectionHelper<>(source, nextQueue, consumer.andThen(c -> {
        while (!queue.isEmpty()) {
          flatMapping.apply(queue.poll())
              .forEach(nextQueue::offer);
        }
      }));
    }

    public <U> LazyCollectionHelper<T, U> map(Function<R, U> mapping) {
      Queue<U> nextQueue = new LinkedList<>();
      return new LazyCollectionHelper<>(source, nextQueue, consumer.andThen(c -> {
        while (!queue.isEmpty()) {
          nextQueue.offer(mapping.apply(queue.poll()));
        }
      }));
    }

    public List<R> force() {
      source.forEach(consumer);
      return new ArrayList<>(queue);
    }
  }

  @Test
  public void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
    List<Employee> employees = getEmployees();

    List<Integer> codes = LazyCollectionHelper.from(employees)
        .flatMap(Employee::getJobHistory)
        .map(JobHistoryEntry::getPosition)
        .flatMap(Exercise4::extractCharacters)
        .map(Exercise4::extractCodes)
        .force();
    //               LazyCollectionHelper.from(employees)
    //                                   .flatMap(Employee -> JobHistoryEntry)
    //                                   .map(JobHistoryEntry -> String(position))
    //                                   .flatMap(String -> Character(letter))
    //                                   .map(Character -> Integer(code letter)
    //                                   .force();
    assertEquals(
        calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester", "QA", "QA", "QA", "dev"),
        codes);
  }

  private static Integer extractCodes(Character character) {
    return (int) character;
  }

  private static List<Character> extractCharacters(String s) {
    List<Character> characters = new ArrayList<>();
    for (char character : s.toCharArray()) {
      characters.add(character);
    }
    return characters;
  }

  private static List<Integer> calcCodes(String... strings) {
    List<Integer> codes = new ArrayList<>();
    for (String string : strings) {
      for (char letter : string.toCharArray()) {
        codes.add((int) letter);
      }
    }
    return codes;
  }

  private static List<Employee> getEmployees() {
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
