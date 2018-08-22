package com.lucadenti.glaze

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.layout.StackPane.setAlignment
import javafx.beans.binding.Bindings
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

object LeftSide {
    // PROPERTIES
    val lside = StackPane()

    // Position
    val lsideX = 0.0
    val lsideY = 0.0

    var screenWidth = Glaze.screenSize["width"]!!
    var width = setWidth(screenWidth)
    //var screenWidth = SimpleDoubleProperty(Glaze.screenSize["width"]!!).value
    var height = Paper.paperSize["height"]!!.value
    val border = 10.0

    var textLabel = Label("")

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

    fun getBox(): StackPane {
        setSize()
//        lside.background = Background(
//                BackgroundFill(
//                        Color.rgb(60, 60, 60),
//                        CornerRadii.EMPTY,
//                        Insets.EMPTY))

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

        // shadow under the left side box
//        val shadow = DropShadow()
//        shadow.radius = 10.0
//        shadow.offsetX = 0.0
//        shadow.offsetY = 5.0
//        shadow.color = Color.rgb(30, 30, 30, 0.8)

        //lside.effect = shadow

        showText()
        lside.stylesheets.add("/styles/LeftSide.css")
        return lside
    }

    val dataText = mutableMapOf<String, String>(
            "pl.x" to Pen.penLocation.x.toInt().toString(),
            "pl.y" to Pen.penLocation.y.toInt().toString(),
            "paperWidth" to Paper.paperSize["width"]?.value.toString(),
            "paperHeight" to Paper.paperSize["height"]?.value.toString()
    )

    fun setTextContent(text: Text, typable: Boolean) {
        setAlignment(text, Pos.TOP_LEFT)
        StackPane.setMargin(text, Insets(30.0, 0.0, 60.0, 20.0))
        text.styleClass.add("text")
        if (typable) Helper.typeWrite(text)
    }

    fun setLabel(label: Label) {
        label.setWrapText(true)
        setAlignment(label, Pos.TOP_LEFT)
        StackPane.setMargin(label, Insets(30.0, 0.0, 60.0, 20.0))
        label.styleClass.add("label")
    }

    fun addText(hasText: Boolean) {
        if (!hasText) {
            lside.children.clear()
            return
        }

        // TITLE
        val title1 = Text("Cursor Location")
        setTextContent(title1, true)

        val title2 = Text("Paper size")
        setTextContent(title2, true)

        lside.children.add(textLabel)

        // OTHER TEXT
        Pen.getChanges().subscribe {
            val penLocation = it.penLocation
            // reset
            lside.children.removeAll(textLabel)
            // refresh
            dataText["pl.x"] = Helper.positive(it.penLocation.x.toInt()).toString()
            dataText["pl.y"] = Helper.positive(it.penLocation.y.toInt()).toString()
            var textFlow = TextFlow(
                    title1,
                    Text(dataText["pl.x"]!!),
                    Text(dataText["pl.y"]!!),
                    title2
            )
            textLabel = Label(null, textFlow)

//            textLabel.textProperty().bind(Bindings.concat(
//                    title1, " : ",
//                    " ", dataText["pl.x"]!!, "  |  ",
//                    " ", dataText["pl.y"]!!, "\n",
//                    title2
//            ))
            //setTextContent(_, false)
            lside.children.add(textLabel)
            textLabel.textProperty().unbind()
        }
    }

    fun removeText() {
        addText(false)
    }

    val showText = {
        Glaze.getChanges().subscribe {
            if (it.isPointerRadarBoxVisible) addText(true) else removeText()
        }
    }
}