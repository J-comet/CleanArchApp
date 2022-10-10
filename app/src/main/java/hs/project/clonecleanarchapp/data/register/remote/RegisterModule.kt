package hs.project.clonecleanarchapp.data.register.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hs.project.clonecleanarchapp.data.common.module.NetworkModule
import hs.project.clonecleanarchapp.data.register.remote.api.RegisterApi
import hs.project.clonecleanarchapp.data.register.remote.repository.RegisterRepositoryImpl
import hs.project.clonecleanarchapp.domain.common.register.RegisterRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class RegisterModule {

    @Singleton
    @Provides
    fun provideRegisterApi(retrofit: Retrofit) : RegisterApi {
        return retrofit.create(RegisterApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRegisterRepository(registerApi: RegisterApi) : RegisterRepository {
        return RegisterRepositoryImpl(registerApi)
    }
}