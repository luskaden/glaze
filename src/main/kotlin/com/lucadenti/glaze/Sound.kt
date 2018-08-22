package com.lucadenti.glaze

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File

object Sound {
    fun playClick() {
        val clickPath = System.getProperty("user.dir") + "/src/main/resources/sounds/click.mp3"
        val clickFile = File(clickPath).toURI().toString()
        val click = Media(clickFile)
        val mediaPlayer = MediaPlayer(click)
        mediaPlayer.play()
    }
}