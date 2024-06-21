package designpattern.strategy

object WALWAL {
    // move라는 행동이 있어

    interface Movable { // interface 가 책임을 나타내는 규약?? 같은거라고 나도 생각함
        fun move()
    }

    class Car : Movable {
        override fun move() {
            println("붕붕")
        }
    }

    class Train : Movable {
        override fun move() {
            println("철컹")
        }
    }

    class Duck : Quackable, Flyable {
        override fun quack() = println("꽥꽥")

        override fun fly() {
            println("닌디")
        }
    }

    class Duck2 : Quackable {
        override fun quack() = println("꽥꽥")
    }

    interface Quackable {
        fun quack()
    }

    interface Flyable {
        fun fly()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // client
        //  꽉거리는 녀석들이 필요해, 달리기를 한다거나, 난다거나 필요 없어
        val quacks: List<Quackable> = listOf(Duck(), Duck2())
        val fly = Duck()

        // 로또 생성하는 걸 인터페이스로 만들었다! 그 능력을 generator 한테 줌
        // 오리1, 2 같은 행동??
    }
}
