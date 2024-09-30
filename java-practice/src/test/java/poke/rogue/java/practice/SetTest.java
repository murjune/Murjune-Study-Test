package poke.rogue.java.practice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class SetTest {

    @Test
    void test_array_to_set() {
        //given
        int[] arr = {3, 2, 1, 2, 3, 4};
        //when
        Set<Integer> set = new HashSet<>(Arrays.stream(arr).boxed().toList());
        // then
        assertThat(set).containsExactly(1, 2, 3, 4);
    }
}
