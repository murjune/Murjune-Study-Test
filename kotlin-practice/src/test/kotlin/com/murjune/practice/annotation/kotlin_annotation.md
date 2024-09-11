
어노테이션이란? : 코드에 메타데이터를 추가하는 방법

말 그대로 어노테이션이 어떤 기능을 동작시키지 않는다. 
어노테이션을 사용하는 것은 컴파일러나 런타임에 어떤 특정 기능을 동작시키기 위한 `정보를 제공`하는 것이다.  

이 어노테이션 정보를 리플렉션을 통해 가져와서 특정 기능을 동작시킬 수 있다.
실제로 어노테이션을 사용하는 예시는 아래와 같다.

1. 컴파일러에게 코드 분석을 요청
2. 런타임에 코드 분석을 요청

Android에서는 주로 런타임에 코드 분석을 요청하는 경우가 많다.

ex1) @Override 어노테이션 View 에서

ex2)
Room 에서 @Dao 어노테이션을 지정하면 해당 Dao interface의 구현체를 생성해준다.
어떻게? @Dao 에는 `어떤 기능을 동작시킬지 정의`되어 있기 때문에 Room 라이브러리가 해당 어노테이션을 분석하여 구현체를 생성한다. 

# Kotlin Annotation

1) built-in annotation : 코틀린에서 제공하는 어노테이션 (코틀린 컴파일러가 특정 상황에서 사용)
   @JvmName, @JvmStatic, @JvmOverloads, @JvmField, @JvmMultifileClass, @JvmSynthetic, @JvmName, @JvmDefault
   @JvmName : 자바에서 사용할 때 메소드 이름을 변경
   @JvmStatic : 자바에서 정적 메소드로 사용
   @JvmOverloads : 자바에서 사용할 때 오버로딩된 메소드를 생성
   @JvmField : 자바에서 필드로 사용
   @JvmMultifileClass : 멀티파일 클래스로 사용
   @JvmSynthetic : 자바에서 사용할 때 synthetic 메소드로 사용
   @JvmName : 자바에서 사용할 때 메소드 이름을 변경
   @JvmDefault : 자바에서 사용할 때 디폴트 메소드로 사용

2) Meta-annotation : 어노테이션을 정의할 때 사용하는 어노테이션 (우리가 직접 만들 수 있음)
   컴파일러가 특정 어노테이션을 처리할 때 어떻게 처리할지 '정보'를 제공 --
   @Target, @Retention, @Repeatable, @MustBeDocumented, @Inherited
   @Target : 어노테이션이 적용될 대상을 지정
   @Retention : 어노테이션이 유지될 기간을 지정 - kotlin 에서는 기본적으로 RUNTIME 으로 지정 (자바는 CLASS)
   @Repeatable : 어노테이션을 반복해서 적용할 수 있도록 지정
   @MustBeDocumented : 어노테이션 정보가 javadoc으로 작성된 문서에 포함되도록 지정
   @Inherited : 어노테이션이 상속될 수 있도록 지정