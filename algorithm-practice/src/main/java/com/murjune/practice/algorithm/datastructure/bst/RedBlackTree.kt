package com.murjune.practice.algorithm.datastructure.bst

class RedBlackTree<T : Comparable<T>>(
    nodes: List<T> = mutableListOf()
) {
    private var root: Node<T>? = null

    init {
        nodes.forEach(::insert)
    }

    fun insert(key: T) {
        val rootNode = root
        if (rootNode == null) {
            root = Node(key)
            return
        }
        insert(rootNode, key)
    }

    private fun insert(parent: Node<T>, key: T) {
        val parentKey: T? = parent.key
        requireNotNull(parentKey) {
            "parent 의 key 는 null 일 수 없습니다."
        }
        if (key < parentKey) {
            val left = parent.left
            if (left == null) {
                parent.left = Node(key)
                return
            }
            return insert(left, key)
        }
        val right = parent.right
        if (right == null) {
            parent.right = Node(key)
            return
        }
        insert(right, key)
    }

    fun search(key: T): Boolean {
        return search(root ?: return false, key)
    }

    private tailrec fun search(parent: Node<T>, key: T): Boolean {
        val parentKey: T? = parent.key
        requireNotNull(parentKey) {
            "parent 의 key 는 null 일 수 없습니다."
        }

        if (key == parentKey) return true

        if (key < parentKey) {
            val left = parent.left ?: return false
            return search(left, key)
        }

        val right = parent.right ?: return false
        return search(right, key)
    }

    fun printTree() {

    }
}

data class Node<T : Comparable<T>>(
    var key: T?,
    var left: Node<T>? = null,
    var right: Node<T>? = null
)