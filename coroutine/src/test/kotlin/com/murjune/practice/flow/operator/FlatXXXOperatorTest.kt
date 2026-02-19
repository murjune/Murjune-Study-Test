package com.murjune.practice.flow.operator

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * flatMapXXX 연산자는 Flow의 각 값을 다른 Flow로 변환하고 평탄화(flatten)한다.
 *
 * - flatMapConcat: 순차적으로 처리 (이전 Flow 완료 후 다음 Flow 시작)
 * - flatMapMerge: 병렬로 처리 (모든 Flow 동시 실행)
 * - flatMapLatest: 최신 것만 처리 (새로운 값이 오면 이전 Flow 취소)
 *
 * ref:
 * - https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flat-map-concat.html
 * - https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flat-map-merge.html
 * - https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flat-map-latest.html
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlatXXXOperatorTest {
    @Test
    fun `flatMapConcat은 순차적으로 Flow를 처리한다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3)
        val res = mutableListOf<String>()
        // when
        flow.flatMapConcat { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 순차적으로 처리되므로 1a -> 1b -> 2a -> 2b -> 3a -> 3b
        val expected = listOf("1a", "1b", "2a", "2b", "3a", "3b")
        res shouldBe expected
    }

    @Test
    fun `flatMapConcat은 이전 Flow가 완료될 때까지 다음 Flow를 기다린다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3).onEach { delay(10) }
        val res = mutableListOf<String>()
        // when
        // 10ms  110ms  210ms  310ms  410ms  510ms
        //  1     1b     2      2b     3      3b
        flow.flatMapConcat { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf("1a", "1b", "2a", "2b", "3a", "3b")
        res shouldBe expected
        currentTime shouldBe 330 // 10 + 100 + 10 + 100 + 10 + 100
    }

    @Test
    fun `✅ flatMapConcat은 순서를 보장해야 할 때 사용한다`() = runTest {
        // given
        val userIds = flowOf(1, 2, 3)
        val res = mutableListOf<String>()
        // when
        userIds.flatMapConcat { userId ->
            // API 호출을 순차적으로 처리
            fetchUserData(userId)
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 순서가 보장됨
        val expected = listOf("User1", "User2", "User3")
        res shouldBe expected
    }

    @Test
    fun `flatMapMerge는 병렬로 Flow를 처리한다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3).onEach { delay(10) }
        val res = mutableListOf<String>()
        // when
        // 10ms  20ms  30ms  110ms 120ms 130ms
        //  1     2     3    1b    2b    3b
        //  1a    2a    3a
        flow.flatMapMerge { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 병렬로 처리되므로 1a, 2a, 3a가 먼저 나오고, 그 다음 1b, 2b, 3b
        val expected = listOf("1a", "2a", "3a", "1b", "2b", "3b")
        res shouldBe expected
        currentTime shouldBe 130 // 병렬 처리로 훨씬 빠름
    }

    @Test
    fun `flatMapMerge는 concurrency로 동시 실행 개수를 제한할 수 있다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3, 4).onEach { delay(10) }
        val res = mutableListOf<String>()
        // when
        // concurrency=2로 최대 2개만 동시 실행
        flow.flatMapMerge(concurrency = 2) { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 1, 2가 먼저 시작되고, 1 완료 후 3 시작, 2 완료 후 4 시작
        // 1b 방출 -> slot 해제 -> 3 시작 -> 3a 방출 -> 2b 방출
        val expected = listOf("1a", "2a", "1b", "3a", "2b", "4a", "3b", "4b")
        res shouldBe expected
    }

    @Test
    fun `✅ flatMapMerge는 독립적인 작업을 병렬로 처리할 때 사용한다`() = runTest {
        // given
        val userIds = flowOf(1, 2, 3)
        val res = mutableListOf<String>()
        // when
        userIds.flatMapMerge { userId ->
            // API 호출을 병렬로 처리
            fetchUserData(userId)
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 순서는 보장되지 않지만 빠름
        res.size shouldBe 3
    }

    // ========== flatMapLatest ==========

    @Test
    fun `flatMapLatest는 새로운 값이 오면 이전 Flow를 취소한다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3).onEach { delay(10) }
        val res = mutableListOf<String>()
        // when
        // 10ms  20ms  30ms  130ms
        //  1(취소) 2(취소) 3     3b
        //  1a     2a    3a
        flow.flatMapLatest { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 1과 2는 100ms를 기다리는 중에 취소되고, 3만 완료됨
        val expected = listOf("1a", "2a", "3a", "3b")
        res shouldBe expected
    }

    @Test
    fun `flatMapLatest는 최신 값만 처리한다`() = runTest {
        // given
        val searchQueries = flow {
            emit("안")
            delay(50)
            emit("안녕")
            delay(50)
            emit("안녕하")
            delay(50)
            emit("안녕하세요")
            delay(200) // 충분한 시간을 줌
        }
        val res = mutableListOf<String>()
        // when
        searchQueries.flatMapLatest { query ->
            // 검색 API 호출 (100ms 소요)
            searchApi(query)
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // "안", "안녕", "안녕하"는 100ms를 기다리는 중에 취소되고
        // "안녕하세요"만 완료됨
        val expected = listOf("Search: 안녕하세요")
        res shouldBe expected
    }

    @Test
    fun `✅ flatMapLatest는 검색 자동완성처럼 최신 입력만 처리할 때 사용한다`() = runTest {
        // given
        val userInputs = flow {
            emit("k")
            delay(10)
            emit("ko")
            delay(10)
            emit("kot")
            delay(10)
            emit("kotl")
            delay(10)
            emit("kotlin")
            delay(200) // 입력 완료 후 충분한 대기
        }
        val res = mutableListOf<String>()
        // when
        userInputs.flatMapLatest { input ->
            // 서버에 검색 요청 (100ms 소요)
            flow {
                delay(100)
                emit("Search result for: $input")
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 중간 입력들은 모두 취소되고 마지막 "kotlin"만 검색됨
        val expected = listOf("Search result for: kotlin")
        res shouldBe expected
    }

    @Test
    fun `‼️ flatMapLatest는 느린 Flow가 빠른 Flow에 의해 계속 취소될 수 있다`() = runTest {
        // given
        val flow = flowOf(1, 2, 3, 4, 5).onEach { delay(10) }
        val res = mutableListOf<String>()
        // when
        flow.flatMapLatest { value ->
            flow {
                emit("${value}a")
                delay(100)
                emit("${value}b") // 이 부분은 취소됨
            }
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 1a~4a는 방출되지만 delay(100) 전에 다음 값이 와서 b는 방출 안 됨
        // 5만 완료됨
        val expected = listOf("1a", "2a", "3a", "4a", "5a", "5b")
        res shouldBe expected
    }

    // ========== Helper Functions ==========

    private fun fetchUserData(userId: Int): kotlinx.coroutines.flow.Flow<String> = flow {
        delay(50)
        emit("User$userId")
    }

    private fun searchApi(query: String): kotlinx.coroutines.flow.Flow<String> = flow {
        delay(100)
        emit("Search: $query")
    }
}
