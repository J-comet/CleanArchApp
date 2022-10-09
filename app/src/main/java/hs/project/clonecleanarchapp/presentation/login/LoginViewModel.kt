package hs.project.clonecleanarchapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginRequest
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginResponse
import hs.project.clonecleanarchapp.domain.common.base.BaseResult
import hs.project.clonecleanarchapp.domain.common.login.entity.LoginEntity
import hs.project.clonecleanarchapp.domain.common.login.usecase.LoginUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _state = MutableStateFlow<LoginActivityState>(LoginActivityState.Init)
    val state : StateFlow<LoginActivityState> get() = _state

    private fun setLoading() {
        _state.value = LoginActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = LoginActivityState.IsLoading(false)
    }

    private fun showToast(msg: String) {
        _state.value = LoginActivityState.ShowToast(msg)
    }

    private fun successLogin(loginEntity: LoginEntity) {
        _state.value = LoginActivityState.SuccessLogin(loginEntity)
    }
    private fun errorLogin(rawResponse: WrappedResponse<LoginResponse>) {
        _state.value = LoginActivityState.ErrorLogin(rawResponse)
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginUseCase.invoke(loginRequest)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when(result) {
                        is BaseResult.Success -> successLogin(result.data)
                        is BaseResult.Error -> errorLogin(result.rawResponse)
                    }
                }
        }
    }
}

sealed class LoginActivityState {
    object Init : LoginActivityState()
    data class IsLoading(val isLoading: Boolean) : LoginActivityState()
    data class ShowToast(val message: String) : LoginActivityState()
    data class SuccessLogin(val loginEntity: LoginEntity) : LoginActivityState()
    data class ErrorLogin(val rawResponse: WrappedResponse<LoginResponse>): LoginActivityState()
}