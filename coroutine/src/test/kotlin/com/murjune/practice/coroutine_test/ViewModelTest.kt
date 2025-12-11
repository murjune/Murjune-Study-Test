package com.murjune.practice.coroutine_test

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class ViewModelTest {

    // io Dispatcher 를 Mockk 를 사용하여 테스트하는 방법
    @Test
    fun test() = runTest {
        // given
        val viewModel = ViewModel()
        // when
        viewModel.increaseCount()
        // then
        delay(1000)
        viewModel.count.value shouldBe 1
    }

    @Test
    fun test2() = runTest {
        // given
        val viewModel = ViewModel()
        // when
        viewModel.increaseCountWithIO()
        // then
        delay(10_000)
        viewModel.count shouldNotBe 1
    }

    @Test
    fun test3() {
        runBlocking {
            // given
            val viewModel = ViewModel()
            // when
            viewModel.increaseCountWithIO()
            // then
            delay(10_000)
            viewModel.count.value shouldBe 1
        }
    }
}


class ViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main.immediate)
    var count = Count()

    fun increaseCount() {
        viewModelScope.launch {
            count.postValue(count.value + 1)
        }
    }

    fun increaseCountWithIO() {
        viewModelScope.launch(Dispatchers.IO) {
            count.postValue(count.value + 1)
        }
    }
}

class Count {
    var value = 0
        private set

    suspend fun postValue(count: Int) {
        delay(100)
        this.value = count
    }
}

@ExperimentalCoroutinesApi
class CoroutinesTestExtension(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
