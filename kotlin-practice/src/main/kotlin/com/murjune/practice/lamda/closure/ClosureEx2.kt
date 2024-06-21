package com.murjune.practice.lamda.closure

private fun outerFunction(): () -> String {
    val outerVariable = "closure 외부 지역 변수, (자유변수)"
    val closureLambda = { "나는 $outerVariable 를 알고 있어, 그러니까 난 closure 야" }
    return closureLambda
}

fun main() {
    val outer = outerFunction()
    // 현재 main 에서는 outerVariable 을 접근할 수 없음
    println(outer())
    // output: 나는 closure 외부 지역 변수 를 알고 있어, 그러니까 난 closure 야
}

// ## JavaScript 와 다른점
//
//> Closure 는 함수와 그 주변 환경(Lexical Environment)에 대한 조합이다.
//
//
//`Lexical Environment` 은 Closure 가 실행 시점(runtime)에 범위에 존재하는 변수(자유변수, 지역 변수)의 값과 외부 Reference 를 관리하는 녀석이다.
//
//## Closure 라 불리는 이유
//
//```kotlin
//fun outer(): () -> Unit {
//    val outerValue = 10
//    fun inner() { // 클로저
//        println("outerValue: $outerValue") // 자유 변수
//    }
//    return ::inner
//}
//
//fun main() {
//    val innerFunction = outer()
//    innerFunction() // "outerValue: 10"
//}
//```
// main() 함수를 실행하면 `"outerValue: 10"` 이라는 실행 결과가 나온다.
//
//#### <U> 이상하지 않은가?? outer() 함수가 종료됐는데 어떻게 innerFunction 을 호출할 때 outerValue 가 출력될까?! </U>
//
//>  보통 함수가 호출될 때 JVM 의 Call Stack 영역에 적재가 되고, 함수가 반환되면 Call Stack 에서 Pop 된다.
//그리고, 함수내 지역 변수의 생명 주기는 함수가 Stack 에서 Pop 되면 생명주기가 끝난다.
//
//이게 가능한 이유는 inner() 가 outerValue 값을 `Capture` 했기 때문이다!!
//
//```kotlin
//fun outer(): () -> Unit {
//    val outerValue = 10
//    val inner = fun inner() {
//        val innerValue = outerValue // 컴파일러가 자동으로 복붙을 해줌! (Capture)
//        println("outerValue: $innerValue")
//    }
//    return ::inner
//}
//```
//코틀린 컴파일러는 람다 혹은 nested 함수가 외부 변수를 참조할 경우
//자동으로 외부 참조값을 body 안에 복사 붙여넣기를 해준다.
//
//➡ outer() 함수가 종료되도 inner 함수 내부적으로 저장한 `innerValue` 을 사용한다!
