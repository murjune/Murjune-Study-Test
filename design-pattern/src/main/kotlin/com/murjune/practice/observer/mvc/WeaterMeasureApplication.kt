package designpattern.observer.mvc

fun main() {
    val weatherModel = WeatherModel(82.0f, 70.0f, 29.2f)
    val controller =
        WeatherDisplayController(weatherModel) { println("Avg/Max/Min temperature = ${weatherModel.temperature} F degrees") }
    weatherModel.registerObserver(controller)
    weatherModel.setMeasurements(78.0f, 90.0f, 29.2f)
    weatherModel.setMeasurements(82.0f, 70.0f, 29.2f)
}
// output
// Avg/Max/Min temperature = 78.0 F degrees
// Avg/Max/Min temperature = 82.0 F degrees
