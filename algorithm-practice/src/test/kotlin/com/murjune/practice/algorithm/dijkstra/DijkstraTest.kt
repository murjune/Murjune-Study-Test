package com.murjune.practice.algorithm.dijkstra

import com.murjune.practice.algorithm.common.Edge
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DijkstraTest {
    @Test
    fun `oneToAll - 시작 노드에서 모든 노드들의 최단 경로를 알려준다`() {
        // given
        val dijkstra = Dijkstra(testCase1.n, testCase1.edges)
        val startNode = 1
        // when
        val result = dijkstra.oneToAll(startNode)
        // then
        result shouldBe listOf(0, 2, 3, 1, 2, 4)
    }

    @Test
    fun `AllToOne - 모든 노드에서 시작 노드까지의 최단 경로를 알려준다`() {
        // given
        val dijkstra = Dijkstra(testCase1.n, testCase1.edges)
        val startNode = 3
        // when
        val result = dijkstra.allToOne(startNode)
        // then
        result shouldBe listOf(3, 3, 0, 2, 1, INF)
    }

    @Test
    fun `간선이 중복되면 예외 발생`() {
        shouldThrow<IllegalArgumentException> {

            Dijkstra(
                3,
                listOf(
                    Edge(1, 2, 1),
                    Edge(1, 2, 2)
                )
            )
        }
    }

    private companion object {
        class TestCase(
            val n: Int,
            val edges: List<Edge>
        )

        const val INF = Int.MAX_VALUE
        val testCase1 = TestCase(
            6,
            listOf(
                Edge(1, 2, 2),
                Edge(1, 3, 5),
                Edge(1, 4, 1),
                Edge(2, 3, 3),
                Edge(2, 4, 2),
                Edge(3, 2, 3),
                Edge(3, 6, 5),
                Edge(4, 3, 3),
                Edge(4, 5, 1),
                Edge(5, 3, 1),
                Edge(5, 6, 2)
            )
        )
    }
}