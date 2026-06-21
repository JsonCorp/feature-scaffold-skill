# 매번 반복하던 Android 기능 모듈, Claude Code Skills로 직접 자동화한 기록

클린 아키텍처로 Android 앱을 만들다 보면, 새 화면 하나를 추가할 때마다 거의 똑같은 파일을 손으로 다시 만듭니다. domain에 모델과 Repository 인터페이스, UseCase를 만들고, data에 Dto와 Mapper, Repository 구현체를 만들고, presentation에 UiState와 ViewModel, Screen을 만듭니다. 기능 하나당 여덟 개에서 열 개의 파일이고, 내용은 매번 거의 같습니다. 이름만 바뀝니다.

이 반복을 Claude Code의 Skills 기능으로 없애봤습니다. 단순히 "Claude에게 매번 부탁하는" 수준이 아니라, 우리 프로젝트의 레이어 규칙을 한 번 정의해두고 명령어 하나로 골격을 생성하도록 만든 과정입니다. 이 글은 제가 만들면서 정리한 그대로의 실습 가이드라, 따라 하면 똑같이 동작하는 스킬을 갖게 됩니다.

> **[화면 캡처 1]** 새 기능을 추가할 때마다 반복해서 만들던 파일 구조(프로젝트의 기존 feature 패키지 트리)를 한 장 넣으면, 어떤 보일러플레이트를 없애려는 건지 독자가 바로 이해합니다.

## Claude Skills를 먼저 짧게 정리하면

Claude Skills는 폴더 하나와 그 안의 `SKILL.md` 파일로 정의됩니다. 이 파일에 "무엇을, 어떻게" 할지를 적어두면 Claude가 자신의 도구 목록에 추가하고, 관련 상황에서 알아서 불러오거나 `/스킬이름` 으로 직접 호출할 수 있습니다.

여기서 CLAUDE.md와 헷갈리기 쉬운데, 둘의 결정적 차이는 로딩 시점입니다. CLAUDE.md는 매 대화에 항상 올라가는 상시 컨텍스트입니다. 반면 스킬의 본문은 필요할 때만 로드됩니다. 즉 길고 구체적인 절차나 템플릿을 적어둬도, 쓰지 않는 동안에는 토큰 비용이 거의 들지 않습니다. "기능 골격 만들기" 같은 가끔 쓰는 절차를 CLAUDE.md에 박아두면 다른 작업에서도 계속 컨텍스트를 차지하지만, 스킬로 빼두면 호출할 때만 펼쳐집니다. 이 점이 제가 스킬을 선택한 이유입니다.

스킬을 두는 위치는 두 가지를 주로 씁니다.

- 프로젝트 스킬: `.claude/skills/<스킬이름>/SKILL.md`. 해당 저장소에서만 동작하고, git에 커밋하면 팀원과 공유됩니다.
- 개인 스킬: `~/.claude/skills/<스킬이름>/SKILL.md`. 내 모든 프로젝트에서 쓸 수 있습니다.

이번 스킬은 "우리 프로젝트의 아키텍처 규칙"을 담는 것이라, 레포에 같이 커밋되는 프로젝트 스킬로 만들었습니다. 개인 환경 어디서나 쓰고 싶다면 경로만 `~/.claude/skills/` 로 바꾸면 됩니다.

## 무엇을 만들 것인가

`feature-scaffold` 라는 프로젝트 스킬을 만듭니다. 동작은 이렇습니다.

```
/feature-scaffold Profile
```

이렇게 호출하면 `Profile` 기능의 data, domain, presentation 레이어 골격이 우리 컨벤션에 맞게 한 번에 생성됩니다. 기능 이름만 바꿔서 어떤 화면에도 재사용할 수 있습니다.

준비물은 두 가지입니다. Claude Code가 설치되어 있어야 하고, 작업은 대상 Android 프로젝트의 루트 디렉토리에서 진행합니다.

## 1단계. 스킬 디렉토리 만들기

프로젝트 루트에서 스킬 폴더와 템플릿 폴더를 만듭니다.

```bash
mkdir -p .claude/skills/feature-scaffold/templates
```

여기서 핵심 규칙이 하나 있습니다. 스킬 디렉토리 이름이 그대로 명령어가 됩니다. 폴더 이름을 `feature-scaffold` 로 했으니 호출은 `/feature-scaffold` 가 됩니다.

