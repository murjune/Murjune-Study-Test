package com.murjune.practice.lamda

// page 232
// page 208: 람다 안에서 람다 밖에 지역변수 변경할 수 있음 (자바와 달리), 코틀린 컴파일러가 내부적으로 Ref 라는 Wrapper 클래스를 만들어서 참조한다.
// 이를 effectively final 이라고 한다.
// https://dev-jwblog.tistory.com/153 참고 : 자바에서 람다에서 지역변수 관련 내용

// 전역 변수로 컴파일
class Student(val name: String, val age: Int)

private fun createPureFunction(): (Int, Int) -> Int {
    val c = 2
    return { a, b -> a + b + c } // 전역 변수로 컴파일되므로 싱글톤이 된다.
}

private var count = 0
private var st = Student("name", 10)

// 전역변수로 컴파일
private fun createImpureFunction(): (Int, Int) -> Int {
    val c = 2
    return { a, b -> a + b + count + st.age } // 전역 변수로 컴파일되므로 싱글톤이 된다.
}

// 람다가 주변 영역의 변수를 참조하면 매 호출마다 다른 instance 를 생성한다.
private fun createImpureFunction2(count: Int): (Int, Int) -> Int {
    var c = 1
    return { a, b ->
        c = 2
        a + b + count + c
    } // 주변
}


fun main() {

    val pureFunction = createPureFunction()
    println(pureFunction == createPureFunction())
    println(pureFunction === createPureFunction())
    println("=====================================")
    // impureFunction은 count 변수를 참조하므로 같은 객체가 아님
    val impureFunction = createImpureFunction()
    val impureFunction2 = createImpureFunction()
    println("impureFunction == impureFunction2 : ${impureFunction == impureFunction2}")
    println(impureFunction(1, 2))
    println(impureFunction2(1, 2))
    println(impureFunction(1, 2) == impureFunction2(1, 2))
    println("=====================================")
    val createImpureFunctionRef = ::createImpureFunction2

    println(createImpureFunctionRef(1) == createImpureFunctionRef(1))
}
