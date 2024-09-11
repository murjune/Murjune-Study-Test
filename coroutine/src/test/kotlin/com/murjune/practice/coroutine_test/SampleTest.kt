package com.murjune.practice.coroutine_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SampleTest {
    @Test
    fun `쿠폰 불러오기`() = runTest {
        // given
        val cartRepository = CartRepository()
        // when
        val (cart, coupon) = cartRepository.getCartAndCoupon()
        // then
        cart shouldBe Cart(listOf(Item("item1", 1000), Item("item2", 2000)))
        coupon shouldBe Coupon(500)
    }

    @Test
    fun `쿠폰 경과시간 지나는거 가능? 불가능`() = runTest {
        // given
        val cartRepository = CartRepository()
        // when
        val (cart, coupon) = cartRepository.getCartAndCoupon()
        // then
        advanceUntilIdle()
        shouldThrow<AssertionError> {
            currentTime shouldBe 1000
        }
    }
}

data class Item(val name: String, val price: Int)

data class Cart(val items: List<Item> = emptyList())

data class Coupon(val discountAmount: Int)

class CartRepository {
    suspend fun getCartAndCoupon(): Pair<Cart, Coupon> = withContext(Dispatchers.IO) {
        val cart = async {
            delay(1000)
            Cart(listOf(Item("item1", 1000), Item("item2", 2000)))
        }
        val coupon = async {
            delay(1000)
            Coupon(500)
        }
        cart.await() to coupon.await()
    }
}
