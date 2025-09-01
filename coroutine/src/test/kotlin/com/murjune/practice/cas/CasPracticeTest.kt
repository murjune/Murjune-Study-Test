package com.murjune.practice.cas

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Objects
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class CasPracticeTest : BehaviorSpec(
    {
        Given("상호 배제 만족하지 못하는 경우") {
            When("멀티스레드 환경에서 공유 자원 접근") {
                var counter = 0

                List(300) {
                    launch(Dispatchers.IO) { // 멀티 스레드 환경
                        delay(10)
                        counter = counter + 1 // 임계 영역
                    }
                }.joinAll()

                Then("Race Condition 발생") {
                    counter shouldNotBe 0 // 실패함
                }
            }

            When("UnSafeSpinLock 활용한 동기화") { // ⚠️ 주의). 해당 테스트는 CPU 자원을 많이 소모하여 맥북이 바보가 될 수 있음
                var counter = 0
                val lock = NonSafeSpinLock()

                List(30) {
                    launch(Dispatchers.IO) { // 멀티 스레드 환경
                        lock.lock() // 락 획득
                        delay(10)
                        try {
                            counter = counter + 1 // 임계 영역
                        } finally {
                            lock.unLock() // 락 해제
                        }
                    }
                }.joinAll()

                Then("공유 자원에 대한 동기화가 보장되지 않는다") {
                    counter shouldNotBe 30 // 실패함
                }
            }
        }

        Given("락 기반 동기화를 통해 상호 배제 만족하는 경우") {
            When("synchronized 키워드 활용한 동기화") {
                var counter = 0
                val lock = Any()

                List(100) {
                    thread {
                        synchronized(lock) { // Lock 기반 동기화
                            Thread.sleep(10)
                            counter = counter + 1 // 임계 영역
                        }
                    }
                }.forEach(Thread::join)

                Then("공유 자원에 대한 동기화가 보장된다") {
                    counter shouldBe 100 // 성공함
                }
            }

            When("ReentrantLock 을 활용한 동기화") {
                var counter = 0
                val lock = ReentrantLock()

                List(100) {
                    thread {
                        lock.withLock { // Lock 기반 동기화
                            Thread.sleep(10)
                            counter = counter + 1 // 임계 영역
                        }
                    }
                }.forEach(Thread::join)

                Then("공유 자원에 대한 동기화가 보장된다") {
                    counter shouldBe 100 // 성공함
                }
            }

            When("Mutex 를 활용한 동기화") {
                var counter = 0
                val mutex = Mutex()

                List(100) {
                    launch(Dispatchers.IO) { // 멀티 스레드 환경
                        mutex.withLock { // Lock 기반 동기화
                            delay(10)
                            counter = counter + 1 // 임계 영역
                        }
                    }
                }.joinAll()

                Then("공유 자원에 대한 동기화가 보장된다") {
                    counter shouldBe 100 // 성공함
                }
            }
        }

        Given("CAS 기반 스핀락 기반 동기화를 통해 상호 배제 만족하는 경우") {
            When("AtomicInteger 을 활용한 동기화") {
                val counter = AtomicInteger(0)

                List(1000) {
                    launch(Dispatchers.IO) { // 멀티 스레드 환경
                        delay(100)
                        counter.incrementAndGet() // 원자적 연산
                    }
                }.joinAll()

                Then("공유 자원에 대한 동기화가 보장된다") {
                    counter.get() shouldBe 1000 // 성공함
                }
            }

            // ⚠️ 주의). 해당 테스트는 CPU 자원을 많이 소모하여 맥북이 바보가 될 수 있음
            When("SpinLock을 활용한 동기화") {
                val spinLock = SpinLock()
                var counter = 0

                List(50) {
                    launch(Dispatchers.IO) { // 멀티 스레드 환경
                        spinLock.lock()
                        delay(10)
                        try {
                            counter = counter + 1 // 임계 영역
                        } finally {
                            spinLock.unLock()
                        }
                    }
                }.joinAll()

                Then("공유 자원에 대한 동기화가 보장된다") {
                    counter shouldBe 50
                }
            }
        }
    }
)