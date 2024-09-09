package com.murjune.practice.algorithm.floyd

// https://www.acmicpc.net/problem/10159
fun main() {
    val n = readln().toInt()
    val INF = 100_000_000
    val SMALL = 0
    val BIG = 1
    val graph = Array(n + 1) { IntArray(n + 1) { INF } }
    repeat(readln().toInt()) {
        val (s, e) = readln().split(" ").map { it.toInt() }
        graph[s][e] = BIG
        graph[e][s] = SMALL
    }
    for (k in 1..n) {
        for (i in 1..n) {
            for (j in 1..n) {
                // 두 지점의 무게를 비교할 수 없을 때
                if (i == j) {
                    graph[i][j] = 0
                    continue
                }
                if (graph[i][j] == INF) {
                    if (graph[i][k] == BIG && graph[k][j] == BIG) {
                        graph[i][j] = BIG
                        graph[j][i] = SMALL
                    }
                }
            }
        }
    }

    repeat(n) {
        val node = it + 1
        println(graph[node].count { it == INF } - 1)
    }
}