package lambda.part3.example;

import lambda.data.Employee;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class Example3 {

    public static class LazyFilterUtil<T> {

        private final List<T> source;
        private final Predicate<T> condition;

        public LazyFilterUtil(List<T> source, Predicate<T> condition) {
            this.source = source;
            this.condition = condition;
        }

        /**
         * Статический фабричный метод.
         *
         * @param source Исходный список.
         * @param <T>    Тип элементов исходного списка.
         * @return Созданный объект.
         */
        public static <T> LazyFilterUtil<T> from(List<T> source) {
            return new LazyFilterUtil<>(source, value -> true);
        }

        /**
         * Создает новый список на основе исходного, проверяя каждый элемент на соответствие заданному условию.
         * ([T], (T -> boolean)) -> [T]
         *
         * @param condition условие по которому производится отбор.
         */
        public LazyFilterUtil<T> filter(Predicate<T> condition) {
            return new LazyFilterUtil<>(source, this.condition.and(condition));
        }

        public List<T> force() {
            ArrayList<T> result = new ArrayList<>();

            source.forEach(element -> {
                if (condition.test(element)) {
                    result.add(element);
                }
            });

            return result;
        }
    }

    @Test
    public void findIvanWithDeveloperExperienceAndWorkedInEpamMoreThenYearAtOnePositionUsingLazyFilterUtil() {
        List<Employee> employees = Example1.getEmployees();

        List<Employee> result = LazyFilterUtil.from(employees)
                                              .filter(employee -> "Иван".equals(employee.getPerson()
                                                                                        .getFirstName()))
                                              .filter(Example3::hasDeveloperExpirience)
                                              .filter(Example3::workedInEpamMoreThenYearAtOnePosition)
                                              .force();

        assertEquals(Arrays.asList(employees.get(0), employees.get(5)), result);
    }

    private static boolean hasDeveloperExpirience(Employee employee) {
        return !LazyFilterUtil.from(employee.getJobHistory())
                              .filter(entry -> "dev".equals(entry.getPosition()))
                              .force()
                              .isEmpty();
    }

    private static boolean workedInEpamMoreThenYearAtOnePosition(Employee employee) {
        return !LazyFilterUtil.from(employee.getJobHistory())
                              .filter(entry -> "EPAM".equals(entry.getEmployer()))
                              .filter(entry -> entry.getDuration() > 1)
                              .force()
                              .isEmpty();
    }
}
