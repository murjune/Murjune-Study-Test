# Navigation 챌린지 체크리스트

## Step 0: TopLevel Preparation


## Step 1: Route 정의 (`ShoppingRoutes.kt`)
- [ ] `@Serializable` object `Home`, `Cart`, `My` 선언
- [ ] `@Serializable` data class `ProductDetail(productId: Int)` 선언
- [ ] `@Serializable` data class `Review(productId: Int)` 선언
- [ ] `@Serializable` object `OrderHistory` 선언
- [ ] `@Serializable` data class `OrderDetail(orderId: String)` 선언
- [ ] `@Serializable` object/data object `Setting` 선언

## Step 2: 기본 NavHost 구성 (`ShoppingNavHost.kt`)
- [ ] `Scaffold` + `NavHost` 기본 구조 작성
- [ ] `BottomNavigationBar` 에 Home, Cart, My, Setting 탭 추가
- [ ] 각 탭 화면에 간단한 UI (Text) 배치

## Step 3: 탭 전환 로직
- [ ] `navigateToTab()` 확장함수 구현 (popUpTo + saveState + restoreState + launchSingleTop)
- [ ] 탭 전환 시 내부 백스택 보존 확인

## Step 4: 하위 화면 네비게이션
- [ ] Home → ProductDetail 이동 구현
- [ ] ProductDetail → Review 이동 구현
- [ ] My → OrderHistory 이동 구현
- [ ] OrderHistory → OrderDetail 이동 구현
- [ ] 각 하위 화면에 TopAppBar + `navigateUp()` 뒤로가기 구현

## Step 5: BottomBar 표시/숨김
- [ ] `currentDestination`으로 현재 화면 판별
- [ ] 탭 화면(Home, Cart, My)에서만 BottomBar 표시
- [ ] 하위 화면에서 BottomBar 숨김

## Step 6: 특수 동작
- [ ] ProductDetail "장바구니 담기" → Cart 이동 + ProductDetail 백스택 제거 (`popUpTo<Home>`)
- [ ] DeepLink 등록: `navDeepLink<ProductDetail>(basePath = "https://study.example.com/product")`

## Step 7: Setting 전체화면 탭 (추가 챌린지)
- [ ] Setting을 일반 destination으로 등록 (탭 destination이 아님)
- [ ] Setting 탭 클릭 → BottomBar 없이 전체화면
- [ ] Setting에서 뒤로가기 → 이전 탭 + BottomBar 복귀

## Step 8: 테스트 작성 (`ShoppingNavigationTest.kt`)
- [ ] 테스트 1: 기본 탭 전환 (Home → Cart → My)
- [ ] 테스트 2: popUpTo 동작 (ProductDetail → Cart 시 ProductDetail 제거)
- [ ] 테스트 3: 탭 내부 스택 보존 (Home → Detail → Cart → Home 복귀 시 Detail 유지)
- [ ] 테스트 4: navigateUp 동작 (하위 화면 → 이전 화면 복귀)
- [ ] 테스트 5: DeepLink 진입 (Intent로 ProductDetail 직접 진입)
- [ ] 테스트 6: Setting 전체화면 (BottomBar 숨김 확인)
- [ ] 테스트 7: Setting 뒤로가기 (이전 탭 + BottomBar 복귀)
