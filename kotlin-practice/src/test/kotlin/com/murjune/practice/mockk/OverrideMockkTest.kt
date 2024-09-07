package com.murjune.practice.mockk

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OverrideMockkTest {
    /**
     * lookUp : name 으로 찾음, 없으면 type 으로 찾음
     *
     *  private으로 적용되는 경우에도 속성이 주입됨 ㄷ ㄷ ㄷ
     *  lateinit var 또는 할당되지 않은 var만을 삽입할 수 있음
     *
     *  이를 변경하려면 overrideValues = true를 사용 --- 이미 초기화 되어 있어도 적용됨
     *  injectImmutable = true를 사용하면 val 변수도 초기화됨
     *
     *  @OverrideMockKs 은 두 flag 둘 다 true 로 설정
     * */
    val age = 10
    val name = "car"
    val price = 100
    val engine = Engine()

    @OverrideMockKs
    private lateinit var car: Car

    @InjectMockKs
    private lateinit var car2: Car

    @InjectMockKs(injectImmutable = true)
    private lateinit var car3: Car

    @InjectMockKs(overrideValues = true)
    private lateinit var car4: Car

    @InjectMockKs(injectImmutable = true, overrideValues = true)
    private lateinit var car5: Car

    @Test
    @DisplayName("OverrideMockKs 을 사용하면 내부에 있는 val 변수도 초기화가 된다")
    fun `test`() {
        assertSoftly {
            car.power() shouldBe engine.power()
            // val 도 초기화됨
            car.age() shouldBe age
        }
    }

    @Test
    @DisplayName("InjectMockKs 에 옵션을 안주면 내부에 있는 val 변수도 초기화 안된다")
    fun `test2`() {
        assertSoftly {
            car2.power() shouldBe engine.power()
            // val 은 Inject 안됨
            car2.age() shouldNotBe age
            car2.age() shouldBe 0
        }
    }

    @Test
    @DisplayName("InjectMockKs 에 injectImmutable = true 줘도 내부에 있는 val 변수도 초기화 안된다")
    fun `test3`() {
        assertSoftly {
            car3.power() shouldBe engine.power()
            // val 은 Inject 안됨
            car3.age() shouldNotBe age
            car3.age() shouldBe 0
        }
    }

    @Test
    @DisplayName("InjectMockKs 에 overrideValues = true 줘도 내부에 있는 val 변수도 초기화 안된다")
    fun `test4`() {
        assertSoftly {
            car4.power() shouldBe engine.power()
            // val 은 Inject 안됨
            car4.age() shouldNotBe age
            car4.age() shouldBe 0
        }
    }

    @Test
    @DisplayName("InjectMockKs 에 overrideValues = true 줘도 내부에 있는 val 변수도 초기화됨")
    fun `test5`() {
        assertSoftly {
            car5.power() shouldBe engine.power()
            // val 은 Inject 안됨
            car5.age() shouldBe age
            car5.age() shouldNotBe 0
        }
    }

    class Car(val name: String = "abc", private val price: Int = 9999) {
        private val age: Int = 0
        private lateinit var engine: Engine

        fun age() = age

        fun power(): Int = engine.power()
    }

    class Engine {
        fun power(): Int = 100
    }
}
