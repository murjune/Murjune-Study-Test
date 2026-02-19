package com.murjune.practice.flow.operator

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class DebounceOperatorTest {

    @Test
    fun `Debounce 연산자는 일정 시간 동안 새로운 값이 없으면 마지막 값을 방출한다`() = runTest {
        // given
        val flow = flow {
            emit(1)
            delay(90)  // debounce 시간(100ms)보다 짧음
            emit(2)
            delay(90)  // debounce 시간(100ms)보다 짧음
            emit(3)
            delay(200) // debounce 시간(100ms)보다 김 -> 3이 방출됨
            emit(4)
            delay(200) // debounce 시간(100ms)보다 김 -> 4가 방출됨
        }
        val res = mutableListOf<Int>()
        // when
        flow.debounce(100).onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 1과 2는 너무 빨리 방출되어 무시되고, 3과 4만 방출됨
        val expected = listOf(3, 4)
        res shouldBe expected
    }

    @Test
    fun `Debounce 연산자는 빠르게 연속으로 방출되는 값 중 마지막 값만 방출한다`() = runTest {
        // given
        val flow = flow {
            emit("a")
            delay(50)
            emit("b")
            delay(50)
            emit("c")
            delay(50)
            emit("d")
            // 이후 값 없음 -> flow 종료 시 마지막 값 방출
        }
        val res = mutableListOf<String>()
        // when
        flow.debounce(100).onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // 모든 값이 100ms 이내에 연속으로 방출되므로 마지막 값 "d"만 방출됨
        val expected = listOf("d")
        res shouldBe expected
    }

    @Test
    fun `StateFlow에서 debounce 연산자 사용 시 초기 값도 고려된다`() = runTest {
        // given
        val stateFlow = MutableStateFlow(0)
        val res = mutableListOf<Int>()
        // when
        stateFlow
            .debounce(100)
            .onEach {
                res.add(it)
            }.launchIn(backgroundScope)

        repeat(4) { i ->
            delay(50)
            stateFlow.value = (i + 1) * 10  // 10, 20, 30, 40
        }
        // then
        advanceTimeBy(1000)
        val expected = listOf(40) // 마지막 값 40만 방출됨
        res shouldBe expected
    }

    @Test
    fun `StateFlow + Debounce + combine 사용 사례`() = runTest {
        // given - 검색 UI의 여러 입력 상태
        val searchQuery = MutableStateFlow("")  // 검색어
        val category = MutableStateFlow("전체")  // 카테고리 필터
        val minPrice = MutableStateFlow(0)       // 최소 가격
        val searchResults = mutableListOf<SearchRequest>()

        // when - 검색어는 debounce 적용, 나머지는 즉시 반영
        val stateFlow: StateFlow<SearchRequest> = combine(
            searchQuery.debounce(300),  // 검색어 입력은 300ms 후에 반영
            category,                    // 카테고리는 즉시 반영
            minPrice,                    // 가격은 즉시 반영
        ) { query, cat, price ->
            SearchRequest(
                query = query,
                category = cat,
                minPrice = price,
            )
        }.stateIn(
            scope = backgroundScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchRequest("", "전체", 0),
        )

        stateFlow
            .onEach {
                searchResults.add(it)
            }
            .launchIn(backgroundScope)

        // 사용자 액션 시뮬레이션
        // 0ms: 초기 상태
        searchQuery.value = "안"
        advanceTimeBy(100)
        // 100ms: 빠른 입력 (debounce로 무시됨)
        searchQuery.value = "안녕"
        advanceTimeBy(100)
        // 200ms: 빠른 입력 (debounce로 무시됨)
        searchQuery.value = "안녕하"
        advanceTimeBy(100)
        // 300ms: 빠른 입력 (debounce로 무시됨)
        searchQuery.value = "안녕하세요"
        advanceTimeBy(350)
        // 650ms: 300ms 경과 -> "안녕하세요" 검색 실행

        // 카테고리 변경 (즉시 반영)
        category.value = "도서"
        advanceTimeBy(10)

        // 최소 가격 변경 (즉시 반영)
        minPrice.value = 10000
        advanceTimeBy(10)

        // 다시 검색어 변경
        searchQuery.value = "코틀린"
        advanceTimeBy(1000)

        // then
        // 초기값 ""은 즉시 "안"으로 덮어씌워져서 debounce에 의해 취소됨
        // 1. 검색어 debounce 후: ("안녕하세요", "전체", 0)
        // 2. 카테고리 변경 즉시: ("안녕하세요", "도서", 0)
        // 3. 가격 변경 즉시: ("안녕하세요", "도서", 10000)
        // 4. 검색어 debounce 후: ("코틀린", "도서", 10000)
        searchResults.size shouldBe 5
        searchResults shouldBe listOf(
            SearchRequest("", "전체", 0),
            SearchRequest("안녕하세요", "전체", 0),
            SearchRequest("안녕하세요", "도서", 0),
            SearchRequest("안녕하세요", "도서", 10000),
            SearchRequest("코틀린", "도서", 10000),
        )
    }

    data class SearchRequest(
        val query: String,
        val category: String,
        val minPrice: Int,
    )
}