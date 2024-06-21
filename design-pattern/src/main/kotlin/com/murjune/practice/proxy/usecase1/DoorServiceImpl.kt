package designpattern.proxy.usecase1

import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject

class DoorServiceImpl : UnicastRemoteObject(), DoorService {
    private val realDoor = RealDoor()
    private val password = "123"
    private var isSecured = true

    @Throws(RemoteException::class)
    override fun open() {
        if (isSecured) {
            println("문이 잠겨있습니다.")
            return
        }
        realDoor.open()
    }

    @Throws(RemoteException::class)
    override fun close() {
        realDoor.close()
    }

    @Throws(RemoteException::class)
    override fun authenticate(inputPassword: String): Boolean {
        isSecured = inputPassword != password
        return !isSecured
    }
}
