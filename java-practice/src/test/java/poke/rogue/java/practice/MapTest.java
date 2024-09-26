package poke.rogue.java.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    @Test
    @DisplayName("Map of 로 불변 Map을 만들 수 있다.")
    void immutableMap() {
        var map = Map.of();
        assertThatThrownBy(() -> map.put(1, 2))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Map.of 는 , 로 key, value를 구분하여 Map을 만들 수 있다.")
    void immutableMap2() {
        var map = Map.of(
                1, 2,
                3, 4
        );

        assertThat(map).containsEntry(1, 2);
        assertThat(map.get(1)).isEqualTo(2);
        assertThat(map.get(0)).isNull();
    }

    @Test
    @DisplayName("mutableMap 은 HashMap 으로 만들어진다.")
    void mutableMap() {
        //given
        var map = new HashMap<Integer, Integer>();
        var map2 = new HashMap<Integer, Integer>() {
            {
                put(1, 2);
                put(3, 4);
            }
        };
        //when
        map.put(1, 2);
        //then
        assertThat(map).containsEntry(1, 2);
        assertThat(map2).containsEntry(1, 2);
        assertThat(map2).containsEntry(3, 4);
    }

    @Test
    @DisplayName("만약, 특정 원소가 없을 때만 추가하고 싶다면, putIfAbsent 를 사용한다.")
    void mutableMap2() {
        // given
        var map = new HashMap<Integer, Integer>();
        // when
        map.putIfAbsent(1, 1);
        map.putIfAbsent(1, 2);
        map.putIfAbsent(2, 3);
        // then
        assertThat(map).containsEntry(1, 1);
        assertThat(map).containsEntry(2, 3);
    }

    @Test
    @DisplayName("keySet 으로 key만 가져올 수 있다.")
    void testKeyset() {
        // given
        var map = Map.of(
                1, 2,
                3, 4
        );
        // when
        var keySet = map.keySet();
        // then
        assertThat(keySet).contains(1, 3);
    }

    @Test
    @DisplayName("values 로 value만 가져올 수 있다.")
    void test() {
        // given
        var map = Map.of(
                1, 2,
                3, 4
        );
        // when
        var values = map.values();
        var valueList = values.stream().toList();
        // then
        assertThat(valueList).contains(2, 4);
    }
}
