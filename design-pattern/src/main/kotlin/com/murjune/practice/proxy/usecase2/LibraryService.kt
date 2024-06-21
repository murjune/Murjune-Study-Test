package designpattern.proxy.usecase2

import java.rmi.Remote
import java.rmi.RemoteException

interface LibraryService : Remote {
    @Throws(RemoteException::class)
    fun checkoutBook(target: String): Boolean

    @Throws(RemoteException::class)
    fun returnBook(target: String): Boolean
}
