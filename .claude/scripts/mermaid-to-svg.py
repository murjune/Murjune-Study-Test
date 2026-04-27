#!/usr/bin/env python3
"""
Mermaid → SVG/PNG 변환 스크립트
README.md 내 ```mermaid 블록을 이미지로 변환하고,
원본 mermaid 소스는 <details> 블록에 보존한다.

사용법:
    python3 .claude/scripts/mermaid-to-svg.py <README_PATH>
    python3 .claude/scripts/mermaid-to-svg.py <README_PATH> --format png
"""

import argparse
import re
import subprocess
import sys
import tempfile
from pathlib import Path


def convert_mermaid_blocks(readme_path: str, fmt: str = "svg") -> None:
    readme = Path(readme_path)
    if not readme.exists():
        print(f"파일을 찾을 수 없습니다: {readme_path}")
        sys.exit(1)

    content = readme.read_text(encoding="utf-8")

    images_dir = readme.parent / "images"
    images_dir.mkdir(exist_ok=True)

    pattern = re.compile(r"```mermaid\n(.*?)```", re.DOTALL)
    matches = list(pattern.finditer(content))

    if not matches:
        print("Mermaid 블록이 없습니다.")
        return

    print(f"{len(matches)}개 Mermaid 블록 발견 (포맷: {fmt})")

    bg = "transparent" if fmt == "svg" else "white"
    extra_args = ["-s", "2"] if fmt == "png" else []

    for i, match in enumerate(reversed(matches), 1):
        idx = len(matches) - i + 1
        mermaid_src = match.group(1).strip()
        img_name = f"diagram-{idx:02d}.{fmt}"
        img_path = images_dir / img_name

        with tempfile.NamedTemporaryFile(
            mode="w", suffix=".mmd", delete=False, encoding="utf-8",
        ) as tmp:
            tmp.write(mermaid_src)
            tmp_path = tmp.name

        try:
            cmd = ["mmdc", "-i", tmp_path, "-o", str(img_path), "-b", bg] + extra_args
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
            if result.returncode != 0:
                print(f"  [{idx}] 변환 실패: {result.stderr[:200]}")
                continue
        except Exception as e:
            print(f"  [{idx}] 에러: {e}")
            continue
        finally:
            Path(tmp_path).unlink(missing_ok=True)

        replacement = (
            f"![diagram-{idx:02d}](images/{img_name})\n"
            f"\n"
            f"<details>\n"
            f"<summary>Mermaid 소스</summary>\n"
            f"\n"
            f"```mermaid\n"
            f"{mermaid_src}\n"
            f"```\n"
            f"\n"
            f"</details>"
        )

        content = content[:match.start()] + replacement + content[match.end():]
        print(f"  [{idx}] {img_name} 변환 완료")

    # 이전 포맷 파일 정리
    old_ext = "png" if fmt == "svg" else "svg"
    for old_file in images_dir.glob(f"diagram-*.{old_ext}"):
        old_file.unlink()
        print(f"  정리: {old_file.name} 삭제")

    readme.write_text(content, encoding="utf-8")
    print(f"\n완료! {len(matches)}개 다이어그램 → {images_dir}/")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Mermaid → 이미지 변환")
    parser.add_argument("readme", help="README.md 경로")
    parser.add_argument("--format", choices=["svg", "png"], default="svg", help="출력 포맷 (기본: svg)")
    args = parser.parse_args()
    convert_mermaid_blocks(args.readme, args.format)
