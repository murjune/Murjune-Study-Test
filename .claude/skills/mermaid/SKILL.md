---
name: mermaid
description: "README 내 Mermaid 다이어그램을 SVG/PNG 이미지로 변환. Android Studio에서 볼 수 있도록 이미지 참조 자동 삽입."
allowed-tools:
  - Read
  - Edit
  - Bash
  - Glob
  - Grep
---

# Mermaid Skill — 다이어그램 이미지 변환

README.md 내 ```mermaid 블록을 이미지로 변환하여 Android Studio Markdown 프리뷰에서 볼 수 있게 한다.

## 사용법

```
/mermaid                           # 현재 프로젝트의 모든 README에서 mermaid 변환
/mermaid "path/to/README.md"       # 특정 파일만 변환
/mermaid --png                     # PNG로 변환 (기본: SVG)
/mermaid --png "path/to/README.md" # 특정 파일을 PNG로 변환
```

## 실행 단계

### 1. 대상 파일 탐색

- 경로가 지정된 경우 → 해당 파일만
- 경로 미지정 → `Grep`으로 프로젝트 전체에서 ` ```mermaid ` 블록이 포함된 README 파일 탐색

### 2. 변환 스크립트 실행

프로젝트 루트의 변환 스크립트를 실행:

```bash
python3 .claude/scripts/mermaid-to-svg.py <README_PATH> [--format png]
```

스크립트 동작:
1. README에서 ```mermaid 블록 추출
2. 각 블록을 `images/diagram-{번호}.svg` (또는 `.png`)로 변환
3. 원본 mermaid 블록을 다음으로 교체:
   - `![diagram-NN](images/diagram-NN.svg)` 이미지 참조
   - `<details>` 접힘 블록에 원본 Mermaid 소스 보존

### 3. 결과 보고

```
변환 완료:
- <파일명>: N개 다이어그램 → images/ 디렉토리
- 포맷: SVG (또는 PNG)
```

## 규칙

- **기본 포맷은 SVG** — `--png` 명시 시에만 PNG
- 이미 변환된 블록(`![diagram-` 참조가 이미 있는 경우)은 건너뜀
- `<details>` 블록의 원본 소스는 항상 보존
- 변환 후 images/ 디렉토리에 이전 포맷 파일이 있으면 정리
- `mmdc`가 설치되어 있지 않으면 `npm install -g @mermaid-js/mermaid-cli`로 자동 설치

## 의존성

- `@mermaid-js/mermaid-cli` (mmdc) — `npm install -g @mermaid-js/mermaid-cli`
- Python 3
