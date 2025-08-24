Kotest 에서는 `5.4.x` 버전 이후로 Robolectric 와의 연동 지원을 공식적으로 종료 했다.
- [관련 문서](https://kotest.io/docs/5.4.x/extensions/robolectric.html)

그래서, Kotest 가 지원하는 StringSpec, BehaviorSpec 과 같은 기능들은 사용할 수 없다.



Kotest 에서는 [Thrid-Party/kotest-android](https://github.com/LeoColman/kotest-android)를 활용하면 사용할 수 있다고는
하나 Kotest가 공식적으로 지원하는 기능이 아니기에.. 사용하지 않으려한다.

`kotest-runner-junit5` 와 `de.mannodermaus.junit5.AndroidJUnit5Builder` 를 같이 쓰면 Should 어설션을 사용할 수 있으니 
크게 불만은 없긴 하다.
