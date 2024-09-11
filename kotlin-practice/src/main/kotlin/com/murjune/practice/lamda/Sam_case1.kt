package com.murjune.practice.lamda

// kotlin 1.4 이상부터는 SAM 생성자를 사용할 수 있다.
// kotlin in action : Page 234
private fun interface SAM {
    fun action()
}

private fun interface SAM2 {
    fun action(a: Int)
}

private fun interface SAM3 {
    fun action(a: String)
}

private fun functionWithLambda(a: () -> Unit) {
    println("call samFunction(a: () -> Unit)")
}

//private fun functionWithLambda(a: (Int) -> Unit) {
//    println("call samFunction(a: (Int) -> Unit)")
//}
//
//private fun functionWithLambda(a: (String) -> Unit) {
//    println("call samFunction(a: (String) -> Unit)")
//}

private fun functionWithSAM(a: SAM) {
    println("call functionWithSAM(a: (Int) -> Unit)")
}

private fun functionWithSAM(a: SAM2) {
    println("call functionWithSAM(a: (Int) -> Unit)")
}

private fun functionWithSAM(a: SAM3) {
    println("call functionWithSAM(a: (Int) -> Unit)")
}

fun main() {
    // Lambda
//    functionWithLambda {
//        println()
//    }
//    functionWithLambda { o: Int ->
//        println()
//    }
//    functionWithLambda { o: String ->
//        println()
//    }
    /// SAM
    functionWithSAM(SAM {
        println()
    }
    )

    functionWithSAM { a: Int ->
        println(a)
    }

    functionWithSAM { a: String ->
        println(a)
    }
}

val x = 1

private fun foo() {
    val x = 10
    bar()
}

fun bar() {
    println(x)
}
