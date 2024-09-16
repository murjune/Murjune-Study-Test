package com.murjune.practice.algorithm.lca

import kotlin.math.log2

fun main() {
    val n = readln().toInt()
    val LOG = log2(n.toDouble()).toInt() + 1
    val graph = List(n + 1) {
        mutableListOf<Int>()
    }
    val depths = IntArray(n + 1)
    val parents = List(n + 1) {
        IntArray(LOG + 1)
    }
    val visited = BooleanArray(n + 1)

    repeat(n - 1) {
        val (a, b) = readln().split(" ").map { it.toInt() }
        graph[a].add(b)
        graph[b].add(a)
    }

    fun dfs(cur: Int, depth: Int) {
        visited[cur] = true

        graph[cur].forEach { ch ->
            if (visited[ch].not()) {
                depths[ch] = depth + 1
                parents[ch][0] = cur
                dfs(ch, depth + 1)
            }
        }
    }

    fun initParents() {
        for (i in 1..LOG) {
            for (j in 1..n) {
                parents[j][i] = parents[parents[j][i - 1]][i - 1]
            }
        }
    }

    fun lca(a: Int, b: Int): Int {
        val aD = depths[a]
        val bD = depths[b]

        var sn = if (aD < bD) a else b
        var dn = if (aD < bD) b else a

        // 1. 두 노드 깊이 맞추기 - dn 위로 점프
        for (i in LOG downTo 0) {
            val diff = depths[dn] - depths[sn]
            val jump = 1 shl i // 2^i
            if (diff >= jump) {
                dn = parents[dn][i]
            }
        }
        // 2. 두 노드가 같으면 반환
        if (sn == dn) return sn
        // 3. 두 노드의 lca 찾기
        for (i in LOG downTo 0) {
            if (parents[sn][i] != parents[dn][i]) {
                sn = parents[sn][i]
                dn = parents[dn][i]
            }
        }
        return parents[sn][0]
    }
    // root 노드: 1
    dfs(1, 0)
    initParents()

    repeat(readln().toInt()) {
        val (a, b) = readln().split(" ").map { it.toInt() }
        println(lca(a, b))
    }
}