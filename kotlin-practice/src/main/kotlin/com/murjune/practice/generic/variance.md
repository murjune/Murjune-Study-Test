## 변성 (variance)

List<String> 과 List<Any>와 같이 기저 타입이 같고 타입 인자가 다른 여러 타입이 
서로 어떤 관계가 있는지 설명하는 개념이다.  

다음 순으로 정리

1) 변성의 중요성
2) 코틀린에서는 변성을 어떻게 사용할 수 있는가
3) 변성을 잘 활용하면 타입 안정성을 보장하는 Api 를 만들 수 있다

## 변성이 있는 이유: 인자를 함수에 넘기기

List<Any