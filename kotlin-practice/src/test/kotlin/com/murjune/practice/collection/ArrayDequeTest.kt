package com.murjune.practice.collection

import io.kotest.matchers.comparables.shouldBeLessThan
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.LinkedList
import kotlin.time.measureTime

class ArrayDequeTest {
    @Nested
    inner class MutableListVsArrayDeque {
        /**
         * 비슷함
         * */
        @Test
        fun `ArrayDeck vs MutableList removeLast 성능 비교`() {
            repeat(10) {
                // given
                val mutableList = MutableList(10_000_000) { it }
                val arrayD = ArrayDeque(mutableList)
                // when
                val arrayDTime =
                    measureTime {
                        while (arrayD.isNotEmpty()) {
                            arrayD.removeLast()
                        }
                    }
                val mutableListTime =
                    measureTime {
                        while (mutableList.isNotEmpty()) {
                            mutableList.removeLast()
                        }
                    }
                // then
                println("ArrayDeque: ${arrayDTime.inWholeMilliseconds}ms")
                println("MutableList: ${mutableListTime.inWholeMilliseconds}ms")
                println("is ArrayDeque faster than MutableList? ${arrayDTime.inWholeMilliseconds < mutableListTime.inWholeMilliseconds}")
//        arrayDTime.inWholeMilliseconds shouldBeLessThan mutableListTime.inWholeMilliseconds
            }
        }

        /**
         * MutableList 가 조금 더 빠른듯 (거의 비슷함)
         * */
        @Test
        fun `ArrayDeque vs MutableList add 성능 비교`() {
            repeat(10) {
                // given
                val mutableList: MutableList<Int> = ArrayList(10_000_000)
                val arrayD = ArrayDeque(mutableList)
                // when
                val mutableListTime =
                    measureTime {
                        repeat(10_000_000) { mutableList.add(it) }
                    }
                val arrayDTime =
                    measureTime {
                        repeat(10_000_000) { arrayD.add(it) }
                    }
                // then
                println("ArrayDeque: ${arrayDTime.inWholeMilliseconds}ms")
                println("MutableList: ${mutableListTime.inWholeMilliseconds}ms")
                println("is ArrayDeque faster than MutableList? ${arrayDTime.inWholeMilliseconds < mutableListTime.inWholeMilliseconds}")
                // arrayDTime.inWholeMilliseconds shouldBeLessThan mutableListTime.inWholeMilliseconds
            }
        }
    }

    @Nested
    inner class LinkedListVsArrayDeque() {
        /**
         * (대부분) ArrayDeque 가 addFirst() 연산이 압도적으로 빠르다.
         * */
        @Test
        fun `ArrayDeck vs LinkedList addFirst 성능 비교`() {
            // given
            val linkedList = LinkedList<Int>()
            val arrayD = ArrayDeque(linkedList)
            // when
            val linkedListTime =
                measureTime {
                    repeat(10_000_000) { linkedList.addFirst(it) }
                }
            val arrayDTime =
                measureTime {
                    repeat(10_000_000) { arrayD.addFirst(it) }
                }
            // then
            println("ArrayDeque: ${arrayDTime.inWholeMilliseconds}ms")
            println("LinkedList: ${linkedListTime.inWholeMilliseconds}ms")
            println("is ArrayDeque faster than LinkedList? ${arrayDTime.inWholeMilliseconds < linkedListTime.inWholeMilliseconds}")
//            arrayDTime.inWholeMilliseconds shouldBeLessThan linkedListTime.inWholeMilliseconds
        }

        /**
         * (대부분) ArrayDeque 가 removeFirst() 연산이 대체로 빠르다 비슷한듯?
         * */
        @Test
        fun `ArrayDeque vs LinkedList removeFirst 성능 비교`() {
            repeat(10) {
                // given
                val mutableList = MutableList(5_000_000) { it }
                val q = LinkedList(mutableList)
                val arrayD = ArrayDeque(mutableList)
                // when
                val arrayDTime =
                    measureTime {
                        repeat(5_000_000) { arrayD.removeFirst() }
                    }
                val qTime =
                    measureTime {
                        repeat(5_000_000) { q.poll() }
                    }
                // then
                println("ArrayDeque: ${arrayDTime.inWholeMilliseconds}ms")
                println("LinkedList: ${qTime.inWholeMilliseconds}ms")
                println("is ArrayDeque faster than LinkedList? ${arrayDTime.inWholeMilliseconds < qTime.inWholeMilliseconds}")
//                arrayDTime.inWholeMilliseconds shouldBeLessThan qTime.inWholeMilliseconds
            }
        }
    }
}