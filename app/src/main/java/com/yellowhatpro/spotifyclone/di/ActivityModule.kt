package com.yellowhatpro.spotifyclone.di

import android.content.Context
import com.yellowhatpro.spotifyclone.data.local.SongRepository
import com.yellowhatpro.spotifyclone.data.local.SongRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {
    @Provides
    fun providesContext(
        @ApplicationContext context: Context
    ): SongRepositoryImpl = SongRepositoryImpl(context)

}


@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class SongRepositoryModule {
    @Binds
    abstract fun bindsSongRepository(repository: SongRepositoryImpl?): SongRepository?
}