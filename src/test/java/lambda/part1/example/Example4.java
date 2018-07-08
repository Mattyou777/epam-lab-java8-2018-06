package lambda.part1.example;

import lambda.data.Person;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://www.ibm.com/developerworks/library/j-java8idioms10/index.html">Java8 lambdas and closures</a>
 */
@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef", "Convert2MethodRef"})
public class Example4 {

    private static String performInCurrentThread(Callable<String> task) throws Exception {
        return task.call();
    }

    static Callable<String> staticRef;

    private int value = 42;

    @Test
    public void closureAnonymousClass() throws Exception {
        // FIXME effectively final
        Person person = new Person("Иван", "Мельников", 33);

        staticRef = new Callable<String>() {

            @Override
            public String call() {
                return person.getFirstName();
            }
        };

        String firstName = performInCurrentThread(new Callable<String>() {
            @Override
            public String call() {
                return person.getFirstName();
            }
        });

        assertEquals("Иван", firstName);
    }

    @Test
    public void closureStatementLambda() throws Exception {
        Person person = new Person("Иван", "Мельников", 33);

        staticRef = () -> {
            String prefix = person.getAge() > 30 ? "Добрый день" : "Привет";
            return prefix + ", " + person.getFirstName();
        };

        String greeting = performInCurrentThread(() -> {
            String prefix = person.getAge() > 30 ? "Добрый день" : "Привет";
            return prefix + ", " + person.getFirstName();
        });

        assertEquals("Добрый день, Иван", greeting);
    }

    @Test
    public void closureExpressionLambda() throws Exception {
        Person person = new Person("Иван", "Мельников", 33);

        String firstName = performInCurrentThread(() -> person.getFirstName());

        assertEquals("Иван", firstName);
    }

    @Test
    public void closureObjectMethodReferenceLambda() throws Exception {
        Person person = new Person("Иван", "Мельников", 33);

        String firstName = performInCurrentThread(person::getFirstName);

        assertEquals("Иван", firstName);
    }

    private Person person;

    @Test
    public void closureReferenceByObjectMethodReferenceLambda() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> getFirstName = this.person::getFirstName;
        staticRef = getFirstName;
        String firstName = performInCurrentThread(getFirstName);

        person = null;

        System.gc();

        staticRef.call();

        assertEquals("Иван", firstName);
    }

    @Test
    public void closureThisReferenceByExpressionLambda() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        String firstName = performInCurrentThread(() -> person.getFirstName());
        assertEquals("Иван", firstName);
    }

    @Test
    public void closureThisReferenceByLambda() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        String firstName = performInCurrentThread(this.person::getFirstName);
        assertEquals("Иван", firstName);

        firstName = performInCurrentThread(new Callable<String>() {

            private final Person hiddenReference = Example4.this.person;

            @Override
            public String call() {
                return hiddenReference.getFirstName();
            }

        });

        assertEquals("Иван", firstName);
    }

    @Test
    public void overwriteReferenceClosuredByExpressionLambdaAfterUsing() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> task = () -> person.getFirstName();
        String firstName = performInCurrentThread(task);

        // FIXME какое имя следует ожидать?
        assertEquals("Иван", firstName);

        person = new Person("Алексей", "Игнатенко", 25);


        assertEquals("Алексей", task.call());
    }

    @Test
    public void overwriteReferenceClosuredByObjectMethodReferenceLambdaAfterUsing() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> task = this.person::getFirstName;
        String firstName = performInCurrentThread(task);
        // FIXME какое имя следует ожидать?
        assertEquals("Иван", firstName);

        person = new Person("Алексей", "Игнатенко", 25);
        String name = task.call();
        assertEquals("Иван", name);
    }

    private Callable<String> performLaterFromCurrentThread(Callable<String> task) {
        return () -> {
            System.out.println("Некий код перед выполнением задачи...");
            return task.call();
        };
    }

    @Test
    public void overwriteReferenceClosuredByExpressionLambdaBeforeUsing() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> delayedTask = performLaterFromCurrentThread(() -> this.person.getFirstName());

        person = new Person("Алексей", "Игнатенко", 25);

        String firstName = delayedTask.call();


        Supplier<String> getLastName = person::getLastName;
        getLastName.get();

        Function<Person, String> getLastName2 = Person::getLastName;
        String apply = getLastName2.apply(person);

        // FIXME какое имя следует ожидать?
        assertEquals("Алексей", firstName);
    }

    @Test
    public void overwriteReferenceClosuredByObjectMethodReferenceLambdaBeforeUsing() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> delayedTask = performLaterFromCurrentThread(person::getFirstName);

        person = new Person("Алексей", "Игнатенко", 25);

        String firstName = delayedTask.call();

        // FIXME какое имя следует ожидать?
        assertEquals("Иван", firstName);

    }

    private Person getPerson() {
        return person;
    }

    @Test
    public void overwriteReferenceClosuredByObjectMethodReferenceGetPersonBeforeUsing() throws Exception {
        person = new Person("Иван", "Мельников", 33);

        Callable<String> delayedTask = performLaterFromCurrentThread(getPerson()::getFirstName);

        person = new Person("Алексей", "Игнатенко", 25);

        String firstName = delayedTask.call();

        // FIXME какое имя следует ожидать?
        assertEquals("Иван", firstName);
    }
}
