package designpattern.proxy.usecase2

/**
 * UseCase : 원격 프록시 패턴을 활용한 도서관 대출/반납 시스템
 * */
fun main() {
    val libraryService: LibraryService = LocalLibraryService()

    println("1. Checkout book")
    println("2. Return book")
    println("3. Exit")

    while (true) {
        print("Enter your choice: ")
        when (val choice = readln()) {
            "1" -> {
                print("Enter book name: ")
                val book = readln()
                if (libraryService.checkoutBook(book)) {
                    println("$book 을 성공적으로 대출했습니다.")
                } else {
                    println("$book 이/는 없습니다.")
                }
            }

            "2" -> {
                print("Enter book name: ")
                val book = readln()
                if (libraryService.returnBook(book)) {
                    println("$book 을 반납했습니다.")
                } else {
                    println("$book 은 도서관 소장 도서가 아닙니다.")
                }
            }

            "3" -> {
                println("프로그램을 종료합니다.")
                break
            }

            else -> println("$choice 은 잘못된 입력입니다.")
        }
    }
}
