package com.murjune.practice.algorithm.datastructure.bst

class RedBlackTree<T : Comparable<T>>(
    nodes: List<T> = mutableListOf()
) {
    private var root: RBTreeNode<T>? = null

    init {
        nodes.forEach(::insert)
    }

    fun insert(key: T) {
        if (root == null) {
            root = RBTreeNode(key).apply {
                color = RBTreeNode.Color.BLACK
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

    private fun nilNode(parent: RBTreeNode<T>): RBTreeNode<T> {
        return RBTreeNode(null, parent = parent, color = RBTreeNode.Color.BLACK)
    }

    private fun insert(parent: RBTreeNode<T>, key: T) {
        val parentKey: T? = parent.key
        // NIL 노드일 경우
        if (parentKey == null) {
            parent.key = key
            parent.color = RBTreeNode.Color.RED
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

    private tailrec fun fixInsertion(currentNode: RBTreeNode<T>) {
        // Case 1: 현재 노드가 root 인 경우 root 를 BLACK 으로 변경
        if (currentNode == root) {
            currentNode.color = RBTreeNode.Color.BLACK
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
        if (parentNode.color == RBTreeNode.Color.BLACK) return
        // Case 2: parent, uncle red 인 경우
        if (uncleNode.color == RBTreeNode.Color.RED) {
            // parent, uncle 을 BLACK 으로 변경, grandParent 를 RED 로 변경
            parentNode.color = RBTreeNode.Color.BLACK
            uncleNode.color = RBTreeNode.Color.BLACK
            grandParent.color = RBTreeNode.Color.RED
            // grandParent 를 기준으로 다시 fixInsertion 을 수행
            return fixInsertion(grandParent)
        }
        // parent red, uncle black 인 경우
        // parent 가 grandParent 의 left/right 인 경우
        // Case 3) parent 의 left/right 가 currentNode 인 경우
        if (isCurrentLeft == isParentLeft) {
            // 먼저, parent 와 grandParent 의 color 를 바꾼다
            parentNode.color = RBTreeNode.Color.BLACK
            grandParent.color = RBTreeNode.Color.RED
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

    private fun rotateLeft(pivot: RBTreeNode<T>) {
        pivot.rotateLeft()
        root = pivot.root()
    }

    private fun rotateRight(pivot: RBTreeNode<T>) {
        pivot.rotateRight()
        root = pivot.root()
    }

    fun search(key: T): Boolean {
        return search(root ?: return false, key)
    }

    private tailrec fun search(parent: RBTreeNode<T>, key: T): Boolean {
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
        return isRootBlack() && isRedParentHasOnlyBlackChildren(root)
                && (isOneSideBlackHeightEqual(root) != -1)
                && isAllLeavesNil(root)
    }

    private fun isAllLeavesNil(node: RBTreeNode<T>?): Boolean {
        if (node?.key == null) {
            val isBlack = node?.color == RBTreeNode.Color.BLACK
            val isLeftNull = node?.left?.key == null
            val isRightNull = node?.right?.key == null
            return isBlack && isLeftNull && isRightNull
        }
        if (node.left == null || node.right == null) return false
        return isAllLeavesNil(node.left) && isAllLeavesNil(node.right)
    }

    private fun isOneSideBlackHeightEqual(parent: RBTreeNode<T>?): Int {
        if (parent?.key == null) return 1
        val left = isOneSideBlackHeightEqual(parent.left)
        val right = isOneSideBlackHeightEqual(parent.right)
        if (left == -1 || right == -1 || left != right) {
            println("부모 노드 ${parent.key}의 서브트리의 높이가 같지 않습니다.")
            return -1
        }
        return if (parent.color == RBTreeNode.Color.BLACK) left + 1 else left
    }

    private fun isRootBlack(): Boolean {
        return root?.color == RBTreeNode.Color.BLACK
    }

    private fun isRedParentHasOnlyBlackChildren(parent: RBTreeNode<T>?): Boolean {
        if (parent?.key == null) return true
        if (parent.color == RBTreeNode.Color.RED) {
            if (parent.left?.color == RBTreeNode.Color.RED || parent.right?.color == RBTreeNode.Color.RED) {
                println("부모 노드 ${parent.key}의 자식 노드가 모두 블랙이 아닙니다.")
                return false
            }
        }
        return isRedParentHasOnlyBlackChildren(parent.left) && isRedParentHasOnlyBlackChildren(
            parent.right
        )
    }

    fun print() {
        println("Red-Black Tree:")
        printNode(root, "", false)
    }

    private fun printNode(node: RBTreeNode<T>?, prefix: String, isLeft: Boolean) {
        if (node?.key == null) return
        if (node == root) println("Root[${node.key}] (${node.color})")
        else println("$prefix${if (isLeft) "├── L " else "└── R "}[${node.key}] (${node.color})")
        printNode(node.left, "$prefix${if (isLeft) "│   " else "    "}", true)
        printNode(node.right, "$prefix${if (isLeft) "│   " else "    "}", false)
    }
}

class RBTreeNode<T : Comparable<T>>(
    var key: T?,
    var color: Color = Color.RED,
    var parent: RBTreeNode<T>? = null,
    var left: RBTreeNode<T>? = null,
    var right: RBTreeNode<T>? = null
) {
    enum class Color {
        RED, BLACK
    }

    fun rotateLeft() {
        val pivot = this
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
    }

    fun rotateRight() {
        val pivot = this
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
    }

    fun root(): RBTreeNode<T> {
        var node = this
        while (node.parent != null) {
            node = node.parent!!
        }
        return node
    }

    override fun toString(): String {
        return "Node[$color, left=${left?.key}, key=${key ?: "NIL"}, right=${right?.key}]"
    }
}

///**
// * AVL 트리의 균형을 확인하는 방법
// * 노드의 왼쪽 서브트리와 오른쪽 서브트리의 높이 차이가 1 이하인지 확인
// * */
//private fun isBalanced(node: Node<T>?): Boolean {
//    if (node == null) return true
//    val leftHeight = height(node.left)
//    val rightHeight = height(node.right)
//    val diff = leftHeight - rightHeight
//    return diff in -1..1 && isBalanced(node.left) && isBalanced(node.right)
//}
//
//private fun height(node: Node<T>?): Int {
//    if (node == null) return 0
//    return 1 + maxOf(height(node.left), height(node.right))
//}