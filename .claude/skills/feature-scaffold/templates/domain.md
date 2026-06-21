# Domain 레이어 템플릿

모든 파일은 첫 줄에 `package` 선언을 둡니다. 베이스 패키지는 `<base>`(기본값 `com.example.app`),
기능 패키지는 소문자 기능명입니다. 예) `com.example.app.feature.profile.domain.model`

## 도메인 모델
파싱한 필드 목록을 그대로 프로퍼티로 만듭니다. 아래는 기본 필드(id, name) 예시입니다.

```kotlin
package com.example.app.feature.profile.domain.model

data class Profile(
    val id: String,
    val name: String,
    // 추가 필드가 있으면 val <이름>: <타입>, 형태로 이어 붙입니다.
)
```

## Repository 인터페이스
```kotlin
package com.example.app.feature.profile.domain.repository

import com.example.app.feature.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(id: String): Profile
}
```

## UseCase
단일 책임. operator invoke로 호출부를 간결하게 유지합니다.

```kotlin
package com.example.app.feature.profile.domain.usecase

import com.example.app.feature.profile.domain.model.Profile
import com.example.app.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(id: String): Profile = repository.getProfile(id)
}
```
