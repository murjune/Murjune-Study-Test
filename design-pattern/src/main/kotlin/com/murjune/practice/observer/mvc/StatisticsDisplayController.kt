package designpattern.observer.mvc

class StatisticsDisplayController(
    private val model: WeatherModel,
    private val view: View,
) : Observer {
    override fun update() {
        view.setOnDisplayListener {
            println("Avg/Max/Min temperature = ${model.temperature} F degrees")
        }
    }
}
