package hs.project.clonecleanarchapp.data.common.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hs.project.clonecleanarchapp.infra.SharedPrefs

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    fun provideSharePref(@ApplicationContext context: Context): SharedPrefs {
        return SharedPrefs(context)
    }
}