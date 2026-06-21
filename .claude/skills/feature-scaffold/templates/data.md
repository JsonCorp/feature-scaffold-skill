# Data 레이어 템플릿

모든 파일은 첫 줄에 `package` 선언을 둡니다. 베이스 패키지는 `<base>`(기본값 `com.example.app`)입니다.

## DTO
도메인 모델과 동일한 필드 목록을 가집니다.

```kotlin
package com.example.app.feature.profile.data.dto

data class ProfileDto(
    val id: String,
    val name: String,
    // 도메인 모델과 같은 필드를 동일 순서로 둡니다.
)
```

## Mapper
모든 필드를 1:1로 매핑합니다.

```kotlin
package com.example.app.feature.profile.data.mapper

import com.example.app.feature.profile.data.dto.ProfileDto
import com.example.app.feature.profile.domain.model.Profile

fun ProfileDto.toDomain(): Profile = Profile(
    id = id,
    name = name,
    // 각 필드를 <이름> = <이름>, 형태로 매핑합니다.
)
```

## Api
Retrofit 인터페이스입니다. 엔드포인트는 실제 서버 스펙에 맞게 어노테이션(`@GET` 등)을 추가합니다.

```kotlin
package com.example.app.feature.profile.data.api

import com.example.app.feature.profile.data.dto.ProfileDto

interface ProfileApi {
    suspend fun getProfile(id: String): ProfileDto
}
```

## RepositoryImpl
```kotlin
package com.example.app.feature.profile.data.repository

import com.example.app.feature.profile.data.api.ProfileApi
import com.example.app.feature.profile.data.mapper.toDomain
import com.example.app.feature.profile.domain.model.Profile
import com.example.app.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
) : ProfileRepository {
    override suspend fun getProfile(id: String): Profile = api.getProfile(id).toDomain()
}
```

## Hilt 모듈
`core/network/di/NetworkModule`이 제공하는 공통 `Retrofit`을 그대로 주입받습니다. `Retrofit`을 직접 생성하지 않습니다.

```kotlin
package com.example.app.feature.profile.data.di

import com.example.app.feature.profile.data.api.ProfileApi
import com.example.app.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.app.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create(ProfileApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileBindingModule {

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
```
