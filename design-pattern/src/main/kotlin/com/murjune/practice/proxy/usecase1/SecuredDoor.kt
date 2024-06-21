package designpattern.proxy.usecase1

class SecuredDoor(private val door: Door) : Door {
    private val password = "123"

    fun authenticate(inputPassword: String): Boolean {
        return inputPassword == password
    }

    override fun open() {
        println("Unauthorized request to open the door.")
    }

    override fun close() {
        door.close()
    }
}
