package com.murjune.practice.algorithm.datastructure.bst

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

class RedBlackTreeTest {

    @Test
    fun `트리에 노드를 삽입할 수 있다`() {
        // given
        val bst = RedBlackTree<Int>()
        // when & then
        shouldNotThrowAny {
            bst.insert(1)
            bst.insert(2)
            bst.insert(3)
            bst.insert(4)
            bst.insert(5)
        }
//        bst.printTree()
    }

    @Test
    fun `중복된 노드는 삽입할 수 없다`() {
        // given
        val bst = RedBlackTree<Int>()
        // when & then
        shouldThrow<IllegalArgumentException> {
            bst.insert(1)
            bst.insert(1)
        }
    }

    @Test
    fun `삽입 시 트리의 균형을 유지한다`() {
        // given
        val bst = RedBlackTree<Int>()
        // when & then
        shouldNotThrowAny {
            bst.insert(1)
            bst.isBalanced().shouldBeTrue()
            bst.insert(2)
            bst.isBalanced().shouldBeTrue()
            bst.insert(3)
            bst.isBalanced().shouldBeTrue()
            bst.insert(4)
            bst.isBalanced().shouldBeTrue()
            bst.insert(5)
            bst.isBalanced().shouldBeTrue()
        }
    }

    // 테스트 케이스 출처 : https://www.youtube.com/watch?v=2MdsebfJOyM
    //
    @Test
    fun `삽입 시 트리의 균형을 유지한다2`() {
        // given
        with(RedBlackTree<Int>(listOf(10, 20, 50, 30))) {
            // when & then
            shouldNotThrowAny {
//                printTree()
                isBalanced().shouldBeTrue()
                insert(80)
//                printTree()
                isBalanced().shouldBeTrue()
                insert(40)
//                printTree()
                isBalanced().shouldBeTrue()
                insert(35)
//                printTree()
                isBalanced().shouldBeTrue()
                insert(25)
//                printTree()
                isBalanced().shouldBeTrue()
            }
        }
    }

    @Test
    fun `삽입된 노드를 검색할 수 있다`() {
        // given
        val keys = listOf(3, 4, 5)
        val bst = RedBlackTree<Int>(keys)
        // then
        keys.forEach {
            bst.search(it).shouldBeTrue()
        }
//        bst.printTree()
    }

    @Test
    fun `삽입하지 않은 노드는 검색할 수 없다`() {
        // given
        val keys = listOf(1, 2, 3, 4, 5)
        val bst = RedBlackTree<Int>(keys)
        // then
        bst.search(6).shouldBeFalse()
    }
}