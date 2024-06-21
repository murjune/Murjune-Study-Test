package designpattern.proxy.usecase2

import java.rmi.server.UnicastRemoteObject

class RemoteLibraryService : UnicastRemoteObject(), LibraryService {
    private val books = mutableListOf("오둥이", "잡둥이", "오둥이 잡둥이")

    override fun checkoutBook(target: String): Boolean {
        val book = target.trim()
        if (book in books) {
            books.remove(book)
            return true
        }
        return false
    }

    override fun returnBook(target: String): Boolean {
        val book = target.trim()
        if (!books.contains(book)) {
            books.add(book)
            return true
        }
        return false
    }
}
