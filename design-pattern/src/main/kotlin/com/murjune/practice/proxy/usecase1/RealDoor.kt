package designpattern.proxy.usecase1

class RealDoor : Door {
    override fun open() {
        println("Door opened")
    }

    override fun close() {
        println("Door closed")
    }
}
