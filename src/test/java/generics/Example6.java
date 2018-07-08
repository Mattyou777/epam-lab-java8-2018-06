package generics;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Example6 {

    public static void main(String[] args) {
        // ? extends
        List<Integer> integers = new ArrayList<>();
        List<? extends Number> list = new ArrayList<Integer>();
        list.add(null);

        Number number = list.get(10);

        // ? super
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("abc");
        List<? super Number> list2 = objects;
        list2.add(new Integer(19));
        list2.add(50d);
        list2.add(null);

        Object object = list2.get(0);

        // Producer
        // Extends
        // Consumer
        // Super

        List<Person> persons = Arrays.asList(new Person("Ivan", 24), new Person("Alex", 28));
        Comparator<Person> byAge = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                // o1.age == Integer.MIN_VALUE
                // o2.age == 10
                return Integer.compare(o1.age, o2.age);
            }
        };
        sortByAge(persons, byAge);

        Comparator<Object> byHashCode = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.compare(o1.hashCode(), o2.hashCode());
            }
        };
        List<Student> students = Arrays.asList(new Student("Ivan", 24, 5), new Student("Alex", 28, 6));
        sortByAge(students, byHashCode);
    }

    private static void sortByAge(List<? extends Person> persons, Comparator<? super Person> comparator) {
        persons.sort(comparator);
    }


    @NotNull
    private static ArrayList<? extends Number> getList() {
        if (true) {
            return new ArrayList<Integer>();
        } else {
            return new ArrayList<Double>();
        }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {

    String name;
    int age;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
class Student extends Person {

    int year;

    public Student(String name, int age, int year) {
        super(name, age);
        this.year = year;
    }
}