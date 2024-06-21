package com.murjune.practice.lamda

// page 232
// page 208: 람다 안에서 람다 밖에 지역변수 변경할 수 있음 (자바와 달리), 코틀린 컴파일러가 내부적으로 Ref 라는 Wrapper 클래스를 만들어서 참조한다.
// 이를 effectively final 이라고 한다.
// https://dev-jwblog.tistory.com/153 참고 : 자바에서 람다에서 지역변수 관련 내용

// 전역 변수로 컴파일
private class LambdaEx {
    class Student(val name: String, val age: Int)

    // 결론) 람다 블럭 {} 안에서 외부 변수를 참조하면, 람다가 호출될 때마다 새로운 인스턴스가 생성된다.

    // 1) 전역 변수로 컴파일됨 -> 싱글톤이 된다. (얘만 싱글톤이 된다.)
    fun createPureFunction(): (Int, Int) -> Int {
        return { a, b -> a + b }
    }

    // 2 - 1) 람다 밖에 있는 함수의 지역변수를 참조하면 람다가 호출될 때마다 새로운 인스턴스가 생성된다.
    // Captured variables
    fun createReferToOuterVal(): (Int, Int) -> Int {
        val c = 2
        return { a, b -> a + b + c } // c의 값이 복사되어 전달된다.
    }

    // 2 - 2) 람다 밖에 있는 함수의 지역변수를 참조하면 람다가 호출될 때마다 새로운 인스턴스가 생성된다.
    // Captured variables
    fun createReferToOuterVar(): (Int, Int) -> Int {
        var c = 2
        return { a, b -> a + b + c } // c를 Ref 클래스로 감싸서 참조한다.
    }

    private var count = 0
    private val st = Student("name", 10)

    // 3 - 1) 람다가 주변 영역의 변수를 참조하면 매 호출마다 다른 instance 를 생성한다.
    fun createReferToMemberVar(): (Int, Int) -> Int {
        return { a, b -> a + b }
    }

    // 3 - 2) 람다가 주변 영역의 변수를 참조하면 매 호출마다 다른 instance 를 생성한다.
    fun createReferToMemberVal(): (Int, Int) -> Int {
        return { a, b -> a + b + st.age }
    }

    fun add(action: Int.() -> Unit) {
        5.action()
    }
}

fun main() {
    data class MediaItem(val title: String, val url: String) {
        fun play() {
            println("Playing $title from $url")
        }
    }

    val item = MediaItem("title", "url")
    val items = emptyList<MediaItem>()
    val a = MediaItem::play
    val b = MediaItem::url
    val c = item::url
    val d: (MediaItem) -> Unit = a
}

//fun main() {
//    with(LambdaEx()) {
//        val pureFunction = createPureFunction()
//        println(pureFunction == createPureFunction())
//        println(pureFunction === createPureFunction())
//        println("=====================================")
//        // impureFunction은 count 변수를 참조하므로 같은 객체가 아님
//        val impureFunction = createImpureFunction()
//        val impureFunction2 = createImpureFunction()
//        println("impureFunction == impureFunction2 : ${impureFunction == impureFunction2}")
//        println(impureFunction(1, 2))
//        println(impureFunction2(1, 2))
//        println(impureFunction(1, 2) == impureFunction2(1, 2))
//        println("=====================================")
//        val createImpureFunctionRef = ::createImpureFunction2
//
//        println(createImpureFunctionRef(1) == createImpureFunctionRef(1))
//    }
//}
