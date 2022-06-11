package com.yellowhatpro.yellowmusic.di

import android.content.Context
import com.yellowhatpro.yellowmusic.data.local.SongsData
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
    ): SongsData = SongsData(context)
}

