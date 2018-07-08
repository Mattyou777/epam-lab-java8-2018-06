package api.example;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import lambda.part1.example.Example1;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings({"Java8ListSort", "Convert2Lambda", "Anonymous2MethodRef", "RedundantTypeArguments"})
public class Example2 {

    @Test
    public void sortWhitComparatorUsingJava7() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 6, 5);

        Collections.sort(values, new Comparator<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                return Integer.compare(right, left);
            }
        });

        assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), values);
    }

    @Test
    public void sortWhitLambdaComparatorUsingJava8() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6);

        Collections.sort(values, (left, right) -> Integer.compare(right, left));

        assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), values);
    }

    @Test
    public void sortWhitIntegerComparatorUsingJava8() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6);

        Person[] persons = Example1.getPersons();
        Arrays.sort(persons, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });
        Comparator<Person> comparing = comparing(Person::getFirstName);
        Arrays.sort(persons, comparing);

        assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), values);
    }

    @Test
    public void sortWhitKeyExtractorComparatorUsingJava8() {
        List<Person> values = getPersons();

        Collections.sort(values, comparing(Person::getFirstName));

        assertEquals(Arrays.asList(
            new Person("Иван", "Литовцев", 15),
            new Person("Ирина", "Семенченко", 27),
            new Person("Кирилл", "Литовцев", 41),
            new Person("Николай", "Дмитриев", 60)
        ), values);
    }

    @Test
    public void sortWhitCombinedKeyExtractorComparatorUsingJava8() {
        List<Person> values = getPersons();

        Collections.sort(values, comparing(Person::getLastName).thenComparing(Person::getFirstName)
                                                               .thenComparing(Person::getAge));
        List<Employee> employees = lambda.part3.example.Example1.getEmployees();

        employees.sort(comparing(Employee::getJobHistory, comparingInt(List::size)).thenComparing(Employee::getPerson, comparing(Person::getFirstName).thenComparing(Person::getLastName)));


        assertEquals(Arrays.asList(
            new Person("Николай", "Дмитриев", 60),
            new Person("Иван", "Литовцев", 15),
            new Person("Кирилл", "Литовцев", 41),
            new Person("Ирина", "Семенченко", 27)
        ), values);
    }

    @Test
    public void sortWhitIntValueKeyExtractorComparatorUsingJava8() {
        List<Person> values = getPersons();

        Collections.sort(values, Comparator.comparingInt(Person::getAge));

        assertEquals(Arrays.asList(
            new Person("Иван", "Литовцев", 15),
            new Person("Ирина", "Семенченко", 27),
            new Person("Кирилл", "Литовцев", 41),
            new Person("Николай", "Дмитриев", 60)
        ), values);
    }

    @Test
    public void sortWhitFirstNullValuesComparatorUsingJava8() {
        List<Person> values = getPersons();
        values.set(2, null);

        Comparator<Person> comparing = Comparator.comparing(Person::getFirstName);
        Collections.sort(values, Comparator.nullsLast(comparing));

        Collections.sort(values, comparing);

        assertEquals(Arrays.asList(
                null,
                new Person("Иван", "Литовцев", 15),
                new Person("Кирилл", "Литовцев", 41),
                new Person("Николай", "Дмитриев", 60)
        ), values);
    }

    public List<Person> getPersons() {
        return Arrays.asList(
            new Person("Иван", "Литовцев", 15),
            new Person("Кирилл", "Литовцев", 41),
            new Person("Ирина", "Семенченко", 27),
            new Person("Николай", "Дмитриев", 60)
        );
    }
}
