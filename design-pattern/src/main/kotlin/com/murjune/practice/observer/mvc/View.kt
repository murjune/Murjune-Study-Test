package designpattern.observer.mvc

fun interface View {
    fun setOnDisplayListener(listener: () -> Unit)
}
