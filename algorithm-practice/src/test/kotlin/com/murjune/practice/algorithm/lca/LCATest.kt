package com.murjune.practice.algorithm.lca

import com.murjune.practice.algorithm.common.Edge
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Timeout
import kotlin.test.Test

/**
 * LCA - 트리에서 특정 Node 2 개 의 공통 조상 노드를 찾는다
 * */
class LCATest {

    //  1
    //   \
    //    2(n1)
    //     \
    //      3(n2)
    @Test
    @Timeout(1)
    fun `TestCase 1`() {
        // given
        val lca = LCA(root = 1, n = 3, arrayOf(Edge(1, 2), Edge(2, 3)))
        val improvedLCA = ImprovedLCA(root = 1, n = 3, arrayOf(Edge(1, 2), Edge(2, 3)))
        val n1 = 2
        val n2 = 3
        // when
        val res1 = lca.lca(n1, n2)
        val res2 = improvedLCA.lca(n1, n2)
        // then
        assertSoftly {
            res1 shouldBe 2
            res2 shouldBe 2
        }
    }

    //       1
    //     /    \
    //   2(n1)   3(n2)
    @Test
    fun `TestCase 2`() {
        // given
        val lca = LCA(root = 1, n = 3, arrayOf(Edge(1, 2), Edge(1, 3)))
        val improvedLCA = ImprovedLCA(root = 1, n = 3, arrayOf(Edge(1, 2), Edge(1, 3)))
        val n1 = 2
        val n2 = 3
        // when
        val res1 = lca.lca(n1, n2)
        val res2 = improvedLCA.lca(n1, n2)
        // then
        assertSoftly {
            res1 shouldBe 1
            res2 shouldBe 1
        }
    }

    //       1
    //     /    \
    //   2(n1)   3
    //            \
    //             4(n2)
    @Test
    fun `test3`() {
        // given
        val lca = LCA(root = 1, n = 4, arrayOf(Edge(1, 2), Edge(1, 3), Edge(3, 4)))
        val improvedLCA = ImprovedLCA(root = 1, n = 4, arrayOf(Edge(1, 2), Edge(1, 3), Edge(3, 4)))
        val n1 = 2
        val n2 = 4
        // when
        val res1 = lca.lca(n1, n2)
        val res2 = improvedLCA.lca(n1, n2)
        // then
        assertSoftly {
            res1 shouldBe 1
            res2 shouldBe 1
        }
    }


    @Test
    fun `노드의 개수는 1 개 이상이다`() {
        shouldThrow<IllegalArgumentException> {
            LCA(1, 0, emptyArray())
        }
    }

    @Test
    fun `LCA 는 트리 자료구조에서만 사용 가능한 알고리즘이다`() {
        shouldThrow<IllegalArgumentException> {
            LCA(1, 2, arrayOf(Edge(1, 2), Edge(2, 3)))
        }
        shouldNotThrow<IllegalArgumentException> {
            LCA(1, 3, arrayOf(Edge(1, 2), Edge(2, 3)))
        }
    }
}