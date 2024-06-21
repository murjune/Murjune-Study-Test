package com.murjune.practice.lamda.closure;

public class Closure {
    // (Lexical) Closure.
    // 클로저는 함수가 선언될 당시의 환경(environment)을 기억했다가 나중에 호출되었을때 원래의 환경에 따라 수행되는 함수이다.
    // 이름이 클로저인 이유는 함수 선언 시의 scope(lexical scope)를 포섭(closure)하여 이후 실행될 때 이용하기 때문이다. 자주 '이름 없는 함수(익명함수)'와 혼동되곤 한다. 많은 언어의 익명함수가 closure를 포함하기 때문에 편하게 부를땐 서로 구분없이 부르기도 한다.

    // 렉시컬 스코프란(Lexical Scope)란?
    // 렉시컬 스코프는 함수를 어디서 호출했는지가 아니라 어디에 선언했는지에 따라 결정되는 것을 의미한다.
    // 즉, 함수가 선언된 시점에 상위 스코프가 결정된다.
    // 이것은 함수가 어디서 호출되는지에 따라 상위 스코프가 결정되는 동적 스코프와는 다르다.
    // 동적 스코프: 함수가 호출될 때 상위 스코프가 결정된다.
    // 자바는 렉시컬 스코프를 따르기 때문에 함수가 선언된 위치에 따라 상위 스코프가 결정된다.

    class A {

        void outer() {
            int a = 1; // Thread 의 stack 영역, stack 영역이 고유한 영역 Heap 이 공통
            a++;
            Runnable r = () -> { // Thread2
//                System.out.println(a);
            };
        }
    }

    public static void main(String[] args) {
        System.out.println(1);
    }
}
