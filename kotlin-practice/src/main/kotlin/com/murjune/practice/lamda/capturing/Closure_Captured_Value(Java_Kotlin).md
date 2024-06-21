https://renuevo.github.io/kotlin/closure-java-and-kotlin/
https://jaeyeong951.medium.com/java-%ED%81%B4%EB%A1%9C%EC%A0%80-vs-kotlin-%ED%81%B4%EB%A1%9C%EC%A0%80-c6c12da97f94

# 람다는 지역변수를 어떻게 Capturing 할까? 

이번 아티클에서는 람다의 

kotlin 과 Java 에서의 람다 차이에 대해 설명할 것이다.


> 이 아티클에서 말하는 람다는 Lambda Expression 이다.

## Java

자바의 람다식에서는 익명 클래스와 달리 아래와 같은 제한이 있다.

- 람다식은 outer 지역 변수를 접근할 경우, finial 혹은 effectively final 이여야한다.
- 복사된 지역 변수의 값은 람다식 내부에서도 변경가능하다

> effectively final 에 대해서는 조금 이따 설명하겠다 ㅎ ㅎ
> Java 8 에 추가된 syntactic sugar 일종으로,
초기화된 이후 값이 한 번도 변경되지 않았다면 effectively final 이라고 할 수 있다.

## 위와 같은 특징이 생기는 이유

1) 람다식에서 참조하는 외부 지역 변수는 복사 값이다.

- 지역 변수는 Stack 영역에서 생성된다. 즉, 함수의 생명주기가 끝나면 지역변수는 Stack 에서 제거된다.

-> 메서드 내 지역변수를 참조하는 람다식을 리턴하는 메서드가 있을 경우 해당 메서드 block 이 끝나면 지역 변수가 스택에서
제거되면서 추후에 람다식이 수행될 때 참조될 수 없다.


- 즉, 지역 변수를 관리하는 스레드와 실행되는 스레드는 다를 수 있다.
=> 스택은 스레드의 고유의 영역

2) 람다식에서 사용되는 변수가 외부 지역변수인 경우

- 참조하고자하는 지역 변수가 final 혹은 effectively final 인 경우에만 접근할 수 있다.
=> 변경 가능한 경우

람다가 생성될 때 람다 식이 변수 값의 스냅샷을 캡처하여 잠재적인 동시성 문제를 방지하도록 보장합니다.


https://stackoverflow.com/questions/30026824/modifying-local-variable-from-inside-lambda

https://stackoverflow.com/questions/70149939/curious-about-lambda-capture-in-java

https://www.baeldung.com/java-lambda-effectively-final-local-variables

https://dev-jwblog.tistory.com/153
