package com.murjune.practice.algorithm.dijkstra

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DijkstraTest {
    @Test
    fun `startNode 와 간선 정보들을 지정하면 각 노드들의 최단 경로를 알려준다`() {
        // given
        val dijkstra = dijstra(testCase1)
        // when
        val result = dijkstra.shortDistances()
        // then
        result shouldBe listOf(0, 2, 3, 1, 2, 4)
    }

    @Test
    fun `간선이 중복되면 예외 발생`() {
        shouldThrow<IllegalArgumentException> {

            Dijkstra(
                3, 2,
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
            val startNode: Int,
            val edges: List<Edge>
        )

        val testCase1 = TestCase(
            6,
            1,
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

        fun dijstra(testCase: TestCase) = Dijkstra(testCase.n, testCase.startNode, testCase.edges)
    }
}