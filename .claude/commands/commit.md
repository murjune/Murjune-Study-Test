---
description: Staged 된 파일들에 대해 커밋합니다.
allowed-tools:
  - Read
  - Write
  - Edit
  - Bash
  - Grep
  - Glob
---

# Git Commit with Code Review

Staged 파일을 리뷰하고 커밋 메시지를 생성하여 커밋합니다.

---

## 실행 단계

### 1. Staged 파일 확인

먼저 Staged된 파일 목록을 확인합니다:
- 만약, Staged 파일이 없으면 종료

```bash
git diff --name-only --cached
```
- Staged 파일이 없으면 종료
```bash
if [ -z "$staged_files" ]; then
    echo "❌ Staged 파일이 없습니다."
    echo "git add 명령어로 파일을 먼저 stage 하세요."
    exit 1
fi

echo "📝 Staged 파일 ($staged_count개):"
echo "$staged_files"
```

### 2. Staged 파일 리뷰

Staged 파일만 읽고 간단히 리뷰:

1. **변경 내용 파악**: `git diff --cached`로 변경사항 확인
2. **주요 변경사항 요약**:
   - 새로운 기능 추가
   - 버그 수정
   - 리팩토링
   - 문서 업데이트

### 3. 커밋 메시지 생성

**간결한 커밋 메시지** (50자 이하 제목):

- 타입: `feat`, `fix`, `refactor`, `docs`, `style`, `test`, `chore`
- 형식: `타입: 간결한 설명`

**예시**:
```
feat: Add user profile screen
fix: Resolve memory leak in LocationManager
refactor: Extract UserRepository interface
docs: Update README with setup instructions
```

**상세 설명** (선택사항, 필요시만):
```
feat: Add user profile screen

- Implement UserProfileScreen with Compose
- Add user avatar and name display
- Connect to UserViewModel
```

### 4. Git Commit 실행

```bash
# 커밋 메시지 생성 (heredoc 사용)
git commit -m "$(cat <<'EOF'
feat: Add user profile screen

- Implement UserProfileScreen with Compose
- Add user avatar and name display
- Connect to UserViewModel
EOF
)"

# 커밋 성공 확인
if [ $? -eq 0 ]; then
    echo "✅ 커밋 완료!"
    git log -1 --oneline
else
    echo "❌ 커밋 실패"
    exit 1
fi
```

---

## 커밋 메시지 가이드라인

### 타입 (Type)

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 코드 리팩토링 (기능 변경 없음)
- `docs`: 문서 수정
- `style`: 코드 포맷팅 (기능 변경 없음)
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드, 설정 파일 수정

### 제목 (Subject)

- **50자 이내**로 간결하게
- 명령형으로 작성 (Add, Fix, Update)
- 첫 글자 대문자
- 마침표 없음

### 본문 (Body) - 선택사항

- 제목으로 충분하면 생략
- 필요시에만 상세 설명 추가
- 무엇을, 왜 변경했는지 설명

---

## 주의사항

- **Staged 파일만** 커밋됨
- 커밋 메시지는 **간결하게** (제목 50자 이하)
- **coWorker CLAUDE 를 description에 포함하지마세요!**

---

## 리뷰 기준 (간단히만)

Staged 파일에서 **치명적 문제만** 체크:

- 🔴 메모리 누수 (Context 참조, Coroutine 미취소)
- 🔴 크래시 위험 (NPE, !! 사용)
- 🔴 무한 루프/재귀

심각한 문제 발견 시:
```
⚠️ 경고: 치명적 문제 발견!

[파일명:줄번호] - [문제 설명]

커밋을 진행하시겠습니까? (yes/no)
```

사용자가 "yes" 입력하면 커밋 진행, "no"면 중단.