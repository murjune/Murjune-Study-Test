package poke.rogue.java.practice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class PrimitiveTypeConversionTest {

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
    void string_to_Integer() {
        String s = "1";
        int num = Integer.parseInt(s);
        assertThat(num).isEqualTo(1);
    }


    @Test
    void char_to_Int() {
        char c = '1';
        int num = c - '0';
        assertThat(num).isEqualTo(1);
    }
}
