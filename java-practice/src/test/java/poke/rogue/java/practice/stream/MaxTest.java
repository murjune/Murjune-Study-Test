package poke.rogue.java.practice.stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MaxTest {
    @Test
    void test_max() {
        int[] arr = {1, 2, 3};
        var res = Arrays.stream(arr).map(i -> i * 2).max().orElse(0);
        assertThat(res).isEqualTo(6);
    }


    record Person(String name, int age) {
    }

    @Test
    @DisplayName("Kotlin 의 maxWith")
    void test_max_record() {
        //given
        Person[] arr = {new Person("a", 1), new Person("b", 2), new Person("c", 3)};
        //when
        Person maxPeople = Arrays.stream(arr).max((p1, p2) -> p1.age - p2.age).get();
        //then
        assertThat(maxPeople).isEqualTo(arr[2]);
    }
}
