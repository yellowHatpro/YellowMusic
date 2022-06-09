package com.yellowhatpro.spotifyclone.di

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yellowhatpro.spotifyclone.R
import com.yellowhatpro.spotifyclone.data.local.SongRepository
import com.yellowhatpro.spotifyclone.exoplayer.LocalMusicSource
import com.yellowhatpro.spotifyclone.exoplayer.MusicServiceConnection
import com.yellowhatpro.spotifyclone.exoplayer.MusicSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGlideInstance(@ApplicationContext context: Context) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun providesMusicServiceContext( @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

}
