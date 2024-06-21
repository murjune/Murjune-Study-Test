package designpattern.proxy.usecase1

import java.rmi.registry.LocateRegistry

fun main() {
    try {
        val registry = LocateRegistry.getRegistry("localhost", 1099)
        val doorService = registry.lookup("DoorService") as DoorService

        while (true) {
            println("1. 문 열기")
            println("2. 문 닫기")
            println("3. 비밀 번호 입력하기")
            println("4. 종료")
            print("1 ~ 4 중 선택해주세요: ")

            when (readln()) {
                "1" -> {
                    println("문을 여는중 ... ")
                    doorService.open()
                }
                "2" -> {
                    println("")
                    doorService.close()
                }
                "3" -> {
                    print("Enter password: ")
                    val inputPassword = readln()
                    if (doorService.authenticate(inputPassword)) {
                        println("Authentication successful. You can now open the door.")
                    } else {
                        println("Authentication failed.")
                    }
                }
                "4" -> {
                    println("Exiting.")
                    return
                }
                else -> println("Invalid choice. Please try again.")
            }
        }
    } catch (e: Exception) {
        println("Client exception: ${e.message}")
    }
}
