package designpattern.observer.push

interface Observer {
    fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    )
}
