package com.murjune.practice.algorithm.floyd


import kotlin.math.pow

// 플로이드-워셜 풀이 - 10억?
fun main() {
    // https://www.acmicpc.net/problem/1238
    data class Node(val v: Int, val t: Int)
    val (N, M, X) = readln().split(" ").map { it.toInt() }
    val graph = Array(N + 1) {
        IntArray(N + 1) { 10.0.pow(8).toInt() }
    }

    repeat(N + 1) {
        graph[it][it] = 0
    }

    repeat(M) {
        val (s, e, t) = readln().split(" ").map { it.toInt() }
        graph[s][e] = t
    }
    for (k in 1..N) {
        for (i in 1..N) {
            for (j in 1..N) {
                if (i == j) continue
                graph[i][j] = (graph[i][k] + graph[k][j]).coerceAtMost(graph[i][j])
            }
        }
    }
    List(N) {
        val num = it + 1
        graph[num][X] + graph[X][num]
    }.max().let(::println)
}