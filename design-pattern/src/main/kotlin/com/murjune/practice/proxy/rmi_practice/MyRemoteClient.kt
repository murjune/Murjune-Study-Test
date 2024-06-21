package designpattern.proxy.rmi_practice

import java.net.InetAddress
import java.rmi.Naming

class MyRemoteClient {
    fun go() {
        try {
            // Naming.lookup()은 RMI 레지스트리에서 원격 개체를 검색하는 데 사용 - 원격 개체에 대한 참조를 반환
            // 반환된 참조는 원격 개체의 스텁이다.
            val ipAddress = InetAddress.getLocalHost().hostAddress
            val service = Naming.lookup("rmi://$ipAddress/MyService") as MyService
            // 스텁은 원격 개체에 대한 호출을 처리하고 결과를 클라이언트에게 반환한다.
            val message = service.sayHello()
            println(message)
        } catch (e: Exception) {
            println("Error occurred: $e")
        }
    }
}

fun main() {
    MyRemoteClient().go()
}
