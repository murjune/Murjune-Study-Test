package designpattern.proxy.usecase1

import java.rmi.Remote
import java.rmi.RemoteException

interface DoorService : Remote {
    @Throws(RemoteException::class)
    fun open()

    @Throws(RemoteException::class)
    fun close()

    @Throws(RemoteException::class)
    fun authenticate(inputPassword: String): Boolean
}