> **[화면 캡처 2]** 위 명령 실행 직후 `tree .claude/skills` 또는 IDE 파일 탐색기에서 생성된 폴더 구조를 보여주면, 스킬의 물리적 형태가 한눈에 들어옵니다.

## 2단계. SKILL.md 작성하기

모든 스킬의 핵심 파일입니다. 구조는 두 부분입니다. `---` 사이의 YAML 프런트매터가 "언제, 어떤 권한으로" 쓸지를 정하고, 그 아래 마크다운 본문이 "무엇을 할지"를 정합니다.

아래 내용을 `.claude/skills/feature-scaffold/SKILL.md` 로 저장합니다.

```markdown
---
name: feature-scaffold
description: 클린 아키텍처 기능 모듈의 표준 골격(data, domain, presentation 레이어)을 한 번에 생성합니다. 새 화면이나 기능 작업을 시작할 때 사용합니다.
argument-hint: [FeatureName]
disable-model-invocation: true
allowed-tools: Write Bash(mkdir *)
---

# 기능 모듈 스캐폴딩

기능 이름은 $ARGUMENTS 입니다. 이 이름으로 클린 아키텍처 골격을 생성합니다.
- PascalCase(예: Profile)를 클래스 접두사로 사용합니다.
- 소문자(예: profile)를 패키지 경로로 사용합니다.

## 생성할 파일
`feature/<소문자 기능명>/` 아래에 다음을 만듭니다.

domain
- domain/model/<기능명>.kt: 도메인 모델 data class
- domain/repository/<기능명>Repository.kt: Repository 인터페이스
- domain/usecase/Get<기능명>UseCase.kt: 단일 책임 UseCase

data
- data/dto/<기능명>Dto.kt
- data/mapper/<기능명>Mapper.kt: Dto와 도메인 모델 변환
- data/repository/<기능명>RepositoryImpl.kt: Repository 구현, 생성자 주입

presentation
- presentation/<기능명>UiState.kt
- presentation/<기능명>ViewModel.kt: HiltViewModel
- presentation/<기능명>Screen.kt: Composable

## 반드시 지킬 규칙
- ViewModel은 UseCase에만 의존합니다. Repository를 직접 주입하지 않습니다.
- Repository 인터페이스는 domain, 구현체는 data에 둡니다.
- UI 상태는 StateFlow로 노출하고, 상태는 sealed interface로 표현합니다.
- DTO를 presentation 레이어까지 노출하지 않습니다. 매핑은 Mapper에서 처리합니다.

## 코드 형태
각 파일의 형태는 templates 디렉토리를 기준으로 기능명에 맞게 치환합니다.
- presentation 형태: templates/presentation.md
- domain 형태: templates/domain.md

생성을 마치면 만든 파일을 트리로 정리해 보고합니다.
```

프런트매터의 각 항목이 실제로 어떻게 동작하는지가 중요합니다.

- `description`: 단순 설명이 아니라 트리거입니다. Claude는 이 문구를 보고 스킬을 쓸지 판단합니다. 모호하면 영영 안 불립니다. 스킬은 과하게 불리기보다 덜 불리는 경향이 있어서, 어떤 상황에서 써야 하는지를 약간 단정적으로 적는 편이 좋습니다.
- `argument-hint`: `/feature-scaffold` 를 칠 때 자동완성에 `[FeatureName]` 힌트를 띄워줍니다. 어떤 인자를 넣어야 하는지 까먹지 않게 됩니다.
- `disable-model-invocation: true`: 파일을 생성하는 작업이라 Claude가 알아서 실행하면 곤란합니다. 이 옵션을 켜면 오직 내가 `/feature-scaffold` 로 호출할 때만 실행됩니다. 호출 시점을 내가 통제합니다.
- `allowed-tools`: 스킬이 활성화된 동안 Write와 mkdir을 매번 승인 없이 쓰도록 미리 허용합니다. 다른 도구는 평소 권한 설정을 그대로 따릅니다.

본문에서 `$ARGUMENTS` 는 호출할 때 넘긴 인자로 치환됩니다. `/feature-scaffold Profile` 이면 `$ARGUMENTS` 자리에 `Profile` 이 들어갑니다.

