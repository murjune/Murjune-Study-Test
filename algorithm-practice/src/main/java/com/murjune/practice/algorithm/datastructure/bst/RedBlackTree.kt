package com.murjune.practice.algorithm.datastructure.bst

class RedBlackTree<T : Comparable<T>>(
    nodes: List<T> = mutableListOf()
) {
    private var root: Node<T>? = null

    init {
        nodes.forEach(::insert)
    }

    fun insert(key: T) {
        if (root == null) {
            root = Node(key).apply {
                color = Node.Color.BLACK
                left = nilNode(this)
                right = nilNode(this)
            }
            return
        }
        insert(
            parent = requireNotNull(root) { "root 는 null 일 수 없습니다." },
            key
        )
    }

    private fun nilNode(parent: Node<T>): Node<T> {
        return Node(null, parent = parent, color = Node.Color.BLACK)
    }

    private fun insert(parent: Node<T>, key: T) {
        val parentKey: T? = parent.key
        // NIL 노드일 경우
        if (parentKey == null) {
            parent.key = key
            parent.color = Node.Color.RED
            parent.left = nilNode(parent)
            parent.right = nilNode(parent)
            return fixInsertion(parent)
        }
        require(parentKey != key) {
            "중복된 key 는 삽입할 수 없습니다."
        }
        if (key < parentKey) {
            val left = requireNotNull(parent.left) {
                "$parent 의 left 는 null 일 수 없습니다."
            }
            return insert(left, key)
        }
        val right = requireNotNull(parent.right) {
            "$parent 의 right 는 null 일 수 없습니다."
        }
        insert(right, key)
    }

    private tailrec fun fixInsertion(currentNode: Node<T>) {
        // Case 1: 현재 노드가 root 인 경우 root 를 BLACK 으로 변경
        if (currentNode == root) {
            currentNode.color = Node.Color.BLACK
            return
        }
        val parentNode = currentNode.parent
        requireNotNull(parentNode) {
            "$currentNode 의 parent 는 null 일 수 없습니다."
        }
        val grandParent = parentNode.parent ?: return
        val isParentLeft = parentNode == grandParent.left
        val isCurrentLeft = currentNode == parentNode.left
        val uncleNode = if (isParentLeft) grandParent.right else grandParent.left
        requireNotNull(uncleNode) {
            "$currentNode 의 uncleNode 는 null 일 수 없습니다."
        }

        // parent 가 black 이면 문제 없음
        if (parentNode.color == Node.Color.BLACK) return
        // Case 2: parent, uncle red 인 경우
        if (uncleNode.color == Node.Color.RED) {
            // parent, uncle 을 BLACK 으로 변경, grandParent 를 RED 로 변경
            parentNode.color = Node.Color.BLACK
            uncleNode.color = Node.Color.BLACK
            grandParent.color = Node.Color.RED
            // grandParent 를 기준으로 다시 fixInsertion 을 수행
            return fixInsertion(grandParent)
        }
        // parent red, uncle black 인 경우
        // parent 가 grandParent 의 left/right 인 경우
        // Case 3) parent 의 left/right 가 currentNode 인 경우
        if (isCurrentLeft == isParentLeft) {
            // 먼저, parent 와 grandParent 의 color 를 바꾼다
            parentNode.color = Node.Color.BLACK
            grandParent.color = Node.Color.RED
            // 그리고, grandParent 를 기준으로 rotateLeft/rotateRight 를 수행
            if (isParentLeft) rotateRight(grandParent) else rotateLeft(grandParent)
            return
        }
        // Case 4) parent 의 right/left 가 currentNode 인 경우
        // 그리고, parent 를 기준으로 rotateLeft/rotateRight 를 수행
        if (isParentLeft) rotateLeft(parentNode) else rotateRight(parentNode)
        // 그리고, parent 를 기준으로 fixInsertion 을 수행
        return fixInsertion(parentNode)
    }

    private fun rotateLeft(pivot: Node<T>) {
        val parentOfPivot = pivot.parent
        val rightChild = requireNotNull(pivot.right) {
            "rotateLeft 호출 시 ${pivot}의 rightChild가 null일 수 없습니다."
        }
        val leftChildOfRight = rightChild.left
        // 1) pivot 부모 자식 관계
        pivot.right = leftChildOfRight
        pivot.parent = rightChild
        // 2) right 부모 자식 관계
        rightChild.left = pivot
        rightChild.parent = parentOfPivot
        // 3) parentOfPivot 의 자식 관계
        if (parentOfPivot?.left == pivot) {
            parentOfPivot.left = rightChild
        } else {
            parentOfPivot?.right = rightChild
        }
        // 4) leftChildOfRight 의 부모 관계
        leftChildOfRight?.parent = pivot
        // root 가 pivot 이면 root 를 right 로 변경
        if (root == pivot) root = rightChild
    }

    private fun rotateRight(pivot: Node<T>) {
        val parentOfPivot = pivot.parent
        val leftChild = requireNotNull(pivot.left) {
            "rotateRight 호출 시 ${pivot}의 leftChild가 null일 수 없습니다."
        }
        val rightChildOfLeft = leftChild.right
        // 1) pivot 부모 자식 관계
        pivot.left = rightChildOfLeft
        pivot.parent = leftChild
        // 2) left 부모 자식 관계
        leftChild.right = pivot
        leftChild.parent = parentOfPivot
        // 3) parentOfPivot 의 자식 관계
        if (parentOfPivot?.left == pivot) {
            parentOfPivot.left = leftChild
        } else {
            parentOfPivot?.right = leftChild
        }
        // 4) rightChildOfLeft 의 부모 관계
        rightChildOfLeft?.parent = pivot
        // root 가 pivot 이면 root 를 left 로 변경
        if (root == pivot) root = leftChild
    }

    fun search(key: T): Boolean {
        return search(root ?: return false, key)
    }

    private tailrec fun search(parent: Node<T>, key: T): Boolean {
        val parentKey: T = parent.key ?: return false // NIL 노드

        if (key == parentKey) return true

        if (key < parentKey) {
            val left = parent.left ?: return false
            return search(left, key)
        }

        val right = parent.right ?: return false
        return search(right, key)
    }

    fun isBalanced(): Boolean {
        return isBalanced(root)
    }

    /**
     * 노드의 왼쪽 서브트리와 오른쪽 서브트리의 높이 차이가 1 이하인지 확인
     * */
    private fun isBalanced(node: Node<T>?): Boolean {
        if (node == null) return true
        val leftHeight = height(node.left)
        val rightHeight = height(node.right)
        val diff = leftHeight - rightHeight
        return diff in -1..1 && isBalanced(node.left) && isBalanced(node.right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + maxOf(height(node.left), height(node.right))
    }


    fun printTree() {
        println("Red-Black Tree:")
        printNode(root, "", false)
    }

    private fun printNode(node: Node<T>?, prefix: String, isLeft: Boolean) {
        if (node?.key == null) return
        if (node == root) println("Root[${node.key}] (${node.color})")
        else println("$prefix${if (isLeft) "├── L " else "└── R "}[${node.key}] (${node.color})")
        printNode(node.left, "$prefix${if (isLeft) "│   " else "    "}", true)
        printNode(node.right, "$prefix${if (isLeft) "│   " else "    "}", false)
    }
}

class Node<T : Comparable<T>>(
    var key: T?,
    var color: Color = Color.RED,
    var parent: Node<T>? = null,
    var left: Node<T>? = null,
    var right: Node<T>? = null
) {
    enum class Color {
        RED, BLACK
    }

    override fun toString(): String {
        return "Node[$color, left=${left?.key}, key=${key ?: "NIL"}, right=${right?.key}]"
    }
}
