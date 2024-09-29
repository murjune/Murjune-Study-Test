package poke.rogue.java.practice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ArrayTest {
    @Test
    void testIterator() {
        //given
        int[] arr = {3, 2, 1};
        List<Integer> li = new ArrayList<>();
        //when
        for (int i : arr) {
            li.add(i);
        }
        //then
        assertThat(li).containsExactly(3, 2, 1);
    }

    @Test
    void test_iterator_size() {
        //given
        int[] arr = {3, 2, 1};
        List<Integer> li = new ArrayList<>(10) {{
            add(1);
            add(2);
            add(3);
        }};
        // when&then
        assertThat(arr.length).isEqualTo(3);
        assertThat(li.size()).isEqualTo(3);
    }

    @Test
    void test_add() {
        //given
        int[] arr = {3, 2, 1};
        List<Integer> li = new ArrayList<>();
        //when
        for (int i : arr) {
            li.add(i);
        }
        //then
        assertThat(li).containsExactly(3, 2, 1);
    }

    @Test
    void test_int_for() {
        //given
        int n = 3;
        int sum = 0;
        int sum2 = 0;
        //when
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        for (int i = n; i >= 1; i--) {
            sum2 += i;
        }
        //then
        assertThat(sum).isEqualTo(6);
        assertThat(sum2).isEqualTo(6);
    }

    @Test
    void test2DArrayList() {
        //given
        int n = 3;
        List<List<Integer>> li = new ArrayList<>();
        //when
        for (int i = 0; i < n; i++) {
            li.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                li.get(i).add(j);
            }
        }
        //then
        assertThat(li).containsExactly(
                new ArrayList<Integer>() {{
                    add(0);
                    add(1);
                    add(2);
                }},
                new ArrayList<Integer>() {{
                    add(0);
                    add(1);
                    add(2);
                }},
                new ArrayList<Integer>() {{
                    add(0);
                    add(1);
                    add(2);
                }}
        );
    }

    @Test
    void test2DArray() {
        //given
        int n = 3;
        int[][] arr = new int[n][n];
        //when
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                arr[i][j] = j;
            }
        }
        //then
        for (int i = 0; i < n; i++) {
            assertThat(arr[i]).containsExactly(0, 1, 2);
        }
    }
}
