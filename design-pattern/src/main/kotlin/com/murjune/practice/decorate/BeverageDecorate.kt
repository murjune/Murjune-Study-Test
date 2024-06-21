package designpattern.decorate

private object DecorateEx {
    @JvmStatic
    fun main(args: Array<String>) {
        println(WinningLotto(object : Lotto {
            override val lottoNumber: List<Int>
                get() = listOf(1, 2, 3, 4, 5, 6)

        }, 9))
    }

    interface Lotto {
        val lottoNumber: List<Int>

        operator fun contains(other: Int): Boolean {
            return other in lottoNumber
        }

        fun countMatchNumbers(other: Lotto): Int {
            return (lottoNumber intersect other.lottoNumber).size
        }
    }

    class WinningLotto(lotto: Lotto, private val bonusNumber: Int) : Lotto by lotto {
        val d = 1

        init {
            println(d)
        }

        val d2 = 2
    }
}

