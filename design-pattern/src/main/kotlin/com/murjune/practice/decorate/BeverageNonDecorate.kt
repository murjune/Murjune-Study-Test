package designpattern.decorate

object BeverageNonDecorate {
    @JvmStatic
    fun main(args: Array<String>) {
        // 고객은
    }

    abstract class Beverage(val description: String) {
        // milk, soy, mocha, whip 등의 첨가물을 추가하는 기능이 필요하다면?
        protected abstract var hasMilk: Boolean
        protected abstract var hasSoy: Boolean
        protected abstract var hasMocha: Boolean
        protected abstract var hsaWhip: Boolean
        private val milkCost: Double = 1000.0
        private val soyCost: Double = 500.0
        private val mochaCost: Double = 300.0
        private val whipCost: Double = 700.0

        open fun cost(): Int {
            var totalPrice = 0.0
            if (hasMilk) totalPrice += milkCost
            if (hasSoy) totalPrice += soyCost
            if (hasMocha) totalPrice += mochaCost
            if (hsaWhip) totalPrice += whipCost

            return totalPrice.toInt()
        }
    }

    class Espresso : Beverage("Espresso") {
        override var hasMilk: Boolean = false
        override var hasSoy: Boolean = false
        override var hasMocha: Boolean = false
        override var hsaWhip: Boolean = false
    }

    class HouseBlend : Beverage("House Blend Coffee") {
        override var hasMilk: Boolean = false
        override var hasSoy: Boolean = true
        override var hasMocha: Boolean = true
        override var hsaWhip: Boolean = false
    }

    class DarkRoast : Beverage("Dark Roast Coffee") {
        override var hasMilk: Boolean = false
        override var hasSoy: Boolean = true
        override var hasMocha: Boolean = false
        override var hsaWhip: Boolean = false
    }
}
