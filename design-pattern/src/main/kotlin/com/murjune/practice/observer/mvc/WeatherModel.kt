package designpattern.observer.mvc

class WeatherModel(
    temperature: Float,
    humidity: Float,
    pressure: Float,
) : Subject {
    var temperature: Float = temperature
        private set
    var humidity: Float = humidity
        private set
    var pressure: Float = pressure
        private set

    private val observerList by lazy { mutableListOf<Observer>() }

    override fun registerObserver(observer: Observer) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observerList.remove(observer)
    }

    override fun notifyObservers() {
        observerList.forEach(Observer::update)
    }

    fun setMeasurements(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) {
        this.temperature = temperature
        this.humidity = humidity
        this.pressure = pressure
        notifyObservers()
    }
}
