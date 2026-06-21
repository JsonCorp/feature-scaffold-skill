# 테스트 레이어 템플릿

테스트는 대응하는 프로덕션 코드와 동일한 패키지를 사용합니다.
모델 생성 시에는 해당 기능의 실제 필드(파싱 결과)로 채웁니다. 아래는 기본 필드(id, name) 예시입니다.
의존: JUnit4, MockK, kotlinx-coroutines-test.

## UseCase 테스트
```kotlin
package com.example.app.feature.profile.domain.usecase

import com.example.app.feature.profile.domain.model.Profile
import com.example.app.feature.profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetProfileUseCaseTest {

    private val repository: ProfileRepository = mockk()
    private val useCase = GetProfileUseCase(repository)

    @Test
    fun `invoke는 repository 결과를 그대로 반환한다`() = runTest {
        val expected = Profile(id = "1", name = "name")
        coEvery { repository.getProfile("1") } returns expected

        val result = useCase("1")

        assertEquals(expected, result)
    }
}
```

## ViewModel 테스트
`viewModelScope`가 `Dispatchers.Main`을 쓰므로 테스트 디스패처로 교체합니다.

```kotlin
package com.example.app.feature.profile.ui

import com.example.app.feature.profile.domain.model.Profile
import com.example.app.feature.profile.domain.usecase.GetProfileUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val getProfileUseCase: GetProfileUseCase = mockk()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load 성공 시 Success 상태가 된다`() = runTest(dispatcher) {
        val profile = Profile(id = "1", name = "name")
        coEvery { getProfileUseCase("1") } returns profile
        val viewModel = ProfileViewModel(getProfileUseCase)

        viewModel.load("1")
        advanceUntilIdle()

        assertEquals(ProfileUiState.Success(profile), viewModel.uiState.value)
    }
}
```
