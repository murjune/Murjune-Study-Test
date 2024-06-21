package com.murjune.practice.generic

private fun printContents(list: List<Any>) {
    println(list.joinToString())
}

private fun ex1() {
    println(printContents(listOf("abc", "bqc")))
}

private fun addAns(list: MutableList<Any>) {
    list.add(42)
}

private fun ex2() {
    val sts = mutableListOf("abc", "bcd")
    // addAns(sts) // 실제로는 컴파일 안됨
    // 그러나 가능하다고 가정을 해보자
    println(sts.maxBy(String::length)) // 실행 시점에서 Error 발생할 것이다.

    """
        sts 안에 42 라는 element 가 있음
        근데 Int 타입은 length 라는 프로퍼티가 없다
        따라서, Error 발생
        
        코틀린에서는 컴파일러가 실제 이런 함수를 호출 못하게 한다
    """.trimIndent()
}

private fun printlnAns(list: List<Any>) {
    println(list)
}
private fun ex3() {
    val sts = mutableListOf("abc", "bcd")
    printlnAns(sts)
    """
        MutableList 와 달리 List 는 가능!
        코틀린에서는 리스트의 변경 가능성에 따라 적절한 인터페이스를 사용하면
        안전하지 못한 함수 호출을 막을 수 있다.
    """.trimIndent()
}

