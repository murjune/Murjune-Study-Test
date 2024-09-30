package com.murjune.practice.algorithm.combination


fun combination(n: Int, r: Int): List<List<Int>> {
    require(n > 0)
    require(r >= 0)
    val result = mutableListOf<List<Int>>()
    val list = ArrayDeque<Int>(n)

    fun dfs(start: Int, depth: Int) {
        if (depth == r) {
            result.add(list.toList()) // 🚨반드시 복사 해줘야한다!
            return
        }

        for (i in start..n) {
            list.add(i)
            dfs(i + 1, depth + 1)
            list.remove(i)
        }
    }

    dfs(1, 0)
    return result
}
