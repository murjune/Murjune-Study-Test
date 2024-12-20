package com.murjune.practice.algorithm.datastructure.bst

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class RBTreeNodeTest {

    @Test
    @DisplayName(
        """
    노드를 오른쪽으로 회전할 수 있다
    
    (회전 전)
         2
        / \
       1   3
       
    (회전 후)
    
        1
         \
          2
           \
            3
    """
    )
    fun `노드를 오른쪽으로 회전할 수 있다`() {
        // given
        val root = RBTreeNode(2).also { p ->
            p.left = RBTreeNode(1, parent = p)
            p.right = RBTreeNode(3, parent = p)
        }
        // when
        root.rotateRight()
        // then
        val newRoot = root.root()
        val right = newRoot.right
        val left = newRoot.left
        val rightRight = right?.right
        val rightLeft = right?.left
        newRoot.key shouldBe 1
        right?.key shouldBe 2
        left shouldBe null
        rightRight?.key shouldBe 3
        rightLeft shouldBe null
    }

    @Test
    @DisplayName(
        """
    노드를 왼쪽으로 회전할 수 있다
    
    (회전 전)
         2
        / \
       1   3
       
    (회전 후)
        
         3
        /
       2
      /
    1
    """
    )
    fun `노드를 왼쪽으로 회전할 수 있다`() {
        // given
        val root = RBTreeNode(2).also { p ->
            p.left = RBTreeNode(1, parent = p)
            p.right = RBTreeNode(3, parent = p)
        }
        // when
        root.rotateLeft()
        // then
        val newRoot = root.root()
        val right = newRoot.right
        val left = newRoot.left
        val leftRight = left?.right
        val leftLeft = left?.left
        newRoot.key shouldBe 3
        left?.key shouldBe 2
        right shouldBe null
        leftRight?.key shouldBe null
        leftLeft?.key shouldBe 1
    }

    @Test
    fun `노드의 왼쪽 자식이 없으면 오른쪽 회전할 수 없다`() {
        // given
        val root = RBTreeNode(2).also { p ->
            p.left = RBTreeNode(1, parent = p)
        }
        // when
        shouldThrow<IllegalArgumentException> {
            root.rotateLeft()
        }
    }

    @Test
    fun `노드의 오른쪽 자식이 없으면 왼쪽 회전할 수 없다`() {
        // given
        val root = RBTreeNode(2).also { p ->
            p.right = RBTreeNode(3, parent = p)
        }
        // when
        shouldThrow<IllegalArgumentException> {
            root.rotateRight()
        }
    }

    @Test
    fun `root 노드를 찾을 수 있다`() {
        // given
        val root = RBTreeNode(2)
        val left = RBTreeNode(1, parent = root).also { root.left = it }
        val leftRight = RBTreeNode(3, parent = left).also { left.right = it }
        // when
        val newRoot = leftRight.root()
        // then
        newRoot.key shouldBe 2
    }
}