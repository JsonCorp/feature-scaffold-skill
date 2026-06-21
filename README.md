# feature-scaffold-skill

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Claude Code](https://img.shields.io/badge/Claude%20Code-skill-d97757)](https://claude.com/claude-code)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Clean Architecture](https://img.shields.io/badge/architecture-clean-2ea44f)](#컨벤션)
[![GitHub last commit](https://img.shields.io/github/last-commit/JsonCorp/feature-scaffold-skill)](https://github.com/JsonCorp/feature-scaffold-skill/commits/main)
[![GitHub stars](https://img.shields.io/github/stars/JsonCorp/feature-scaffold-skill?style=social)](https://github.com/JsonCorp/feature-scaffold-skill/stargazers)

Android 클린 아키텍처 기능 모듈의 표준 골격을 한 번에 생성하는 [Claude Code](https://claude.com/claude-code) 스킬입니다.
기능 이름(과 선택적 필드)만 주면 domain / data / ui 레이어 + Hilt DI + 테스트 골격까지 정형화된 형태로 만들어 줍니다.

## 무엇을 만들어 주나

`/feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean` 한 줄로:

```
feature/article/                              com.example.app.feature.article
├── domain/
│   ├── model/Article.kt              도메인 모델 (data class)
│   ├── repository/ArticleRepository.kt   Repository 인터페이스
│   └── usecase/GetArticleUseCase.kt      단일 책임 UseCase
├── data/
│   ├── dto/ArticleDto.kt
│   ├── mapper/ArticleMapper.kt           DTO → 도메인 매핑
│   ├── api/ArticleApi.kt                 Retrofit 인터페이스
│   ├── repository/ArticleRepositoryImpl.kt
│   └── di/ArticleModule.kt               Hilt 모듈 (@Provides Api + @Binds Repository)
├── ui/
│   ├── ArticleUiState.kt                 sealed interface (Loading/Success/Error)
│   ├── ArticleViewModel.kt               HiltViewModel + StateFlow
│   └── ArticleScreen.kt                  Composable
└── test/
    ├── GetArticleUseCaseTest.kt          MockK 단위 테스트
    └── ArticleViewModelTest.kt           코루틴 테스트 디스패처 기반
```

완성된 예시는 [`examples/article`](examples/article)에서 확인할 수 있습니다.

## 설치

레포를 클론한 뒤 설치 스크립트를 실행하면 됩니다.

```bash
git clone https://github.com/JsonCorp/feature-scaffold-skill.git
cd feature-scaffold-skill
```

**macOS / Linux / Git Bash**

```bash
./install.sh                 # 현재 프로젝트의 .claude/skills/ 에 설치
./install.sh /path/to/proj   # 지정한 프로젝트에 설치
./install.sh --global        # ~/.claude/skills/ 에 전역 설치
```

**Windows (PowerShell)**

```powershell
.\install.ps1                 # 현재 프로젝트의 .claude\skills\ 에 설치
.\install.ps1 -Target C:\proj # 지정한 프로젝트에 설치
.\install.ps1 -Global         # ~\.claude\skills\ 에 전역 설치
```

<details>
<summary>스크립트 없이 수동 복사</summary>

```bash
# 프로젝트 전용
cp -r .claude/skills/feature-scaffold <your-project>/.claude/skills/

# 또는 전역
cp -r .claude/skills/feature-scaffold ~/.claude/skills/
```
</details>

## 사용법

```
/feature-scaffold <FeatureName>[:field1[:Type],field2[:Type],...]
```

- **기능 이름**: PascalCase가 클래스 접두사, 소문자가 패키지 경로가 됩니다. (예: `Profile` → `Profile*`, `feature/profile`)
- **필드**: 쉼표로 구분. `이름` 또는 `이름:타입` 형태이며 타입을 생략하면 `String`. 필드를 모두 생략하면 기본값 `id, name`.
- 부가 설명이 섞여 들어와도 첫 토큰에서 기능 이름과 필드만 추출합니다.

예시:

```
/feature-scaffold Profile
/feature-scaffold Profile:id,email,age:Int
/feature-scaffold Article:id,title,author,publishedAt:Long,likeCount:Int,isBookmarked:Boolean
```

## 컨벤션

- **레이어 의존성**: ViewModel은 UseCase에만 의존(Repository 직접 주입 금지). Repository 인터페이스는 domain, 구현체는 data.
- **DTO 격리**: DTO는 ui 레이어까지 노출하지 않고 Mapper에서 도메인 모델로 변환.
- **상태 표현**: UI 상태는 `StateFlow`로 노출하고 `sealed interface`로 표현.
- **취소 안전성**: ViewModel의 비동기 호출은 `try/catch`로 감싸고 `CancellationException`은 rethrow. (`runCatching` 미사용)
- **DI**: 공통 `Retrofit`은 `core/network/di/NetworkModule`이 제공하고 각 기능은 이를 주입받음. Repository는 `@Binds`, Api는 `@Provides`.

## 베이스 패키지 설정

생성되는 모든 코드의 패키지 루트는 [`.claude/skills/feature-scaffold/config.md`](.claude/skills/feature-scaffold/config.md)의 `base_package` 한 줄로 결정됩니다.
다른 프로젝트로 옮길 때 이 값만 바꾸면 됩니다. 호출 시 `--pkg=com.your.app` 인자로 1회성 오버라이드도 가능합니다.

## 라이선스

[MIT](LICENSE)
