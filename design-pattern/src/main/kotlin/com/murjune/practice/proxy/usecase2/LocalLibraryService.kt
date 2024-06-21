package designpattern.proxy.usecase2

import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry

class LocalLibraryService : LibraryService {
    private val libraryService: LibraryService

    init {
        val registry: Registry = LocateRegistry.getRegistry("localhost", 1099)
        libraryService = registry.lookup("Library") as LibraryService
    }

    override fun checkoutBook(target: String): Boolean {
        return libraryService.checkoutBook(target)
    }

    override fun returnBook(target: String): Boolean {
        return libraryService.returnBook(target)
    }
}
