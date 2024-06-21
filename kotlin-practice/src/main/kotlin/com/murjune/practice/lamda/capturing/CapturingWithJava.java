package com.murjune.practice.lamda.capturing;

import java.util.function.Consumer;

public class CapturingWithJava {

    // 람다식은 외부 범위의 변수를 캡처하여 사용할 수 있다.
    // 람다에서 캡쳐링을 하는 이유)
    // 만약, outer 함수가 종료될 경우, a 는 Stack 영역에서 제거된다.
    // 하지만, 람다식이 a 를 캡쳐하고 있다면, a 는 Heap 영역에 복사되어 람다식에서 사용할 수 있다.

    // 이 함수는 외부 범위에서 변수를 캡처하는 함수를 생성합니다.
    // (final) 지역 변수 a를 캡쳐하여 람다에서 사용합니다.
    private static Consumer<Void> createFunction1() {
        int a = 1; // 이 변수는 캡처(복사)되어 람다식 내부에서 사용된다.
        return unused -> System.out.println(a);
    }
    // a 에 final 을 붙이지 않아도 된다. 위에서 a를 수정하지 않기 때문이다.

    // final 이 아닌 지역 변수를 캡쳐할 경우 컴파일 에러가 발생한다.
    // 이유) 캡쳐한 a 가 가장 최신의 값으로 복사된 것인지 획신할 수 없기 때문이다.

    // 예시) outer 함수가 스레드 A 에서 실행되고, 람다식이 스레드 B 에서 실행될 경우
    // a 는 스레드 A의 지역 변수 즉 Stack 영역에 저장되어 있음
    // Stack 영역의 경우 스레드의 고유한 영역이기 때문에 다른 스레드(B)에서 접근할 수 없음
    // 따라서, 람다식에서 캡쳐한 a의 값이 최신인지 보장할 수 없음
    // 자바에서는 final 이 아닌 지역 변수를 캡쳐할 수 없도록 제한함
    private static Consumer<Void> createFunction2_1() {
        int a = 1;
        return unused -> System.out.println(a++);
    }

    private static Consumer<Void> createFunction2_2() {
        int a = 1;
        a++;
        return unused -> System.out.println(a);
    }

    // Effectively final: final 키워드가 선언되지 않았지만, 값이 재할당되지 않아 final 변수와 유사하게 동작하는 변수
    // 객체가 가리키는 참조를 변경하지 않으면 effectively final 이다.

    // 이번 예제에서는 final 키워드가 선언되지 않았지만, effectively final 인 변수를 캡쳐하는 함수를 생성한다.
    // inner 와 outer 의 실행환경이 다를지라도 캡쳐한 변수의 값이 최신인지 보장할 수 있다.

    // 이유는 ref 의 주소값이 변경되지 않고, ref 가 가리키는 객체의 필드값만 변경되기 때문이다.
    // ref 가 가리키는 객체의 경우 Heap 영역에 저장되어 있으므로
    // inner 와 outer 의 실행환경이 달라도 동일한 객체를 참조한다.

    private static Consumer<Void> createFunction3() {
        final var ref = new Object() {
            int a = 2;
        };
        return unused -> System.out.println(ref.a++);
    }

    private static Consumer<Void> createFunction4() {
        int a = 3;
        a = 4;
        final var ref = new Object() {
//            a++;
        };

        final Consumer<Void> consumer = new Consumer<Void>() {
            @Override
            public void accept(Void unused) {
                a++;
            }
        };

        return unused -> System.out.println(ref.a++);
    }
}
