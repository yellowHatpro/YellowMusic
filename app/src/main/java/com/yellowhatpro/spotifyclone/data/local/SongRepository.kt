package com.yellowhatpro.spotifyclone.data.local

import com.yellowhatpro.spotifyclone.data.entities.Song

interface SongRepository {
    suspend fun fetchSongs() : List<Song>
}