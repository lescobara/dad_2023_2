package com.unal.reto6

import android.content.Context
import android.media.MediaPlayer

class SoundManager (context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var robotMoveMediaPlayer: MediaPlayer? = null
    private var win : MediaPlayer? = null
    private var loose : MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer.create(context,R.raw.player)
        robotMoveMediaPlayer = MediaPlayer.create(context,R.raw.pc)
        win = MediaPlayer.create (context,R.raw.winner)
        loose = MediaPlayer.create (context,R.raw.loose)
    }

    fun playPlayerSound() {
        mediaPlayer?.start()
    }

    fun playRobotSound() {
        robotMoveMediaPlayer?.start()
    }

    fun playWinSound(){
        win?.start()
    }

    fun playLooseSound(){
        loose?.start()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        robotMoveMediaPlayer?.release()
        win?.release()
        loose?.release()
        mediaPlayer = null
        robotMoveMediaPlayer = null
        win = null
        loose = null
    }
}