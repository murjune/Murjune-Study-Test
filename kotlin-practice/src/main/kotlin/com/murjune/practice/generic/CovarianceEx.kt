package com.murjune.practice.generic

// 공변성
private fun interface Producer<out T> {
    fun produce(): T
}

private fun ex1() {
    // 공변성: 하위 타입 관계를 유지
    // A: Number, B: Int
    // A는 B의 상위 타입
    // 보통 Producer<A> 는 Producer<B>의 상위 타입이 아니다 (무공변)
    // 그러나, out 키워드를 써주면 Producer<A> 는 Producer<B>의 상위 타입 (공변성)

    // 제네릭 클래스가 타입 파라미터에 대해 공변적임을 나타내려면
    // out T(타입 파라미터) 이렇게 써주면 된다.
    val p1: Producer<Number> = Producer<Int> { 1 }
}
