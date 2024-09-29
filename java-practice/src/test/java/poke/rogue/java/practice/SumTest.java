package poke.rogue.java.practice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SumTest {

    @Test
    void test_Sum_arr() {
        int[] arr = {1, 2, 3};
        var d = Arrays.stream(arr).map(i -> i * 2).sum();
        assertThat(d).isEqualTo(12);
    }


    @Test
    void test_reduce_arr() {
        //given
        int[] arr = {3, 2, 1};
        // when
        int sum = Arrays.stream(arr).reduce(Integer::sum).orElse(0);
        int sum2 = Arrays.stream(arr).reduce(1, (left, right) -> left + right);
        // then
        assertThat(sum).isEqualTo(6);
        assertThat(sum2).isEqualTo(7);
    }
}
