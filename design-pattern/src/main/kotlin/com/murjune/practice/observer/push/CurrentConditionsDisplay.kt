package designpattern.observer.push

class CurrentConditionsDisplay(weatherData: Subject) : Display, Observer {
    private var temperature: Float = 0.0f
    private var humidity: Float = 0.0f
    private var pressure: Float = 0.0f

    init {
        weatherData.registerObserver(this) // 왜 여기서 등록하고, WeatherData 클래스에서 등록하지 않았을까?
    }

    override fun display() {
        println("Current conditions: $temperature F degrees and $humidity% humidity and $pressure% pressure")
    }

    override fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) {
        this.temperature = temperature
        this.humidity = humidity
        this.pressure = pressure
        display()
    }
}
