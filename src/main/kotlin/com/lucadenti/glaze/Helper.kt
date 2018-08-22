package com.lucadenti.glaze

import javafx.animation.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.text.Text
import javafx.util.Duration

object Helper {
    fun typeWrite(text: Text) {
        var i = SimpleIntegerProperty(0)
        var timeline = Timeline()
        val string = text.text

        timeline.setCycleCount(string.length + 1)
        timeline.keyFrames.add(
                KeyFrame(
                        Duration.millis(100.0),
                        EventHandler<ActionEvent> {
                            if (i.get() > string.length) {
                                timeline.stop()
                            } else {
                                text.text = string.substring(0, i.get())
                                Sound.playClick() //TODO: update this
                                i.set(i.get() + 1)
                            }
                        }
                )
        )
        timeline.play()
    }

    fun positive(digit: Int): Int {
        return if(digit >= 0) digit else 0
    }
}