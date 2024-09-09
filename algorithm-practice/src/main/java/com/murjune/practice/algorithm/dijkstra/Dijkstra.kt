package com.murjune.practice.algorithm.dijkstra

import java.util.PriorityQueue

class Dijkstra(
    val n: Int,
    val edges: List<Edge>,
) {
    private val distance: IntArray = IntArray(n + 1) { Int.MAX_VALUE }
    private val oneToAllGraph: Array<MutableList<Node>> = Array(n + 1) {
        mutableListOf()
    }
    private val allToOneGraph: Array<MutableList<Node>> = Array(n + 1) {
        mutableListOf()
    }

    init {
        require(n >= 1) {
            "n must be greater than or equal to 1"
        }

        require(edges.all { it.from in 0..n }) {
            "edges.from must be in 0 until n"
        }

        require(edges.all { it.to in 0..n }) {
            "edges.to must be in 0 until n"
        }

        require(edges.map { it.from to it.to }.distinct() == edges.map { it.from to it.to }) {
            "edges must be distinct"
        }
        initGraph()
    }

    private fun initGraph() {
        edges.forEach { (f, t, cost) ->
            oneToAllGraph[f].add(Node(t, cost))
            allToOneGraph[t].add(Node(f, cost))
        }
    }

    fun oneToAll(startNode: Int, isNodeStartZero: Boolean = false): List<Int> {
        return shortDistances(oneToAllGraph, startNode, isNodeStartZero)
    }

    fun allToOne(startNode: Int, isNodeStartZero: Boolean = false): List<Int> {
        return shortDistances(allToOneGraph, startNode, isNodeStartZero)
    }

    private fun shortDistances(
        graph: Array<MutableList<Node>>,
        startNode: Int,
        isNodeStartZero: Boolean = false
    ): List<Int> {
        require(startNode in 0..n) {
            "startNode must be in 0..n"
        }
        val start = Node(startNode, 0)
        val pq = PriorityQueue<Node>(compareBy { it.cost }).apply {
            add(start)
            distance[start.v] = 0
        }

        while (pq.isNotEmpty()) {
            val (nowNode, nowCost) = pq.remove()

            // 방문 처리
            if (distance[nowNode] < nowCost) continue

            // 다음 노드 선택
            graph[nowNode].forEach { (nextNode, nextCost) ->
                val newCost = nowCost + nextCost

                if (newCost < distance[nextNode]) {
                    distance[nextNode] = newCost
                    val newNode = Node(nextNode, newCost)
                    pq.add(newNode)
                }
            }
        }

        if (isNodeStartZero) return distance.dropLast(1)
        return distance.drop(1)
    }

    private data class Node(val v: Int, val cost: Int)
}

data class Edge(val from: Int, val to: Int, val cost: Int = 0)
