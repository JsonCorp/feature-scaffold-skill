#!/usr/bin/env bash
#
# feature-scaffold 스킬을 로컬 .claude/skills/ 에 설치합니다.
#
# 사용법:
#   ./install.sh                 현재 디렉토리(프로젝트)의 .claude/skills/ 에 설치
#   ./install.sh <project-dir>   지정한 프로젝트 디렉토리에 설치
#   ./install.sh --global        ~/.claude/skills/ 에 전역 설치 (모든 프로젝트에서 사용)
#   ./install.sh --help          도움말
#
# 설치 후 Claude Code에서:
#   /feature-scaffold Profile
#   /feature-scaffold Profile:id,email,age:Int
#   /feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean
#
set -euo pipefail

SKILL_NAME="feature-scaffold"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC="$SCRIPT_DIR/.claude/skills/$SKILL_NAME"

GLOBAL=false
TARGET="."

while [ $# -gt 0 ]; do
  case "$1" in
    -g|--global) GLOBAL=true; shift ;;
    -h|--help)
      sed -n '2,16p' "$0" | sed 's/^# \{0,1\}//'
      exit 0 ;;
    *) TARGET="$1"; shift ;;
  esac
done

if [ ! -d "$SRC" ]; then
  echo "오류: 스킬 원본을 찾을 수 없습니다: $SRC" >&2
  echo "이 스크립트는 레포 루트에서 실행해야 합니다." >&2
  exit 1
fi

if $GLOBAL; then
  DEST_DIR="$HOME/.claude/skills"
else
  DEST_DIR="$TARGET/.claude/skills"
fi

mkdir -p "$DEST_DIR"
rm -rf "$DEST_DIR/$SKILL_NAME"
cp -r "$SRC" "$DEST_DIR/$SKILL_NAME"

echo "✅ 설치 완료: $DEST_DIR/$SKILL_NAME"
echo
echo "Claude Code에서 이렇게 사용하세요:"
echo "  /feature-scaffold Profile"
echo "  /feature-scaffold Profile:id,email,age:Int"
echo "  /feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean"
echo
echo "베이스 패키지는 .claude/skills/$SKILL_NAME/config.md 의 base_package 한 줄로 바꿀 수 있습니다."
