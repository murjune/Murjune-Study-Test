# RxJava2 학습 모듈

## 개요

RxJava2 리액티브 프로그래밍을 학습하고, Kotlin Coroutine/Flow 와의 비교 분석을 통해 마이그레이션 전략을 수립하는 모듈입니다.

- **테스트 프레임워크**: Kotest
- 참고). Kotest 의 run 아이콘을 활용하기 위해서는 [Kotest](https://plugins.jetbrains.com/plugin/14080-kotest) 
  플러그인을 안스에 설치해야 한다.

## 학습 목표

### 1. 실무 배경 및 필요성
- **레거시 프로젝트 대응**: 기존 안드로이드 프로젝트에서 RxJava2 사용 빈도가 높음
- **마이그레이션**: 기존 기능 동작의 변경 없이 RxJava2 → Coroutine 전환을 위함
- **유지보수 역량**: 기존 RxJava2 코드의 이해와 수정 능력 확보

### 2. 핵심 학습 내용
- **기본 개념**: Observable, Observer, Scheduler, Operator 이해
- **비교 분석**: RxJava2 vs Coroutine/Flow 장단점 정리
- **동작 원리**: 내부 구현 메커니즘과 메모리 관리
- **마이그레이션 예시**: 실제 코드 변환 패턴과 주의사항

### 3. 실습 방향
- **학습 테스트**: 각 개념 동작 검증
- **성능 비교**: RxJava2 vs Coroutine 벤치마크
- **실제 사례**: 안드로이드 앱에서 자주 사용되는 패턴 구현
- **마이그레이션 실습**: 동일 기능을 양쪽으로 구현하여 비교

## 학습 구조

```
rxjava2/
├── src/test/kotlin/
│   ├── basic/           # 기본 개념 (Observable, Observer)
│   ├── operators/       # 연산자 학습
│   ├── schedulers/      # 스케줄러와 스레딩
│   ├── comparison/      # Coroutine과 비교
│   └── migration/       # 마이그레이션 패턴
└── README.md
```

## 실무 적용 포인트

- **메모리 누수 방지**: CompositeDisposable 사용 패턴
- **에러 핸들링**: onError vs try-catch 전략
- **스레드 관리**: 안드로이드 메인 스레드 고려사항
- **성능 최적화**: 백프레셔(Backpressure) 처리 방법