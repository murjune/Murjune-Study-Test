package com.murjune.practice.collection

import io.kotest.matchers.shouldBe
import java.util.PriorityQueue
import kotlin.test.Test

class PriorityQueueTest {
    @Test
    fun `Comparator - Node 의 cost 순으로 PQ에 저장`() {
        // given
        data class Node(val v: Int, val cost: Int)

        val pq = PriorityQueue<Node>(
            compareBy { it.cost }
        ).apply {
            add(Node(2, 4))
            add(Node(1, 3))
            add(Node(3, 5))
        }
        // when
        val actual = buildList<Node> {
            while (pq.isNotEmpty()) {
                add(pq.remove())
            }
        }
        // then
        val expect = listOf(Node(1, 3), Node(2, 4), Node(3, 5))
        actual shouldBe expect
    }

    @Test
    fun `Comparable - Node 의 cost 순으로 PQ에 저장`() {
        // given
        data class Node(val v: Int, val cost: Int) : Comparable<Node> {
            override fun compareTo(other: Node): Int {
                return cost.compareTo(other.cost)
            }
        }

        val pq = PriorityQueue<Node>().apply {
            add(Node(2, 4))
            add(Node(1, 3))
            add(Node(3, 5))
        }
        // when
        val actual = buildList<Node> {
            while (pq.isNotEmpty()) {
                add(pq.remove())
            }
        }
        // then
        val expect = listOf(Node(1, 3), Node(2, 4), Node(3, 5))
        actual shouldBe expect
    }
}