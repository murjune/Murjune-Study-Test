package designpattern.observer.non_observer_pattern

/**
 소프트웨어 개발에 절대 변하지 않는 것은 '변화'임
 그러나 아래 코드는 변화에 대해 유연하지 않고, 확장하기 어려움 (즉, 잘못된 설계임)
 - 디스플레이를 추가하거나 제거할 때마다 WeatherData 클래스도 수정해야함
 - 즉, 동적으로 디스플레이를 추가하거나 제거할 수 없음
 이러한 문제를 해결하기 위해 Observer 패턴을 사용할 것임
 우리는 변화에 대한 정보를 제공하는 Subject와 변화를 관찰하는 Observer를 정의할 것임
 */
class WeatherData(
    private val temperature: Float,
    private val humidity: Float,
    private val pressure: Float,
    private val conditionDisplay: Display,
    private val statisticsDisplay: Display,
    private val forecastDisplay: Display,
) {
    fun measurementsChanged() {
        conditionDisplay.update(temperature, humidity, pressure)
        statisticsDisplay.update(temperature, humidity, pressure)
        forecastDisplay.update(temperature, humidity, pressure)
    }
}

class ConditionDisplay : Display {
    override fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) = println("ConditionDisplay updated")
}

class StatisticsDisplay : Display {
    override fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) = println("StatisticsDisplay updated")
}

class ForecastDisplay : Display {
    override fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    ) = println("ForecastDisplay updated")
}

interface Display {
    fun update(
        temperature: Float,
        humidity: Float,
        pressure: Float,
    )
}
