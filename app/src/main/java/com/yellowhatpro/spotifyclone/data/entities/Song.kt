package com.yellowhatpro.spotifyclone.data.entities

import android.net.Uri

data class Song (
    val mediaID : String="",
    val title : String="",
    val artist: String = "",
    val uri : Uri
)