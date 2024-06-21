package designpattern.observer.mvc

class WeatherDisplayController(private val model: WeatherModel, private val view: View) :
    Observer {
    override fun update() {
        view.setOnDisplayListener {
            println("Current conditions: ${model.temperature} F degrees and ${model.humidity}% humidity and ${model.pressure}% pressure")
        }
    }
}
