package com.lucadenti.glaze

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.layout.StackPane.setAlignment
import javafx.scene.layout.Pane
import javafx.scene.text.Text

object LeftSide {
    // PROPERTIES
    val lside = Pane()

    // Position
    val lsideX = 0.0
    val lsideY = 0.0

    var screenWidth = Glaze.screenSize["width"]!!
    var width = setWidth(screenWidth)
    //var screenWidth = SimpleDoubleProperty(Glaze.screenSize["width"]!!).value
    var height = Paper.paperSize["height"]!!.value
    val border = 10.0

    private fun setWidth(screenWidth: Double): Double {
        return (screenWidth - Paper.paperSize["width"]!!.value) / 2 - (border * 2)
    }

    /**
     * Set the menu width using active screen measure
     */
    fun setSize() {
        //screenWidth = SimpleDoubleProperty(Glaze.screenSize["width"]!!).value
        lside.setMaxSize(width, height)
        lside.setPrefSize(width, height) //TODO: check height: now it's shorter than paper
    }

    private var dataText = mutableMapOf<String, Any>(
            "Cursor location" to listOf(0, 0),
            "Paper size" to listOf(Paper.paperSize["width"]?.value, Paper.paperSize["height"]?.value)
    )

    private var title1 = Text()
    private var data1 = Text()
    private var title2 = Text()
    private var data2 = Text()

    private fun setText(posX: Double, posY: Double, content: String, typable: Boolean): Text {
        val text = Text(posX, posY, content)
        text.styleClass.add("text")
        if (typable) Helper.typeWrite(text)
        return text
    }

    private fun setText(posX: Double, posY: Double, data: List<Double>): Text {
        var text = Text()
        var stringContent = ""
        for (d in data) {
            stringContent += "${d.toInt()} | "
        }
        text = Text(posX, posY, Regex("[|] $").replace(stringContent, ""))
        text.styleClass.add("text")
        return text
    }

    private fun <T> getValue(index: Int): T {
        return dataText.values.elementAt(index) as T
    }

    private fun getText(typable: Boolean) {
        val pointerPosition = getValue<List<Double>>(0)
        val paperSize = getValue<List<Double>>(1)

        title1 = setText(20.0, 40.0, "Paper size : ", typable)
        data1 = setText(200.0, 40.0, paperSize)

        title2 = setText(20.0, 80.0, "Cursor location : ", typable)
        data2 = setText(200.0, 80.0, pointerPosition)

        lside.children.addAll(title1, data1, title2, data2)
    }

    fun addText(hasText: Boolean) {
        if (!hasText) {
            lside.children.clear()
            return
        }

        getText(typable = true)

        // Dynamic retrieved text after change
        Pen.getChanges().subscribe {
            // reset
            lside.children.removeAll(title1, data1, title2, data2)
            // refresh
            dataText["Cursor location"] = listOf(Helper.positive(it.penLocation.x.toInt()), Helper.positive(it.penLocation.y.toInt()))
            getText(typable = false)
        }
    }

    fun removeText() {
        addText(false)
    }

    fun getBox(): Pane {
        setSize()

        StackPane.setMargin(lside, Insets(Menu.height, border + 5, 0.0, border - 5))
        setAlignment(lside, Pos.CENTER_LEFT)
        lside.id = "left_side_box"
        lside.style = "-fx-shape: \"\n" +
                "M $lsideX, $lsideY" +
                "H ${width - 18}" +
                "L ${width - 18}, 0 ${width}, 20" +
                "V ${height - 20}" +
                "H 0" +
                "Z\";"

        showText()
        lside.stylesheets.add("/styles/LeftSide.css")
        return lside
    }

    val showText = {
        Glaze.getChanges().subscribe {
            if (it.isPointerRadarBoxVisible) addText(true) else removeText()
        }
    }
}