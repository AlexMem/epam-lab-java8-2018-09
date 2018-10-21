package lambda.part3.exercise;

import com.google.common.primitives.Chars;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings({"unused", "ConstantConditions"})
class Exercise4 {

    private static class LazyCollectionHelper<T, R> {

        private List<T> source;
        private Function<T, List<R>> function;

        public LazyCollectionHelper(@NotNull List<T> list, @NotNull Function<T, List<R>> function) {
            this.source = list;
            this.function = function;
        }

        public static <T> LazyCollectionHelper<T, T> from(@NotNull List<T> list) {
            return new LazyCollectionHelper<>(list, Collections::singletonList);
        }

        public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
            return new LazyCollectionHelper<>(source, unify(flatMapping).compose(function));
        }

        public <U> LazyCollectionHelper<T, U> map(Function<R, U> mapping) {
            return new LazyCollectionHelper<>(source, unify(mapping.andThen(Collections::singletonList)).compose(function));
        }

        public List<R> force() {
            return unify(function).apply(source);
        }

        private <U, V> Function<List<U>, List<V>> unify(Function<U, List<V>> function) {
            return list -> {
                List<V> result = new ArrayList<>();
                list.forEach(function.andThen(result::addAll)::apply);
                return result;
            };
        }
    }

    @Test
    void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
        List<Employee> employees = getEmployees();

        List<Integer> codes = LazyCollectionHelper.from(employees)
                                                  .flatMap(Employee::getJobHistory)
                                                  .map(JobHistoryEntry::getPosition)
                                                  .flatMap(position -> Chars.asList(position.toCharArray()))
                                                  .map(Integer::valueOf)
                                                  .force();
        assertThat(codes, Matchers.contains(calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester", "QA", "QA", "QA", "dev").toArray()));
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
                                new JobHistoryEntry(1, "tester", "U-Systems")
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
                                new JobHistoryEntry(2, "QA", "U-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }

}
