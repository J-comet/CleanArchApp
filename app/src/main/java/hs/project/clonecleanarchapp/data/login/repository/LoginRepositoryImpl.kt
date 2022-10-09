package hs.project.clonecleanarchapp.data.login.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.login.remote.api.LoginApi
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginRequest
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginResponse
import hs.project.clonecleanarchapp.domain.common.base.BaseResult
import hs.project.clonecleanarchapp.domain.common.login.LoginRepository
import hs.project.clonecleanarchapp.domain.common.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginApi: LoginApi) : LoginRepository {

    override suspend fun login(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> {
        return flow {
            val response = loginApi.login(loginRequest)
            if (response.isSuccessful) {
                val body = response.body()!!
                val loginEntity = LoginEntity(
                    body.data?.id!!,
                    body.data?.name!!,
                    body.data?.email!!,
                    body.data?.token!!
                )
                emit(BaseResult.Success(loginEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<LoginResponse>>() {}.type
                val err: WrappedResponse<LoginResponse> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }
}