package com.murjune.practice.sequence

// https://kotlinlang.org/docs/sequences.html
object SequenceEx2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val numbersSequence = sequenceOf("four", "three", "two", "one")
        val s = generateSequence(2) { it + 1 }
        listOf(1, 2, 3).asSequence()

        val oddNumbers = generateSequence(3) { it + 2 } // seed : 초기값
        println(oddNumbers.take(5).toList())

        val oddNumbersLessThan10 = generateSequence(1) { if (it < 8) it + 2 else null } // null 이 반환되면 Sequence 끝
        println(oddNumbersLessThan10.take(10).toList())

        val oddNumbers2 = sequence { yield(1) }
        println(oddNumbers2.take(10).toList())

        println(primesUtil100())
        println(primesUtil100UseSequence().take(29).toList())

    }

    private fun primesUtil100(): List<Int> {
        var nums = List(99) { it + 2 }
        val primes = mutableListOf<Int>()

        while (nums.isNotEmpty()) {
            val prime = nums.first()
            primes.add(prime)
            nums = nums.filterNot { it % prime == 0 }
        }
        return primes
    }

    private fun primesUtil100UseSequence() = sequence {
        var nums = generateSequence(2) { it + 1 }

//        while (true) {
//            val prime = nums.first()
//            yield(prime)
//            nums = nums.drop(1).filterNot { it % prime == 0 }
//        }
        var prime: Int
        while (true) {
            prime = nums.first()
            yield(prime)
            nums = nums
                .drop(1)
                .filterNot { it % prime == 0 }
        }
    }
}
