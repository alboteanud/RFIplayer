package com.craiovadata.rfiplayer

data class RadioStation(
    val nameResId: Int,
    val url: String,
    val buttonTextResId: Int = nameResId,
)
