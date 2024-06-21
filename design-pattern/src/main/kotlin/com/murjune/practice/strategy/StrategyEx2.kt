package designpattern.strategy

import kotlin.random.Random

object StrategyEx2 {
    // 왜 전략패턴을 사용하는가..??
    // 충전 전략을 세워야대 고속 충전, 저속 충전
    class GalaxyPhone(val name: String) {
        var battery = 0
            private set

        fun charge(amount: Int) {
            battery += amount
        }
    }

    // Number 제너레이터
    interface ChargeStrategy {
        fun charge(phone: GalaxyPhone)
    }

    // 충전기 == 아줌마? 생각
    class Charger(private val strategy: ChargeStrategy) {
        fun charge(phone: GalaxyPhone) {
            strategy.charge(phone)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // phone
        val s23 = GalaxyPhone("S23")
        // charger
        val fastCharger =
            Charger(
                object : ChargeStrategy {
                    override fun charge(phone: GalaxyPhone) {
                        // 충전을 할껀데 누구한테?? 누구한테 충전을 하지???
                        // 여러 기능을 함수로 다 쪼개봐
                        phone.charge(10)
                    }
                },
            )
        val generalCharger =
            Charger(
                object : ChargeStrategy {
                    override fun charge(phone: GalaxyPhone) {
                        phone.charge(5)
                    }
                },
            )
        val slowCharger =
            Charger(
                object : ChargeStrategy {
                    override fun charge(phone: GalaxyPhone) {
                        phone.charge(3)
                    }
                },
            )
        // 실제로 사용하는 녀석!
        val randomCharger =
            Charger(
                object : ChargeStrategy {
                    override fun charge(phone: GalaxyPhone) {
                        phone.charge(Random.nextInt(0, 30)) // 항상 동일한 결과가 나와야해
                    }
                },
            )
        // charge
        fastCharger.charge(s23)
        println("battery : ${s23.battery}")
        generalCharger.charge(s23)
        println("battery : ${s23.battery}")
        slowCharger.charge(s23)
        println("battery : ${s23.battery}")
        randomCharger.charge(s23)
        println("battery : ${s23.battery}")
    }
}
