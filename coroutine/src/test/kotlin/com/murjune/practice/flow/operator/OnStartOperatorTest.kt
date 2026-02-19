package com.murjune.practice.flow.operator

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class OnStartOperatorTest {
    @Test
    fun `onStart 연산자는 collect 를 시작할 때 호출된다`() = runTest {
        // given
        val mutableStateFlow = MutableStateFlow(1)
        var callOnStartTimes = 0L
        val flow = mutableStateFlow
            .onStart {
                callOnStartTimes = currentTime
            }

        delay(100)
        // 100ms -collecrtor 1 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 100L

        delay(100)
        // 200ms - collecrtor 2 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 200L

        delay(100)
        // 200ms - collecrtor 3 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 300
    }

    @Test
    fun `onStart 위치`() = runTest {
        flow {
            // 1. 원본 데이터 방출 (업스트림 Flow)
            emit(1)
            emit(2)
        }
            .map {
                // 3. 0, 1, 2 모두에게 map 적용
                it * 10
            }
            .onEach {
                println("수집된 값: $it")
            }
            .onStart {
                // 2. 수집 시작 직전에 실행 및 값 방출
                println(">>> Flow 시작: 로딩 상태 emit")
                emit(0)
            }
            .debounce(300)
            .launchIn(this)

        advanceUntilIdle()
    }

    @Test
    fun `catch 연산자는 에러가 발생했을 때 호출된다`() = runTest {
        // given
        val flow = flow<Int> {
            emit(1)
            emit(2)
            throw IllegalStateException("Test Exception")
        }.onStart {
            println("Flow 시작")
        }.catch {
            println("에러 발생: ${it.message}")
            emit(9999)
        }

        val collectedValues = mutableListOf<Int>()
        // when
        flow
            .onEach {
                collectedValues.add(it)
            }
            .launchIn(this)

        // then
        advanceUntilIdle()
        collectedValues shouldBe listOf(1, 2, 9999)
    }

    /**
     * StateFlow 는 업스트림 Flow 가 한 번 종료(complete or error)되면 다시 시작되지 않는다.
     * → 즉, 두 번째 collector가 붙어도 upstream flow 를 다시 실행하지 않는다.
     * → 그래서 fetchSomeThing() 은 두 번째 collect 시점에는 절대 호출되지 않는다.
     * → 따라서 42는 절대 emit 되지 않는다.
     * */
    @Test
    fun `StateFlow 와 catch 연산자 사용 시 예외 발생시 StateFlow 다시 시작안된다`() = runTest {
        var isFirstCollect = true
        val fetchSomeThing: suspend () -> Int = {
            if (isFirstCollect) {
                isFirstCollect = false
                throw IllegalStateException("Fetch Error")
            }
            42
        }

        val stateFlow = flow {
            emit(fetchSomeThing())
            throw IllegalStateException("Test Exception")
        }.catch {
            println("에러 발생: ${it.message}")
            // StateFlow 는 예외 발생 후 다시 시작되지 않음
        }.onCompletion {
            println("Flow 완료 $it")
        }.stateIn(
            scope = backgroundScope,
            started = SharingStarted.WhileSubscribed(100),
            initialValue = -1
        )
        val collectedValues = mutableListOf<Int>()

        stateFlow
            .launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()

        stateFlow
            .onEach {
                collectedValues.add(it)
            }
            .launchIn(backgroundScope)

        advanceUntilIdle()
        runCurrent()
        collectedValues shouldBe listOf(-1)
    }

    /**
     * StateFlow 는 업스트림 Flow 가 한 번 종료(complete or error)되면 다시 시작되지 않는다.
     * → 즉, 두 번째 collector가 붙어도 upstream flow 를 다시 실행하지 않는다.
     * → 그래서 fetchSomeThing() 은 두 번째 collect 시점에는 절대 호출되지 않는다.
     * → 따라서 42는 절대 emit 되지 않는다.
     *
     * 네 테스트가 실패하는 이유는:
     * 	•	StateFlow 는 upstream flow 를 단 한 번만 실행한다.
     * 	•	첫 실행이 실패하면 두 번째 collect 시점에는 upstream 실행이 아예 일어나지 않는다.
     * 	•	따라서 42가 나올 수 없다.
     * 	•	기대값 [-1, 42]는 StateFlow 개념상 이루어질 수 없는 일이다.
     * */
    @Test
    fun `StateFlow 와 catch 연산자 사용 시 예외 발생시 StateFlow 다시 시작안된다2`() = runTest {
        var isFirstCollect = true
        val fetchSomeThing: suspend () -> Int = {
            if (isFirstCollect) {
                isFirstCollect = false
                throw IllegalStateException("Fetch Error")
            }
            42
        }

        val stateFlow = flow {
            emit(fetchSomeThing())
            throw IllegalStateException("Test Exception")
        }.catch {
            println("에러 발생: ${it.message}")
            // StateFlow 는 예외 발생 후 다시 시작되지 않음
        }.onCompletion {
            println("Flow 완료 $it")
        }.stateIn(
            scope = backgroundScope,
            started = SharingStarted.WhileSubscribed(100),
            initialValue = -1
        )
        val collectedValues = mutableListOf<Int>()

        val job = stateFlow
            .launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        job.cancelAndJoin()
        advanceTimeBy(200)
        // WhileSubscribed 는 subscriber 가 생기면 upstream 을 다시 실행한다
        stateFlow
            .onEach {
                collectedValues.add(it)
            }
            .launchIn(backgroundScope)

        advanceUntilIdle()
        runCurrent()
        collectedValues shouldBe listOf(-1, 42)
    }

    @Test
    fun `Flow 와 catch 연산자 사용 시 예외 발생시 Flow 다시 시작`() = runTest {
        var isFirstCollect = true
        val fetchSomeThing: suspend () -> Int = {
            if (isFirstCollect) {
                isFirstCollect = false
                throw IllegalStateException("Fetch Error")
            }
            42
        }

        val flow = flow {
            emit(fetchSomeThing())
            throw IllegalStateException("Test Exception")
        }.catch {
            println("에러 발생: ${it.message}")
            // StateFlow 는 예외 발생 후 다시 시작되지 않음
        }.onCompletion {
            println("Flow 완료 $it")
        }
        val collectedValues = mutableListOf<Int>()

        flow
            .launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()

        flow
            .onEach {
                collectedValues.add(it)
            }
            .launchIn(backgroundScope)

        advanceUntilIdle()
        runCurrent()
        collectedValues shouldBe listOf(42)
    }
}