package com.murjune.practice.cancel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Coroutine 취소(Cancellation) 주의사항 학습 테스트
 *
 * ## 핵심 개념:
 * - Coroutine 취소는 **협력적(Cooperative)**이다
 * - cancel() 호출 시 즉시 취소되는 것이 아니라, **중단 지점(suspension point)**에서 CancellationException 발생
 * - 중단 지점: delay(), yield(), suspend 함수 호출 등
 *
 * ## 주요 학습 내용:
 * 1. **try-catch로 CancellationException 잡아먹지 않기**
 *    - catch에서 ensureActive() 호출 필수
 *
 * 2. **finally에서 suspend 함수 호출 시 withContext(NonCancellable) 사용**
 *    - 리소스 정리를 안전하게 수행
 *
 * 3. **CoroutineScope 취소 vs 자식 코루틴만 취소**
 *    - scope.cancel(): 전체 스코프 취소, 재사용 불가
 *    - scope.coroutineContext.cancelChildren(): 자식만 취소, 스코프 재사용 가능
 *
 * 4. **긴 연산/IO 작업에서 주기적으로 ensureActive() 체크**
 *    - blocking 작업은 자동으로 취소되지 않음
 */
class CoroutineCancelCautionTest : FunSpec({

    context("1. try-catch로 CancellationException을 잡아먹는 문제") {
        test("✅ 정상: try-catch가 repeat 밖에 있으면 정상 취소됨") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val taskCompleted = AtomicBoolean(false)
            val fetchCount = AtomicInteger(0)

            val job = coroutineScope.launch {
                try {
                    repeat(10) { i ->
                        fetchData(i)
                        fetchCount.incrementAndGet()
                    }
                    taskCompleted.set(true)
                } catch (e: CancellationException) {
                    // 정상적으로 취소됨
                }
            }

            delay(300) // 300ms 후 취소 (약 3개 완료 예상)
            job.cancelAndJoin()

            // 검증: 모든 작업이 완료되지 않았고, 일부만 실행됨
            taskCompleted.get().shouldBeFalse()
            fetchCount.get() shouldBeLessThan 10
        }

        test("❌ 문제: try-catch가 repeat 안에 있으면 취소가 제대로 안됨") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val catchCount = AtomicInteger(0) // CancellationException을 몇 번 잡았는지

            val job = coroutineScope.launch {
                repeat(10) { i ->
                    try {
                        fetchData(i)
                    } catch (_: Exception) {
                        // ❌ CancellationException을 잡아먹어서 다음 루프가 계속 실행됨
                        catchCount.incrementAndGet()
                    }
                }
            }

            delay(300)
            job.cancelAndJoin()

            // 검증: CancellationException이 여러 번 발생했음 (취소가 제대로 안됨)
            // 정상적이면 1번만 발생해야 하지만, 잡아먹기 때문에 여러 번 발생
            catchCount.get() shouldBeGreaterThan 1 // 여러 번의 취소 시도가 catch됨
        }

        test("✅ 해결: catch에서 ensureActive() 호출") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val allTasksCompleted = AtomicBoolean(false)
            val fetchCount = AtomicInteger(0)

            val job = coroutineScope.launch {
                repeat(10) { i ->
                    try {
                        fetchData(i)
                        fetchCount.incrementAndGet()
                    } catch (e: Exception) {
                        // ✅ 현재 코루틴이 활성 상태인지 확인하고, 아니면 CancellationException 재발생
                        coroutineContext.ensureActive()
                    }
                }
                allTasksCompleted.set(true)
            }

            delay(300)
            job.cancelAndJoin()

            // 검증: ensureActive()로 취소가 제대로 동작함
            allTasksCompleted.get().shouldBeFalse()
            fetchCount.get() shouldBeLessThan 10
        }
    }

    context("2. finally 블록에서 suspend 함수 호출 문제") {
        test("❌ 문제: finally에서 suspend 함수 호출하면 CancellationException 발생") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val resourceCleaned = AtomicBoolean(false)

            val job = coroutineScope.launch {
                try {
                    repeat(10) { i ->
                        fetchData(i)
                    }
                } finally {
                    // ❌ 취소된 상태에서 suspend 함수 호출 -> CancellationException 발생
                    try {
                        cleanUpResources(resourceCleaned) // 이 함수가 실행 안됨
                    } catch (_: CancellationException) {
                        // 리소스 정리 중 취소됨
                    }
                }
            }

            delay(300)
            job.cancelAndJoin()

            // 검증: 리소스 정리가 실행되지 않음
            resourceCleaned.get().shouldBeFalse()
        }

        test("✅ 해결: withContext(NonCancellable)로 리소스 정리") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val resourceCleaned = AtomicBoolean(false)

            val job = coroutineScope.launch {
                try {
                    repeat(10) { i ->
                        fetchData(i)
                    }
                } finally {
                    // ✅ NonCancellable 컨텍스트에서 리소스 정리
                    withContext(NonCancellable) {
                        cleanUpResources(resourceCleaned) // 정상적으로 실행됨
                    }
                }
            }

            delay(300)
            job.cancelAndJoin()

            // 검증: 리소스 정리가 정상적으로 실행됨
            resourceCleaned.get().shouldBeTrue()
        }
    }

    context("3. CoroutineScope 취소 vs 자식 코루틴만 취소") {
        test("❌ 문제: CoroutineScope 전체를 취소하면 재사용 불가") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val newJobRan = AtomicBoolean(false)

            coroutineScope.launch {
                repeat(10) { i ->
                    fetchData(i)
                }
            }

            delay(300)
            coroutineScope.cancel() // ❌ 전체 스코프 취소
            delay(100)

            // ❌ 이후에 새로운 코루틴을 launch 할 수 없음
            try {
                coroutineScope.launch {
                    newJobRan.set(true)
                }.join()
            } catch (_: Exception) {
                // 취소된 스코프에서 launch 불가
            }

            // 검증: 새로운 코루틴이 실행되지 않음
            newJobRan.get().shouldBeFalse()
        }

        test("✅ 해결 방법 1: cancelChildren()로 자식만 취소") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val newJobRan = AtomicBoolean(false)

            coroutineScope.launch {
                repeat(10) { i ->
                    fetchData(i)
                }
            }

            delay(300)
            coroutineScope.coroutineContext.cancelChildren() // ✅ 자식 코루틴만 취소
            delay(100)

            // ✅ 스코프는 살아있어서 새로운 코루틴 실행 가능
            coroutineScope.launch {
                newJobRan.set(true)
            }.join()

            // 검증: 새로운 코루틴이 정상적으로 실행됨
            newJobRan.get().shouldBeTrue()
        }

        test("✅ 해결 방법 2: 새로운 CoroutineScope 생성") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val newJobRan = AtomicBoolean(false)

            coroutineScope.launch {
                repeat(10) { i ->
                    fetchData(i)
                }
            }

            delay(300)
            coroutineScope.cancel() // 전체 스코프 취소
            delay(100)

            // ✅ 새로운 코루틴 스코프 생성 후 launch
            val newCoroutineScope = CoroutineScope(Dispatchers.IO)
            newCoroutineScope.launch {
                newJobRan.set(true)
            }.join()

            // 검증: 새로운 스코프에서 코루틴이 정상적으로 실행됨
            newJobRan.get().shouldBeTrue()
        }
    }

    context("4. 긴 연산/IO 작업에서 취소 체크") {
        test("❌ 문제: blocking 작업은 중단 지점이 없어서 취소 안됨") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val chunkCount = AtomicInteger(0)

            val job = coroutineScope.launch {
                val fakeInput = ByteArray(5) // 청크 5개

                for (i in fakeInput.indices) {
                    chunkCount.incrementAndGet()
                    Thread.sleep(500) // ❌ blocking 작업 — cancel이 걸려도 즉시 중단되지 않음
                }
            }

            delay(100) // 100ms 후 취소
            job.cancelAndJoin()

            // 검증: 취소했지만 모든 청크를 읽음 (문제!)
            chunkCount.get() shouldBe 5 // 모든 청크가 처리됨
        }

        test("✅ 해결: ensureActive()로 주기적으로 취소 상태 확인") {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val chunkCount = AtomicInteger(0)

            val job = coroutineScope.launch {
                val fakeInput = ByteArray(5) // 청크 5개

                for (i in fakeInput.indices) {
                    coroutineContext.ensureActive() // ✅ 취소 상태인지 주기적으로 확인
                    chunkCount.incrementAndGet()
                    Thread.sleep(500)
                }
            }

            delay(100)
            job.cancelAndJoin()

            // 검증: 취소되어 일부 청크만 처리됨
            chunkCount.get() shouldBeLessThan 5
        }
    }
}) {
    companion object {
        private suspend fun fetchData(i: Int) {
            delay(100) // 중단 지점 - 여기서 취소 체크
        }

        private suspend fun cleanUpResources(cleaned: AtomicBoolean) {
            delay(100) // 리소스 정리 시뮬레이션
            cleaned.set(true)
        }
    }
}

