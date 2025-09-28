package com.murjune.practice.flow.stateflow

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MutableStateFlowUpdateTest {
    @Test
    fun `싱글 스레드 환경에서 setValue 는 데이터 상태이상이 없다`() = runTest {
        // given
        val viewModel = ProductViewModel(backgroundScope)

        // when
        repeat(100_000) {
            viewModel.increaseProductBySetValue()
        }
        runCurrent()

        // then
        viewModel.uiState.value.count shouldBe 100_000
    }

    @Test
    fun `싱글 스레드 환경에서 update 는 데이터 상태이상이 없다`() = runTest {
        // given
        val viewModel = ProductViewModel(backgroundScope)

        // when
        repeat(100_000) {
            viewModel.increaseProductByUpdate()
        }
        runCurrent()

        // then
        viewModel.uiState.value.count shouldBe 100_000
    }

    /**
     * 테스트가 실패할 수도 있다..!
     * 테스트를 통해 실제 멀티 스레드 환경에서 발생하는 레이스 컨디션을 재현하기 어렵다.
     * */
    @Test
    fun `멑티 스레드 환경에서 setValue 는 데이터 상태 이상이 발생할 수 있다`() {
        runBlocking {
            // given
            val viewModelScope = CoroutineScope(Dispatchers.IO)
            val viewModel = ProductViewModel(viewModelScope)

            // when
            repeat(100_000) {
                viewModel.increaseProductBySetValue()
            }

            viewModelScope.coroutineContext.job.children.forEach {
                it.join()
            }

            // then
            viewModel.uiState.value.count shouldNotBe 100_000
        }
    }

    @Test
    fun `멀티 스레드 환경에서 update 는 임계영역을 보호하여, 데이터 상태 이상이 발생하지 않는다`() {
        runBlocking {
            // given
            val viewModelScope = CoroutineScope(Dispatchers.IO)
            val viewModel = ProductViewModel(viewModelScope)

            // when
            repeat(100_000) {
                viewModel.increaseProductByUpdate()
            }
            viewModelScope.coroutineContext.job.children.forEach {
                it.join()
            }

            // then
            viewModel.uiState.value.count shouldBe 100_000
        }
    }

    private abstract class ViewModel(val viewModelScope: CoroutineScope)

    private class ProductViewModel(viewModelScope: CoroutineScope) : ViewModel(viewModelScope) {
        private val _uiState = MutableStateFlow<ProductState>(ProductState.idle())
        val uiState: StateFlow<ProductState> = _uiState.asStateFlow()

        fun increaseProductByUpdate() {
            viewModelScope.launch {
                _uiState.update { it.copy(count = it.count + 1) }
            }
        }

        fun increaseProductBySetValue() {
            viewModelScope.launch {
                val count = _uiState.value.count
                _uiState.value = _uiState.value.copy(count = count + 1)
            }
        }
    }

    private data class ProductState(
        val name: String,
        val count: Int,
    ) {

        companion object {
            fun idle() = ProductState(
                name = "Idle",
                count = 0,
            )
        }
    }
}
