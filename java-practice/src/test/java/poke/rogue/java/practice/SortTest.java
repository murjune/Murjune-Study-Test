package poke.rogue.java.practice;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SortTest {

    @Test
    void test_intArray_Sort() {
        int[] arr = {3, 2, 1};

        List<Integer> sortedList = Arrays.stream(arr)
                .boxed()
                .sorted()
                .toList();

        assertThat(sortedList).containsExactly(1, 2, 3);
    }

    record Person(String name, int age) {
    }


    @Test
    void test_record_sort() {
        //given
        Person[] arr = {new Person("a", 1), new Person("b", 3), new Person("c", 2)};
        // when
        List<Person> li = Arrays.stream(arr).sorted((p1, p2) -> p1.age - p2.age).toList();
        // then
        assertThat(li.get(0).age).isEqualTo(1);
        assertThat(li.get(li.size() - 1).age).isEqualTo(3);
    }
}