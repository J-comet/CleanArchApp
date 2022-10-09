package hs.project.clonecleanarchapp.data.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hs.project.clonecleanarchapp.data.common.module.NetworkModule
import hs.project.clonecleanarchapp.data.login.remote.api.LoginApi
import hs.project.clonecleanarchapp.data.login.repository.LoginRepositoryImpl
import hs.project.clonecleanarchapp.domain.common.login.LoginRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class LoginModule {

    @Singleton
    @Provides
    fun provideLoginApi(retrofit: Retrofit) : LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(loginApi: LoginApi) : LoginRepository {
        return LoginRepositoryImpl(loginApi)
    }
}