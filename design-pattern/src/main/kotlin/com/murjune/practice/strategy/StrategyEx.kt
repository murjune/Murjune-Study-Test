package designpattern.strategy

object StrategyEx {
    fun interface Strategy { // 전략 == 로또 넘버 발행하는 친구
        fun execute()
    }

    // 함수에 전략을 주입하는 방법
    class StrategyExecutor1 { // 전략 사용 객체 == 로또 판매원 아줌마
        fun execute(strategy: Strategy) {
            strategy.execute()
        }
    }

    // 생성자에 전략을 주입하는 방법
    class StrategyExecutor2(private val strategy: Strategy) {
        fun execute() {
            strategy.execute()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val strategyExecutor = StrategyExecutor1()
        strategyExecutor.execute { println("전략 1") }
        strategyExecutor.execute { println("전략 2") }

        val strategyExecutor2 = StrategyExecutor2 { println("전략 3") }
        strategyExecutor2.execute()
    }
}
