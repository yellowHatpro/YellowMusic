package com.yellowhatpro.spotifyclone.exoplayer

import com.yellowhatpro.spotifyclone.exoplayer.State.*

class LocalMusicSource {

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }
    fun whenReady(action: (Boolean)-> Unit):Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING){
            onReadyListeners += action
            false
        } else {
            action(state==STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}