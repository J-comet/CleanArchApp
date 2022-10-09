package hs.project.clonecleanarchapp.domain.common.login.usecase

import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginRequest
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginResponse
import hs.project.clonecleanarchapp.domain.common.base.BaseResult
import hs.project.clonecleanarchapp.domain.common.login.LoginRepository
import hs.project.clonecleanarchapp.domain.common.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository){
    suspend fun invoke(loginRequest: LoginRequest) : Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> {
        return loginRepository.login(loginRequest)
    }
}