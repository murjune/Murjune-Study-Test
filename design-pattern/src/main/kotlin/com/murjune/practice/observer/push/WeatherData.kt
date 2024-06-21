package designpattern.observer.push

internal class WeatherData(
    private var temperature: Float,
    private var humidity: Float,
    private var pressure: Float,
) : Subject {
    private val observerList by lazy { mutableListOf<Observer>() }

    override fun registerObserver(observer: Observer) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observerList.remove(observer)
    }

    override fun notifyObservers() {
        observerList.forEach { observer -> observer.update(temperature, humidity, pressure) }
    }

    fun measurementsChanged() {
        notifyObservers()
    }

    fun setMeasurements(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) {
        this.temperature = temperature
        this.humidity = humidity
        this.pressure = pressure
        measurementsChanged()
    }
}
