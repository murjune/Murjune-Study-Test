package com.murjune.practice.lamda

object Lambda_FunctionReference_Ex1 {
    private lateinit var listener: ButtonClickListener

    @JvmStatic
    fun main(args: Array<String>) {
        listOf("1").sortedBy(String::length)
        val button = listener.onClick()
        val button2 = listener::onClick
//        ScreenView().apply {
//            listener = ButtonClickListener()
//            button.performClick()
//            button2.performClick()
    }
}

private class Button(
    private val onClick: () -> Unit
) {
    fun performClick() = onClick()
}


private class ButtonClickListener {
    fun onClick() {
        println("Button clicked")
    }
}

private class ScreenView {
    lateinit var listener: ButtonClickListener
    val button = Button { listener.onClick() }
    val button2 = Button(listener::onClick) // 위와 똑같다
}

// Lambda - getter()로 붙터옴
// val button = Button { listener.onClick() } 이 시점에 이미
// lambda : the Function0 익명 클래스의 구현체가 만들어짐
// 그래서 우리가 Button 의 생성자에 람다를 넘기면
// the listener variable will be used after performing button click and it will already be initialized.
// performClick() 함수가 호출된 이후에 listener 를 호출하기 때문에 ㄱㅊㄱㅊ


// 함수 reference  - field (receiver) 를 불러옴
// this.receiver 는 Function0 에 종속되어 있음
// 그리고, receiver 는 FUnction0 의 invoke 가 호출될 때 불림
// 즉, Button 생성된 시점에서, listener 가 초기화 되어있지 않으면 UninitializedPropertyAccessException 이 발생

// 정리)
// 메소드 참조: 참조가 생성되는 순간 고정
// 람다: getter()로 변수를 호출하니까 매번 달라질 수 있음
