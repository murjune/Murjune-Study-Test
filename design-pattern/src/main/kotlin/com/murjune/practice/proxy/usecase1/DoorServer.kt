package designpattern.proxy.usecase1

import java.rmi.registry.LocateRegistry

fun main() {
    try {
        val registry = LocateRegistry.createRegistry(1099)
        val doorService = DoorServiceImpl()
        registry.bind("DoorService", doorService)
        println("============ Door Server is running ============")
    } catch (e: Exception) {
        println("Server exception: ${e.message}")
    }
}
