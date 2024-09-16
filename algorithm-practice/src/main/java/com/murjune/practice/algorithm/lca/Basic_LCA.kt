package com.murjune.practice.algorithm.lca

fun main() {
    val n = readln().toInt()
    val root = 1
    val parents = IntArray(n + 1)
    val depths = IntArray(n + 1)
    val visited = BooleanArray(n + 1)
    val graph = List(n + 1) {
        mutableListOf<Int>()
    }

    repeat(n - 1) {
        val (f, t) = readln().split(" ").map { it.toInt() }
        graph[f].add(t)
        graph[t].add(f)
    }

    fun findDepths(cur: Int, depth: Int) {
        visited[cur] = true

        graph[cur].forEach { child ->
            if (visited[child].not()) {
                parents[child] = cur
                depths[child] = depth + 1
                findDepths(child, depth + 1)
            }
        }
    }
    // 깊이 설정
    findDepths(root, 0)
//    println(depths.toList())
    // LCA 함수
    fun lca(a: Int, b: Int): Int {
        val aD = depths[a]
        val bD = depths[b]
        // 깊이 맞추기
        var sNode = if (aD < bD) a else b
        var dNode = if (aD < bD) b else a

        while (depths[sNode] != depths[dNode]) {
            dNode = parents[dNode] // 한칸씩 up
        }
        // 공통 부모 찾기
        while (sNode != dNode) {
            sNode = parents[sNode]
            dNode = parents[dNode]
        }
        return sNode
    }

    repeat(readln().toInt()) {
        val (a, b) = readln().split(" ").map { it.toInt() }
        println(lca(a, b))
    }
}