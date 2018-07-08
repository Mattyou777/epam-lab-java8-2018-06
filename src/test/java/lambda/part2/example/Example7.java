package lambda.part2.example;

import lambda.data.Person;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SameParameterValue")
public class Example7 {

    @FunctionalInterface
    private interface PersonFactory {

        Person create(String name, String lastName, int age);
    }

    // ((String, String, int) -> Person) -> String -> String -> int -> Person
    private static Function<String, Function<String, IntFunction<Person>>> curriedPersonFactory(PersonFactory factory) {
        return name -> lastName -> age -> factory.create(name, lastName, age);
    }

    @Test
    public void testCurriedPersonFactory() {
        Person person = new Person("Иван", "Мельников", 33);

        PersonFactory factoryUsingConstructor = Person::new;
        assertEquals(person, factoryUsingConstructor.create("Иван", "Мельников", 33));

        Function<String, Function<String, IntFunction<Person>>> curriedPersonFactory = curriedPersonFactory(Person::new);
        assertEquals(person, curriedPersonFactory.apply("Иван").apply("Мельников").apply(33));

        Function<String, Function<String, IntFunction<Person>>> lambdaCurriedPersonFactory = lastName -> name -> age -> factoryUsingConstructor.create(name, lastName, age);
        assertEquals(person, lambdaCurriedPersonFactory.apply("Мельников").apply("Иван").apply(33));
    }

    // ((String, String, int) -> Person, String) -> ((String, Integer) -> Person)
    private BiFunction<String, Integer, Person> personFactoryWithFixedLastName(PersonFactory factory, String lastName) {
        return (name, age) -> factory.create(name, lastName, age);
    }

    @Test
    public void testPartiallyAppliedPersonFactory() {
        Person person = new Person("Иван", "Мельников", 33);

        PersonFactory factory = Person::new;

        BiFunction<String, Integer, Person> melnkikovsFactory = personFactoryWithFixedLastName(factory, "Мельников");
        assertEquals(person, melnkikovsFactory.apply("Иван", 33));

        BiFunction<String, Integer, Person> lambdaPartyalliAppliedMelnkikovsFactory = (name, age) -> factory.create(name, "Мельников", age);
        assertEquals(person, lambdaPartyalliAppliedMelnkikovsFactory.apply("Иван", 33));
    }

    @Test
    public void testCurriedAndPartiallyAppliedPersonFactory() {
        Person person = new Person("Иван", "Мельников", 33);

        PersonFactory factory = Person::new;

        IntFunction<Function<String, Function<String, Person>>> curriedRevertedFactory = age -> lastName -> name -> factory.create(name, lastName, age);
        assertEquals(person, curriedRevertedFactory.apply(33).apply("Мельников").apply("Иван"));

        Function<String, IntFunction<Person>> melnkikovsFactory = name -> age -> curriedRevertedFactory.apply(age).apply("Мельников").apply(name);
        assertEquals(new Person("Семён", "Мельников", 25), melnkikovsFactory.apply("Семён").apply(25));

        Function<String, Function<String, Person>> youngGuysFactory = lastName -> name -> curriedRevertedFactory.apply(17).apply(lastName).apply(name);
        assertEquals(new Person("Алексей", "Игнатьев", 17), youngGuysFactory.apply("Игнатьев").apply("Алексей"));
    }
}
