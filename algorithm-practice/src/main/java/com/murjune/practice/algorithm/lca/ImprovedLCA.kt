package com.murjune.practice.algorithm.lca

import com.murjune.practice.algorithm.common.Edge
import kotlin.math.log2

// 기존 LCA 코드를 개선한 코드
// M 개의 쿼리를 처리할 때 O(MLogN) 시간복잡도를 가진다
// 그러나, 이전에 LCA 는 공간 복잡도를 O(N) 으로 가지고 있었지만
// 이 코드는 O(NLogN) 의 공간 복잡도를 가진다

class ImprovedLCA(
    val root: Int = 1,
    val n: Int,
    val edges: Array<Edge>,
) {
    private val LOG = log2(n.toDouble()).toInt() + 1

    // [x][i] 는 x 노드의 2^i 번째 부모 노드를 의미한다
    private val parents: List<IntArray> = List(n + 1) {
        IntArray(LOG + 1)
    }
    private val depths: IntArray = IntArray(n + 1)

    init {
        require(n >= 1)
        require(n - 1 == edges.size) {
            "트리만 LCA 가 가능하다"
        }
    }

    private val graph: List<MutableList<Int>> = List<MutableList<Int>>(n + 1) {
        mutableListOf()
    }.apply {
        edges.forEach { (f, t, _) ->
            this[f].add(t)
            this[t].add(f)
        }
    }
    private val visited: BooleanArray = BooleanArray(n + 1)

    init {
        visited[root] = true
        initDepth(root, 0)
        initParents()
    }

    private fun initDepth(curNode: Int, depth: Int) {
        graph[curNode].forEach { child ->
            if (visited[child].not()) {
                visited[child] = true
                // 2^0 == 1 번째 부모 노드는 curNode 이다
                parents[child][0] = curNode
                depths[child] = depth + 1
                initDepth(child, depth + 1)
            }
        }
    }

    private fun initParents() {
        for (i in 1..LOG) {
            for (j in 1..n) {
                parents[j][i] = parents[parents[j][i - 1]][i - 1]
            }
        }
    }

    fun lca(n1: Int, n2: Int): Int {
        // 1) 두 노드의 높이를 맞춰야 함
        var sNode = if (depths[n1] < depths[n2]) n1 else n2
        var dNode = if (depths[n1] < depths[n2]) n2 else n1

        // 2) 동일하게 높이를 맞춰준다.
        for (i in LOG downTo 0) {
            val diff = depths[dNode] - depths[sNode]
            val shift = 1 shl i
            // 두 높이의 차이가 2^i 보다 크거나 같다면 sNode 를 Jump 시킨다.
            if (diff >= shift) {
                dNode = parents[dNode][i]
            }
        }
        // 3) 두 노드가 같다면 LCA 는 둘 중 하나다
        if (sNode == dNode) return sNode

        // 4) 두 노드의 부모를 비교하면서 공통 부모를 찾는다
        for (i in LOG downTo 0) {
            // 두 노드의 부모가 다를 때만 Jump 를 한다.
            if (parents[sNode][i] != parents[dNode][i]) {
                sNode = parents[sNode][i]
                dNode = parents[dNode][i]
            }
        }

        return parents[sNode][0] // sNode 의 1칸 위 부모가 LCA다
    }
}