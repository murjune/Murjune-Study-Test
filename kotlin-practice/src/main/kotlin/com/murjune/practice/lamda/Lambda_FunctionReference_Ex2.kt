package com.murjune.practice.lamda

object Lambda_FunctionReference_Ex2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val screenView = ScreenView()
        screenView.listener = ButtonClickListener("Second")
        screenView.buttonLambda.performClick()
        screenView.buttonReference.performClick()
    }

    private class Button(
        private val onClick: () -> Unit
    ) {
        fun performClick() = onClick()
    }

    private class ButtonClickListener(
        private val name: String
    ) {
        fun onClick() {
            print(name)
        }
    }

    private class ScreenView {
        var listener = ButtonClickListener("First")
        val buttonLambda = Button { listener.onClick(); error(1) }
        val buttonReference = Button(listener::onClick)
    }
}

// https://proandroiddev.com/kotlin-lambda-vs-method-reference-fdbd175f6845
