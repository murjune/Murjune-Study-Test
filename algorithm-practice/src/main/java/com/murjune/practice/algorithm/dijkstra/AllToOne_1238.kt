package com.murjune.practice.algorithm.dijkstra

import java.util.PriorityQueue

fun main() {
    // https://www.acmicpc.net/problem/1238
    // N 1 .. 1000
    // M 1 .. 10000
    // X 1 .. N
    // 방향 그래프, 순환 그래프 X
    data class Node(val v: Int, val t: Int)
    val (N, M, X) = readln().split(" ").map { it.toInt() }
    val graph = Array(N + 1) {
        mutableListOf<Node>()
    }
    val reverseGraph = Array(N + 1) {
        mutableListOf<Node>()
    }
    repeat(M) {
        val (s, e, t) = readln().split(" ").map { it.toInt() }.toTypedArray()
        graph[s].add(Node(e, t))
        reverseGraph[e].add(Node(s, t))
    }

    // start -> X -> start O(M * LogM) * 2
    fun dijkstra(start: Int, graph: Array<MutableList<Node>>): List<Int> {

        val distance = IntArray(N + 1) { Int.MAX_VALUE }
        val pq = PriorityQueue<Node>(compareBy { it.t })

        distance[start] = 0
        pq.add(Node(start, 0))

        while (pq.isNotEmpty()) {
            val (v, t) = pq.remove()

            if (distance[v] < t) continue

            graph[v].forEach { (nv, nt) ->
                val newT = t + nt

                if (distance[nv] > newT) {
                    distance[nv] = newT
                    pq.add(Node(nv, newT))
                }
            }
        }
        return distance.drop(1)
    }

    val xToAllDist = dijkstra(X, graph) // x -> all 가는데 걸리는 시간
    val allToXDist = dijkstra(X, reverseGraph) // all -> x 가는데 걸리는 시간
    val totalDist = xToAllDist.zip(allToXDist).map { it.first + it.second }

    println(totalDist.max())
}