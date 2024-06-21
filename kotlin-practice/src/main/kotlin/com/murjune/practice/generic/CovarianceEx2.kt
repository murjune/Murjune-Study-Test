package com.murjune.practice.generic

// T는 이제 공변적이다

// var 에 private 붙여야함
// T is declared as 'out' but occurs in 'invariant' position in type List<T>

class Herd2<out T : Animal>(private var animals: List<T> = mutableListOf()) {
    val size: Int get() = animals.size
    operator fun get(i: Int): T {
        return animals[i]
    }
}

fun feedAll2(animals: Herd2<Animal>) {
    (0 until animals.size).forEach {
        animals[it].feed()
    }
}

fun takeCareOfCats2(cats: Herd2<Cat>) {
    (0 until cats.size).forEach {
        cats[it].cleanLitter()
    }
    feedAll2(cats) // 타입 불일치를 해결하기 위해 강제 캐스팅하는건 옳은 방법이 아님
//     feedAll(cats)
    // [Error]
    // Type mismatch: inferred type is Herd<Cat> but Herd<Animal> was expected
    """
        feedAll 함수에 Herd<Cat> 을 넘기면 타입 불일치
        Herd의 T 타입 파라미터에 아무 변성을 지정하지 않았기 때문에
        고양이 무리는 동물 무리가 아ㅣㄴ다
        
        명시적으로 타입 캐스팅을 사용하면 되긴 하지만 
    """.trimIndent()
}

fun main() {
    """
        out 키워드를 통해 Herd를 공변적인 클래스로 만들고 호출 코드를 적절히 바꿀 수 있음
    """.trimIndent()
    takeCareOfCats2(Herd2(listOf(Cat())))
    val f: (Cat) -> Number = fun(a: Animal): Int {
        return 1
    }
    val a: MutableList<in Int> = mutableListOf(1, 2, 3)
    val d = a.removeAt(0) // Int 타입이 나오지 않아
    val a2: MutableList<out Int> = mutableListOf(1, 2, 3)

    val a3: MutableList<Number> = mutableListOf(1, 2.0)
}

fun <T : R, R> copyData(li1: MutableList<T>, li2: MutableList<R>) {
    for (e in li1) {
        li2.add(e)
    }
}

fun <T> copyData2(li1: MutableList<out T>, li2: MutableList<in T>): MutableList<in T> {
    for (e in li1) {
        li2.add(e)
    }
    return li2
}
