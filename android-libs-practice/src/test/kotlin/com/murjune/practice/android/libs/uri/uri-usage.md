## Uri 에 대한 개인적인 생각

많은 개발자들이 android.net.Uri 사용에 대해 거부감을 가지는 경우를 많이 보았다.
그 이유는 Uri를 Presenter나 Domain Layer에서 사용하면 Android 프레임워크에 의존성이 생기기 때문이다.

하지만 실제 Android 개발에서는 다음과 같이 Uri를 다루는 일이 꽤나 자주 발생한다.
- 딥링크(AppLink, DeepLink)
- 웹 URL
- 인텐트(Intent)
- 콘텐츠 프로바이더(ContentProvider)

이때 `android.net.Uri`는 인코딩, 디코딩, 쿼리 파라미터 추출 등 여러 면에서 매우 유용하다.
물론 나 역시 ViewModel이나 UseCase 등 Domain Layer에서 `android.net.Uri`를 직접 사용하는 것은 지양한다.
대신, 아래와 같이 UI Layer 에서 Uri를 파싱하거나 생성하는 작업을 처리하는 것을 선호한다.

```kotlin
// UI Layer (Activity, Fragment 등)에서 Uri를 처리하는 예시
fun handleDeepLink() {
    val uri = intent?.data ?: return

    if (isValidUri(uri)) {
        val bookId = uri.getQueryParameter("bookId")
        viewModel.loadBookDetails(bookId)
    } else {
        goToMainScreen()
    }
}

```