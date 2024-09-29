package poke.rogue.java.practice.stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class IntStreamTest {
    @Test
    void testIntStream() {
        //given
        int n = 5;
        List<Integer> li = new ArrayList<>();
        //when
        IntStream.range(0, n).forEach(li::add);
        //then
        assertThat(li).containsExactly(0, 1, 2, 3, 4);
    }
}