> **[화면 캡처 3]** 에디터에서 작성한 SKILL.md 전체 화면. 프런트매터와 본문이 한 화면에 보이도록 캡처하면, 글에서 설명한 구조와 바로 연결됩니다.

## 3단계. 템플릿 파일 추가하기

SKILL.md 본문은 500줄을 넘기지 않고 핵심만 담는 편이 좋습니다. 실제 코드 형태처럼 길어질 수 있는 부분은 별도 파일로 빼두고, 본문에서 참조만 합니다. 이렇게 분리한 파일은 Claude가 필요할 때만 읽습니다.

먼저 `.claude/skills/feature-scaffold/templates/presentation.md` 를 만듭니다.

````markdown
# Presentation 레이어 템플릿

## UiState
sealed interface로 로딩, 성공, 실패를 표현합니다.

```kotlin
sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: Profile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
```

## ViewModel
```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            runCatching { getProfileUseCase(id) }
                .onSuccess { _uiState.value = ProfileUiState.Success(it) }
                .onFailure { _uiState.value = ProfileUiState.Error(it.message ?: "알 수 없는 오류") }
        }
    }
}
```

## Screen
```kotlin
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is ProfileUiState.Loading -> { /* 로딩 UI */ }
        is ProfileUiState.Success -> { /* s.profile 렌더링 */ }
        is ProfileUiState.Error -> { /* s.message 표시 */ }
    }
}
```
````

이어서 `.claude/skills/feature-scaffold/templates/domain.md` 를 만듭니다.

````markdown
# Domain 레이어 템플릿

## 도메인 모델
```kotlin
data class Profile(
    val id: String,
    val name: String,
)
```

## Repository 인터페이스
```kotlin
interface ProfileRepository {
    suspend fun getProfile(id: String): Profile
}
```

## UseCase
단일 책임. operator invoke로 호출부를 간결하게 유지합니다.

```kotlin
class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(id: String): Profile = repository.getProfile(id)
}
```
````

여기까지 만들면 스킬 폴더 구조는 이렇게 됩니다.

```
.claude/skills/feature-scaffold/
├── SKILL.md
└── templates/
    ├── presentation.md
    └── domain.md
```

## 4단계. 스킬이 인식되는지 확인하기

Claude Code는 스킬 디렉토리의 변경을 세션 중에 감지합니다. 이미 `claude` 가 켜져 있었다면, SKILL.md를 추가하거나 수정한 내용이 재시작 없이 반영됩니다. 다만 세션 시작 시점에 없던 최상위 스킬 디렉토리를 새로 만든 경우에는 한 번 재시작이 필요합니다.

확인은 입력창에 `/feature` 까지만 쳐보면 됩니다. 자동완성 목록에 `feature-scaffold` 가 뜨고, 옆에 `[FeatureName]` 힌트가 보이면 정상입니다.

참고로 "What skills are available?" 라고 물어 목록을 확인하는 방법도 있지만, 이번처럼 `disable-model-invocation: true` 를 켠 스킬은 Claude의 상시 컨텍스트에 설명이 올라가지 않습니다. 그래서 이 질문에는 잡히지 않을 수 있고, `/` 자동완성 메뉴로 확인하는 쪽이 확실합니다. 이 부분은 직접 켜보고 양쪽을 비교해보면 차이가 분명히 보입니다.

> **[화면 캡처 4]** `/feature` 입력 시 뜨는 자동완성 목록. `feature-scaffold` 항목과 인자 힌트가 같이 보이는 순간을 잡으면 좋습니다.

## 5단계. 실행하고 결과 확인하기

이제 실제로 호출합니다.

```
/feature-scaffold Profile
```

프로젝트 `.claude/skills/` 에 둔 스킬이 `allowed-tools` 로 권한을 요구하기 때문에, 처음 실행할 때 해당 폴더를 신뢰할지 묻는 확인창이 한 번 뜰 수 있습니다. 승인하면 Claude가 SKILL.md의 규칙과 templates를 참고해 파일을 생성하고, 마지막에 만든 파일을 트리로 정리해 보고합니다.

> **[화면 캡처 5]** `/feature-scaffold Profile` 실행 직후, Claude가 파일을 생성하고 결과를 트리로 보고하는 화면. 이 글에서 가장 핵심이 되는 캡처입니다.

생성 결과는 대략 이런 형태가 됩니다.

