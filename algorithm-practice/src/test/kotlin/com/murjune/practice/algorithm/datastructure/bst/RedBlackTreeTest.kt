package com.murjune.practice.algorithm.datastructure.bst

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
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

    @Test
    fun `root 노드를 삭제시에도 균형을 유지한다`() {
        // given
        val nodes = listOf(10)
        val bst = RedBlackTree<Int>(nodes)
        // when
        bst.delete(10)
        // then
        bst.print()
        bst.isBalanced().shouldBeTrue()
    }

    /**
     * red 삭제
     *
     * Root[20] (BLACK)
     *     ├── L [10] (RED)
     *     └── R [30] (RED)
     *
     * (10) 삭제
     *
     * Root[20] (BLACK)
     *     └── R [30] (RED)
     */
    @Test
    fun `자식이 red인 노드를 삭제시에도 균형을 유지한다`() {
        // given
        val nodes = listOf(10, 20, 30)
        val bst = RedBlackTree<Int>(nodes)
        // when
        bst.delete(10)
        // then
        bst.isBalanced().shouldBeTrue()
    }

    /**
     * 자식 1개인 black 삭제 + root 삭제 case
     *
     * Root[10] (BLACK)
     *     └── R [20] (RED)
     *
     * (10) 삭제
     *
     * Root[20] (BLACK)
     *
     */
    @Test
    fun `root 의 자식이 1개일 때, root를 삭제 시 균형을 유지한다`() {
        // given
        val nodes = listOf(10, 20)
        val bst = RedBlackTree<Int>(nodes)
        // when
        bst.print()
        bst.delete(10)
        bst.print()
        // then
        bst.isBalanced().shouldBeTrue()
    }

    /**
     * 자식 2개인 black 삭제 + root 삭제 case
     *
     * Root[20] (BLACK)
     *     ├── L [10] (RED)
     *     └── R [30] (RED)
     *
     * (10) 삭제
     *
     * Root[20] (BLACK)
     *
     */
    @Test
    fun `root 의 자식이 2개일 때, root를 삭제 시 균형을 유지한다`() {
        // given
        val nodes = listOf(10, 20, 30)
        val bst = RedBlackTree<Int>(nodes)
        // when
        bst.print()
        bst.delete(20)
        bst.print()
        // then
        bst.isBalanced().shouldBeTrue()
    }

    /**
     *  case 4
     *
     *  Root[5] (BLACK)
     *     ├── L [3] (BLACK)
     *     │   ├── L [1] (RED)
     *     └── R [10] (BLACK)
     *         └── R [15] (RED)
     *
     *  삭제할 노드 : 3
     * */
    @Test
    fun `red-and-black 이 발생하는 case 에도, 균형을 유지한다`() {
        // given
        val nodes = listOf(5, 3, 10, 1, 15, 8, 4)
        val bst = RedBlackTree<Int>(nodes)
        bst.print()
        // when
//        bst.delete(3)
        // then
        bst.isBalanced().shouldBeTrue()
    }

    /**
     *  case 4
     *
     *  Root[5] (BLACK)
     *     ├── L [3] (BLACK)
     *     │   ├── L [1] (RED)
     *     └── R [10] (BLACK)
     *         └── R [15] (RED)
     *
     *  삭제할 노드 : 3
     * */
    @Test
    fun `삭제 이상 case 4 - doubly black 의 형제가 black, 형제의 오른쪽 자식이 red인 경우에도 균형을 유지한다`() {
        // given
        val nodes = listOf(5, 3, 10, 1, 15, 8)
        val bst = RedBlackTree<Int>(nodes)
        bst.print()
        // when
//        bst.delete(3)
        // then
        bst.isBalanced().shouldBeTrue()
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
        shouldNotThrowAny {
            keys.forEach {
                bst.search(it)
            }
        }
//        bst.printTree()
    }

    @Test
    fun `삽입하지 않은 노드는 검색할 수 없다`() {
        // given
        val keys = listOf(1, 2, 3, 4, 5)
        val bst = RedBlackTree<Int>(keys)
        // then
        shouldThrow<IllegalArgumentException> {
            bst.search(6)
        }
    }
}