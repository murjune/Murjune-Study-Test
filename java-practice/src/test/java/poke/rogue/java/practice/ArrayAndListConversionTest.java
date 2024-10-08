package poke.rogue.java.practice;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ArrayAndListConversionTest {
    @Test
    @DisplayName("mapToInt")
    void test_StringArr_to_IntArr() {
        //given
        String[] strArr = {"1", "2", "3"};
        //when
        int[] intArr = Arrays.stream(strArr).mapToInt(Integer::valueOf).toArray();
        //then
        assertThat(intArr).containsExactly(1, 2, 3);
    }

    record Person(String name, int age) {
    }

    @Test
    @DisplayName("toArray")
    void test_list_to_arr() {
        //given
        List<Person> people = List.of(new Person("John", 20), new Person("Jane", 30));
        //when
        Person[] arr = people.toArray(Person[]::new);
        //then
        assertThat(arr).containsExactly(new Person("John", 20), new Person("Jane", 30));
    }


    @Test
    void test_arr_to_list_to_arr() {
        //given
        int[] arr = {3, 2, 1};
        // when
        List<Integer> li = Arrays.stream(arr).boxed().toList();
        // then
        assertThat(li).containsExactly(3, 2, 1);
        int[] arr2 = li.stream().mapToInt(i -> i).toArray();
        // then
        assertThat(arr2).containsExactly(3, 2, 1);

    }
}
