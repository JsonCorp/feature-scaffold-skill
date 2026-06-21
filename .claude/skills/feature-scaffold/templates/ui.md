# UI 레이어 템플릿

모든 파일은 첫 줄에 `package` 선언을 둡니다. 베이스 패키지는 `<base>`(기본값 `com.example.app`)이며,
UI 레이어 경로는 `<base>.feature.<소문자 기능명>.ui` 입니다.

## UiState
sealed interface로 로딩, 성공, 실패를 표현합니다.

```kotlin
package com.example.app.feature.profile.ui

import com.example.app.feature.profile.domain.model.Profile

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: Profile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
```

## ViewModel
```kotlin
package com.example.app.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            // runCatching은 CancellationException까지 삼켜 구조적 동시성을 깨뜨리므로 사용하지 않습니다.
            try {
                _uiState.value = ProfileUiState.Success(getProfileUseCase(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}
```

## Screen
```kotlin
package com.example.app.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
