package poke.rogue.java.practice.stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


class GroupByTest {

    @Test
    void intToLong() {
        //given
        int i = 1;
        //when
        long l = (long) i;
        //then
        assertThat(l).isEqualTo(1L);
    }

    @Test
    void each_count() {
        List<String> names = Arrays.asList("John", "Jane", "Tom", "Jerry", "Mike", "Lisa");

        Map<String, Long> groupedByFirstLetter = names.stream()
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        assertThat(groupedByFirstLetter.get("John")).isEqualTo(1);
    }


    @Test
    void test_summingInt() {
        //given
        int[] arr = {3, 2, 1};
        //when
        Map<Integer, Integer> map = Arrays.stream(arr).boxed().collect(Collectors.groupingBy(i -> i, Collectors.summingInt(i -> i)));
        //then
        assertThat(map.get(3)).isEqualTo(3);
    }
}
