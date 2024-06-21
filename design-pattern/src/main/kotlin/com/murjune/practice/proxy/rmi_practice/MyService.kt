package designpattern.proxy.rmi_practice

import java.rmi.Remote

/**
 * 원격 인터페이스 : 클라이언트가 원격으로 호출할 메서드를 정의한다
 * 클라이언트에서 이 인터페이스의 구현체를 사용한다.
 * 즉, 스텁과 실제 서비스는 이 인터페이스의 구현체인 것!
 *
 * 1) Remote를 확장 - Remote는 Marker인터페이스 ( 원격 호출을 지원할 수 있다는 의미)
 * */
interface MyService : Remote {
    /**
     * sayHello() 메서드는 원격으로 호출할 메서드
     *
     * @exception RemoteException : 원격 호출이 실패할 경우 발생 (원격 호출이 실패할 경우 발생)
     * */
    @Throws(java.rmi.RemoteException::class)
    fun sayHello(): String
}
