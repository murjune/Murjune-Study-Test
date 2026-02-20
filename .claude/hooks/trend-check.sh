#!/bin/bash
# trend-check.sh — STUDY_BACKLOG.md staleness 체크
# 백로그가 7일 이상 업데이트되지 않으면 백그라운드 트렌드 업데이트를 트리거한다.

BACKLOG="$PWD/STUDY_BACKLOG.md"

# 백로그 파일 존재 확인
if [ ! -f "$BACKLOG" ]; then
  exit 0
fi

# git에서 STUDY_BACKLOG.md의 마지막 커밋 날짜 확인
LAST_COMMIT_EPOCH=$(git log -1 --format=%ct -- "$BACKLOG" 2>/dev/null)

if [ -z "$LAST_COMMIT_EPOCH" ]; then
  exit 0
fi

NOW_EPOCH=$(date +%s)
DIFF_DAYS=$(( (NOW_EPOCH - LAST_COMMIT_EPOCH) / 86400 ))

if [ "$DIFF_DAYS" -gt 7 ]; then
  echo "[TREND_UPDATE_NEEDED: ${DIFF_DAYS}일 경과]"
  echo "STUDY_BACKLOG.md가 ${DIFF_DAYS}일간 업데이트되지 않았습니다."
  echo "백그라운드에서 Task(subagent_type='general-purpose', run_in_background=true)로 /trend 스킬의 실행 단계를 수행하세요."
  echo "사용자에게 '백그라운드에서 최신 Android API 트렌드를 업데이트 중입니다'라고 안내한 후 사용자의 원래 요청을 처리하세요."
fi
