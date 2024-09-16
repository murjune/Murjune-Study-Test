package com.murjune.practice.algorithm.lca

import com.murjune.practice.algorithm.common.Edge

class LCA(
    val root: Int = 1,
    val n: Int,
    val edges: Array<Edge>,
) {
    private val parents: IntArray = IntArray(n + 1)
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
    }

    // 모든 노드들의 depth 를 구한다
    private fun initDepth(curNode: Int, depth: Int) {
        graph[curNode].forEach { child ->
            if (visited[child].not()) {
                visited[child] = true
                parents[child] = curNode
                depths[child] = depth + 1
                initDepth(child, depth + 1)
            }
        }
    }

    fun lca(n1: Int, n2: Int): Int {
        // 1) 두 노드의 높이를 맞춰야 함
        var sNode = if (depths[n1] < depths[n2]) n1 else n2
        var dNode = if (depths[n1] < depths[n2]) n2 else n1

        while (depths[sNode] != depths[dNode]) {
            dNode = parents[dNode]
        }
        // 2) 한칸씩 위로 올라가면서 parent 가 같은지 찾는다
        while (sNode != dNode) {
            sNode = parents[sNode]
            dNode = parents[dNode]
        }

        return sNode
    }
}