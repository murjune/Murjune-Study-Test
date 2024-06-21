package designpattern.observer.push

fun main() {
    val weatherData = WeatherData(80f, 65f, 30.4f)
    val currentConditionsDisplay = CurrentConditionsDisplay(weatherData)

    weatherData.setMeasurements(82f, 70f, 29.2f)
    weatherData.setMeasurements(78f, 90f, 29.2f)
}
// output
// Current conditions: 82.0 F degrees and 70.0% humidity and 29.2% pressure
// Current conditions: 78.0 F degrees and 90.0% humidity and 29.2% pressure
