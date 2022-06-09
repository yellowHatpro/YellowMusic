package com.yellowhatpro.spotifyclone.data.local

import android.content.Context
import com.yellowhatpro.spotifyclone.data.entities.Song

class SongRepository(val context: Context) {
    suspend fun fetchSongs() : List<Song> = SongsData(context).fetchSongs()
}