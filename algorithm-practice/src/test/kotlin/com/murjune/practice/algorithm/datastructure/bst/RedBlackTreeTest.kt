package com.murjune.practice.algorithm.datastructure.bst

import io.kotest.assertions.throwables.shouldNotThrowAny
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

    @Test
    fun `삽입된 노드를 검색할 수 있다`() {
        // given
        val keys = listOf(3, 4, 5)
        val bst = RedBlackTree<Int>(keys)
        // then
        keys.forEach {
            bst.search(it).shouldBeTrue()
        }
        bst.printTree()
        bst.isBalanced().shouldBeTrue()
    }

    @Test
    fun `삽입하지 않은 노드는 검색할 수 없다`() {
        // given
        val keys = listOf(1, 2, 3, 4, 5)
        val bst = RedBlackTree<Int>(keys)
        // then
        bst.search(6).shouldBeFalse()
        bst.isBalanced().shouldBeTrue()
    }
}