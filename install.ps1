<#
.SYNOPSIS
  feature-scaffold 스킬을 로컬 .claude/skills/ 에 설치합니다.

.DESCRIPTION
  사용법:
    .\install.ps1                  현재 디렉토리(프로젝트)의 .claude/skills/ 에 설치
    .\install.ps1 -Target <dir>    지정한 프로젝트 디렉토리에 설치
    .\install.ps1 -Global          ~/.claude/skills/ 에 전역 설치 (모든 프로젝트에서 사용)

  설치 후 Claude Code에서:
    /feature-scaffold Profile
    /feature-scaffold Profile:id,email,age:Int
    /feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean
#>
param(
    [string]$Target = ".",
    [switch]$Global
)

$ErrorActionPreference = "Stop"

$SkillName = "feature-scaffold"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Src = Join-Path $ScriptDir ".claude\skills\$SkillName"

if (-not (Test-Path $Src)) {
    Write-Error "스킬 원본을 찾을 수 없습니다: $Src`n이 스크립트는 레포 루트에서 실행해야 합니다."
    exit 1
}

if ($Global) {
    $DestDir = Join-Path $HOME ".claude\skills"
} else {
    $DestDir = Join-Path $Target ".claude\skills"
}

New-Item -ItemType Directory -Force -Path $DestDir | Out-Null
$Dest = Join-Path $DestDir $SkillName
if (Test-Path $Dest) { Remove-Item -Recurse -Force $Dest }
Copy-Item -Recurse $Src $Dest

Write-Host "✅ 설치 완료: $Dest"
Write-Host ""
Write-Host "Claude Code에서 이렇게 사용하세요:"
Write-Host "  /feature-scaffold Profile"
Write-Host "  /feature-scaffold Profile:id,email,age:Int"
Write-Host "  /feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean"
Write-Host ""
Write-Host "베이스 패키지는 .claude\skills\$SkillName\config.md 의 base_package 한 줄로 바꿀 수 있습니다."
