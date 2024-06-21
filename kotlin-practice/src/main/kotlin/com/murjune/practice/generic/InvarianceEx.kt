package com.murjune.practice.generic

open class Animal {
    fun feed() {}
}

class Cat : Animal() {
    fun cleanLitter() {}
}

// 타입 파라미터가 무공변성이 됨
class Herd<T : Animal>(val animals: List<T> = listOf()) {
    val size: Int get() = animals.size
    operator fun get(i: Int): T {
        return animals[i]
    }
}

fun feedAll(animals: Herd<Animal>) {
    (0 until animals.size).forEach {
        animals[it].feed()
    }
}

fun takeCareOfCats(cats: Herd<Cat>) {
    (0 until cats.size).forEach {
        cats[it].cleanLitter()
    }
    feedAll(cats as Herd<Animal>) // 타입 불일치를 해결하기 위해 강제 캐스팅하는건 옳은 방법이 아님
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
    takeCareOfCats(Herd(listOf(Cat())))
}
