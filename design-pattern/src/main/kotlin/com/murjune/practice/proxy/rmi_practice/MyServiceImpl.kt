package designpattern.proxy.rmi_practice

import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject

/**
 * 2) 서비스 구현 클래스 : 실제 작업을 처리하는 클래스
 * 원격 메서드를 실제로 구현한 코드가 들어감
 * 나중에 클라이언트에서 이 객체에 있는 메서드 호출 (즉 얘가 스텁)
 *
 * 추후 작성할 헤드퍼스트 예제의 GumballMachine 같은거
 *
 * MyService 인터페이스를 구현하고, UnicastRemoteObject를 상속받아야 한다.
 * UnicastRemoteObject는 원격 객체 기능을 제공하는 클래스
 *
 * RMI(Remote Method Invocation) 레지스트리는 Java RMI 프레임워크의 핵심 구성 요소로
 * 원격 클라이언트가 이름으로 원격 개체를 조회하고 해당 참조를 얻을 수 있도록 하는 간단한 서버측 이름 레지스트리 역할을 합니다.
 * 본질적으로 이는 이름을 원격 개체와 연결하여 RMI 클라이언트가 원격 서비스에 더 쉽게 연결할 수 있도록 하는 이름 지정 서비스입니다.
 *
 *
 * 원격 개체가 RMI 레지스트리에 등록되면 레지스트리는 개체 이름과 개체에 대한 참조를 모두 저장합니다.
 * 나중에 클라이언트는 이름으로 레지스트리를 쿼리하여 원격 개체에 대한 참조를 얻은 다음 해당 개체가 로컬 개체인 것처럼 해당 개체에 대한 메서드를 호출할 수 있습니다.
 * */
class MyServiceImpl : MyService, UnicastRemoteObject() {
    @Throws(java.rmi.RemoteException::class)
    override fun sayHello(): String {
        return "안녕 나 오둥이"
    }

    private companion object {
        const val serialVersionUID =
            1L // 직렬화 버전 UID, 직렬화를 위한 고유 ID - UnicastRemoteObject는 Serializable을 구현하고 있으므로 UID가 필요
    }
}

fun main() {
    try {
        val service = MyServiceImpl()
        // 프로매틱한 방법으로 서버가 실행 중인 JVM 내의 포트 1099에 RMI 레지스트리를 생성
        val registry: Registry = LocateRegistry.createRegistry(1099)
        // 레지스트리에 service 등록
        registry.rebind("MyService", service)

        println("MyService is ready.")
    } catch (e: RemoteException) {
        println("RemoteException occurred: $e")
    } catch (e: Exception) {
        println("Unknown error occurred: $e")
    }
}
