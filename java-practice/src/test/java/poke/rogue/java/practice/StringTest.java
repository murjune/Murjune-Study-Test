package poke.rogue.java.practice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class StringTest {

    @Test
    void test_split() {
        //given
        String str = "a,b,c";
        //when
        String[] arr = str.split(",");
        //then
        assertThat(arr).containsExactly("a", "b", "c");
    }


    @Test
    void test_charAt() {
        //given
        String s = "12345";
        //when
        Character chr = s.charAt(0);
        //then
        assertThat(chr).isEqualTo('1');
    }
}
