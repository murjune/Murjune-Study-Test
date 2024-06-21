package designpattern.proxy.usecase2

import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry

fun main() {
    val registry: Registry = LocateRegistry.createRegistry(1099)
    val service = RemoteLibraryService()
    registry.bind("Library", service)
    println("Library Server is running ...")
}
