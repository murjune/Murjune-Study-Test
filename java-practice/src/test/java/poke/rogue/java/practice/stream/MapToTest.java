package poke.rogue.java.practice.stream;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class MapToTest {

    @Test
    @DisplayName("배열을 Map으로 변환하기 (인덱스를 키로, 요소를 값으로")
    void test_intArr_to_map() {
        //given
        int[] arr = {3, 2, 1};
        Map<Integer, Integer> map = IntStream.range(0, arr.length).boxed().collect(Collectors.toMap(i -> i, i -> arr[i]));

        // then
        assertThat(map.get(0)).isEqualTo(3);
    }

    @Test
    void test_stringArr_to_map() {
        //given
        String[] names = {"John", "Jane", "Tom", "Jerry", "Mike", "Lisa"};
        int n = names.length;

        Map<String, Integer> map = IntStream.range(0, n).boxed().collect(Collectors.toMap(i -> names[i], i -> i));

        AssertionsForClassTypes.assertThat(map.get("John")).isEqualTo(0);
    }
}
