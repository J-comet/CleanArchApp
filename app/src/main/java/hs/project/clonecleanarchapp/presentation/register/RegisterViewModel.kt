package hs.project.clonecleanarchapp.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.register.remote.dto.RegisterRequest
import hs.project.clonecleanarchapp.data.register.remote.dto.RegisterResponse
import hs.project.clonecleanarchapp.domain.common.base.BaseResult
import hs.project.clonecleanarchapp.domain.common.register.entity.RegisterEntity
import hs.project.clonecleanarchapp.domain.common.register.usecase.RegisterUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RegisterActivityState>(RegisterActivityState.Init)
    val state: StateFlow<RegisterActivityState>
        get() = _state

    private fun setLoading() {
        _state.value = RegisterActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = RegisterActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = RegisterActivityState.ShowToast(message)
    }

    private fun successRegister(registerEntity: RegisterEntity) {
        _state.value = RegisterActivityState.SuccessRegister(registerEntity)
    }

    private fun errorRegister(rawResponse: WrappedResponse<RegisterResponse>) {
        _state.value = RegisterActivityState.ErrorRegister(rawResponse)
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            registerUseCase.invoke(registerRequest)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            successRegister(result.data)
                        }
                        is BaseResult.Error -> {
                            errorRegister(result.rawResponse)
                        }
                    }
                }
        }
    }
}

sealed class RegisterActivityState {
    object Init : RegisterActivityState()
    data class IsLoading(val isLoading: Boolean) : RegisterActivityState()
    data class ShowToast(val message: String) : RegisterActivityState()
    data class SuccessRegister(val registerEntity: RegisterEntity) : RegisterActivityState()
    data class ErrorRegister(val rawResponse: WrappedResponse<RegisterResponse>) :
        RegisterActivityState()
}