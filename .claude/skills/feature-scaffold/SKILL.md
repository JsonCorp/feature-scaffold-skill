---
name: feature-scaffold
description: 클린 아키텍처 기능 모듈의 표준 골격(domain, data, ui 레이어 + Hilt DI + 테스트)을 한 번에 생성합니다. 새 화면이나 기능 작업을 시작할 때 사용합니다.
argument-hint: FeatureName[:field1[:Type],field2...]
disable-model-invocation: true
allowed-tools: Read Write Bash
---

# 기능 모듈 스캐폴딩

입력은 $ARGUMENTS 입니다. 이 입력으로 클린 아키텍처 골격을 생성합니다.

## 입력 파싱
입력 문법: `<FeatureName>[:field1[:Type],field2[:Type],...]`
- 콜론(`:`) 앞이 기능 이름, 뒤가 쉼표로 구분된 필드 목록입니다.
- 각 필드는 `이름` 또는 `이름:타입` 형태입니다. 타입을 생략하면 `String`으로 간주합니다.
- 필드 부분 전체를 생략하면 기본값 `id:String, name:String`을 사용합니다.
- 예) `Profile` → id, name / `Profile:id,email,age:Int` → id:String, email:String, age:Int
- 기능 이름은 PascalCase(예: Profile)를 클래스 접두사로, 소문자(예: profile)를 패키지 경로로 사용합니다.
- 부가 설명이 섞여 들어와도(예: "Profile 만들어줘") 첫 토큰에서 기능 이름과 필드만 추출합니다.

이 필드 목록은 도메인 모델, DTO, Mapper에 동일하게 반영합니다.

## 패키지 규칙
- 베이스 패키지는 **생성 직전에 `config.md`의 `base_package` 값을 읽어** 결정합니다. 호출 시 `--pkg=<패키지>` 인자가 있으면 그 값이 우선합니다. 둘 다 없을 때만 `com.example.app`을 씁니다.
- 템플릿의 모든 `com.example.app`은 위에서 결정한 베이스 패키지로 치환해 출력합니다. 이렇게 해야 "한 곳만 바꾸면 된다"가 실제로 성립합니다.
- 모든 .kt 파일 첫 줄에 `package` 선언을 넣습니다.
- 레이어별 패키지: `<base>.feature.<소문자 기능명>.<domain|data|ui>...`
  - 예) `com.example.app.feature.profile.domain.model`, `com.example.app.feature.profile.ui`
- 공통 네트워크 모듈: `<base>.core.network.di`
- 레이어 간 참조(도메인 모델, DTO, UseCase 등)는 명시적 `import`로 연결합니다. 템플릿에 import 형태가 포함되어 있습니다.

## 생성할 파일
`feature/<소문자 기능명>/` 아래에 다음을 만듭니다.

domain
- domain/model/<기능명>.kt: 도메인 모델 data class
- domain/repository/<기능명>Repository.kt: Repository 인터페이스
- domain/usecase/Get<기능명>UseCase.kt: 단일 책임 UseCase

data
- data/dto/<기능명>Dto.kt
- data/mapper/<기능명>Mapper.kt: Dto와 도메인 모델 변환
- data/api/<기능명>Api.kt: Retrofit 인터페이스
- data/repository/<기능명>RepositoryImpl.kt: Repository 구현, 생성자 주입
- data/di/<기능명>Module.kt: Hilt 모듈 (Api Provides + Repository Binds)

ui
- ui/<기능명>UiState.kt
- ui/<기능명>ViewModel.kt: HiltViewModel
- ui/<기능명>Screen.kt: Composable

test
- test/Get<기능명>UseCaseTest.kt: UseCase 단위 테스트
- test/<기능명>ViewModelTest.kt: ViewModel 상태 전이 테스트

## 반드시 지킬 규칙
- ViewModel은 UseCase에만 의존합니다. Repository를 직접 주입하지 않습니다.
- Repository 인터페이스는 domain, 구현체는 data에 둡니다.
- UI 상태는 StateFlow로 노출하고, 상태는 sealed interface로 표현합니다.
- DTO를 ui 레이어까지 노출하지 않습니다. 매핑은 Mapper에서 처리합니다.
- Hilt 모듈은 `core/network/di/NetworkModule`이 제공하는 공통 `Retrofit`을 주입받아 Api를 생성합니다. Retrofit을 기능별로 직접 생성하지 않습니다.
- Repository는 `@Binds`로, Api는 `@Provides`로 바인딩합니다.
- ViewModel에서 비동기 호출은 `try/catch`로 감싸고 `CancellationException`은 반드시 rethrow합니다. `runCatching`은 취소 예외까지 삼키므로 사용하지 않습니다.

## 코드 형태
각 파일의 형태는 templates 디렉토리를 기준으로 기능명에 맞게 치환합니다.
- domain 형태: templates/domain.md
- data 형태(DTO, Mapper, Api, RepositoryImpl, Hilt 모듈): templates/data.md
- ui 형태: templates/ui.md
- test 형태(UseCase, ViewModel 테스트): templates/test.md

## 사전 조건
`core/network/di/NetworkModule.kt`(OkHttpClient, Retrofit 공통 Provides)이 없으면 먼저 생성한 뒤 기능 모듈을 만듭니다. 이미 있으면 그대로 재사용합니다.

생성을 마치면 만든 파일을 트리로 정리해 보고합니다.
