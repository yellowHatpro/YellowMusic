package com.yellowhatpro.yellowmusic.data.local

import android.content.Context
import com.yellowhatpro.yellowmusic.data.entities.Song

class SongRepository(val context: Context) {
    suspend fun fetchSongs() : List<Song> = SongsData(context).fetchSongs()
}