```
feature/profile/
├── data/
│   ├── dto/ProfileDto.kt
│   ├── mapper/ProfileMapper.kt
│   └── repository/ProfileRepositoryImpl.kt
├── domain/
│   ├── model/Profile.kt
│   ├── repository/ProfileRepository.kt
│   └── usecase/GetProfileUseCase.kt
└── presentation/
    ├── ProfileUiState.kt
    ├── ProfileViewModel.kt
    └── ProfileScreen.kt
```

생성된 파일 몇 개를 열어, 템플릿의 규칙이 그대로 적용됐는지 확인합니다. ViewModel이 UseCase에만 의존하는지, UiState가 sealed interface로 표현됐는지를 보면 됩니다.

> **[화면 캡처 6]** 생성된 ProfileViewModel.kt 또는 ProfileUiState.kt를 IDE에서 연 화면. 규칙이 실제 코드로 반영된 결과를 보여주면 설득력이 올라갑니다.

마지막으로 팀과 공유하려면 스킬 자체를 커밋합니다.

```bash
git add .claude/skills/feature-scaffold
git commit -m "Add feature-scaffold skill"
```

이제 같은 저장소를 받은 팀원은 별도 설정 없이 `/feature-scaffold` 를 그대로 씁니다. 아키텍처 컨벤션이 문서가 아니라 실행 가능한 형태로 레포에 들어간 셈입니다.

## 실제로 써보니

며칠 돌려보고 느낀 점을 솔직하게 정리합니다.

좋았던 점부터 봅니다. 첫째, 일관성입니다. 사람이 손으로 만들면 누구는 UiState를 data class로, 누구는 enum으로 만드는 식의 편차가 생기는데, 스킬이 규칙을 강제하니 결과가 균일합니다. 둘째, 컨벤션이 레포에 함께 산다는 점입니다. 위키에 적힌 규칙은 아무도 안 읽지만, 명령어로 강제되는 규칙은 자연히 지켜집니다. 셋째, CLAUDE.md를 비대하게 만들지 않습니다. 가끔 쓰는 절차를 상시 컨텍스트에 넣지 않고 필요할 때만 펼치니, 다른 작업의 토큰 예산을 갉아먹지 않습니다.

아쉬운 점도 분명합니다. 우선 `description` 튜닝이 생각보다 중요합니다. 자동 호출을 쓰는 스킬이라면 설명이 조금만 모호해도 원하는 순간에 안 불립니다. 이번에는 수동 호출(`disable-model-invocation: true`)로 만들어 이 문제를 피했지만, 자동 트리거를 원한다면 설명을 다듬는 데 시간을 들여야 합니다. 다음으로 컨텍스트 비용입니다. 스킬은 한 번 호출되면 그 본문이 세션 내내 컨텍스트에 남습니다. 한 세션에서 여러 스킬을 계속 부르면 누적되므로, 본문은 짧고 단정하게 유지하는 편이 좋습니다. 마지막으로, 생성 결과는 반드시 리뷰해야 합니다. 골격은 정확히 나오지만 Mapper의 실제 변환 로직이나 엣지 케이스까지 알아서 채워주지는 않습니다. 스캐폴딩은 시작점을 줄여줄 뿐, 구현을 대신하지 않습니다.

정리하면, 단순 반복 골격 생성에는 확실히 값을 합니다. 다만 골격을 넘어 복잡한 코드 생성이 필요해지면 KSP 같은 정식 코드 제너레이터가 더 맞는 자리도 있습니다. 둘은 경쟁이 아니라 역할이 다릅니다. 스킬은 "팀의 작업 방식"을 가볍게 코드화하는 데 강합니다.

다음 단계로는 같은 방식으로 테스트 골격을 만드는 스킬, 또는 PR 요약 스킬을 추가해볼 생각입니다. 한 번 손에 익으니 "이 반복은 스킬로 빼면 되겠다" 하는 작업이 계속 눈에 들어옵니다.

직접 만들어보면 30분이면 첫 스킬이 동작합니다. 매번 똑같이 만들던 파일이 명령어 하나로 정리되는 순간이 생각보다 개운합니다.

---

태그: Claude Code, Claude Skills, Agent Skills, SKILL.md, 안드로이드 개발, 클린 아키텍처, Jetpack Compose, Hilt, 코틀린, AI 코딩 도구, 개발 생산성, 보일러플레이트 자동화
