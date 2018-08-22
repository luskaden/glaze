/*
  @author Luca Denti
  Created on Jul 24, 2018
*/
package com.lucadenti.glaze

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue

import javafx.geometry.Insets

import javafx.scene.ImageCursor
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

object Paper {
    // PROPERTIES
    private var paperType = "A4"
    var paperOrientation: Int = 0 //horizontal

    val canvas = StackPane()

    /** Set the (paper) size of the page */
    private val paperWidth = SimpleDoubleProperty(0.0).asObject()
    private val paperHeight= SimpleDoubleProperty(0.0).asObject()
    var paperSize: MutableMap<String, ObservableValue<Double>> = mutableMapOf(
            "width" to paperWidth,
            "height" to paperHeight)

    /** Fix a zoom ratio to have always the best landing size for the page */
    private fun getZoomRatio(size: Array<Int>): Double {
        val ratio = 0.9
        return (Glaze.screenSize["height"]!! - Menu.height )/ size.let { if (paperOrientation % 2 == 0) it.first() else it.last() } * ratio
    }

    /**
     * set the orientation of the page
     * @param list List<ObservableValue<Double>>
     * @param measure String
     * @return Pair<String, ObservableValue<Double>>
     */
    private fun setPaperOrientation(list: List<ObservableValue<Double>>, measure: String): Pair<String, ObservableValue<Double>> {
        val isHorizontal = list.first().value > list.last().value
        val isVertical = !isHorizontal

        // Which orientation is required?
        val isGoingHorizontal = paperOrientation % 2 == 0
        val isGoingVertical = !isGoingHorizontal

        val isReversedApplicable = (isGoingHorizontal && isVertical || isGoingVertical && isHorizontal)

        // it reverses paper measures
        val reversed = list.run { if (isReversedApplicable) reversed() else list }

        val position = {
            measure: String, list: List<ObservableValue<Double>>
            -> when (measure){
                "width" -> list.first()
                "height" -> list.last()
                else -> SimpleDoubleProperty(0.0).asObject()
            }
        }

        val orientedRow: (String) -> Pair<String, ObservableValue<Double>> = {
            measure: String -> measure to list.run {reversed}.let {position(measure, it)}
        }

        return orientedRow(measure)
    }

    /**
     * Set default paper measures and the new ones when orientation is changed
     * @param paperTypeSize: Array<Int>
     * @param canvas: StackPane
     */
    private fun setPaperSize(paperTypeSize: Array<Int>, canvas: StackPane) {
        val zoomRatio: Double = getZoomRatio(paperTypeSize)

        val pxPaperSize: List<Double> = paperTypeSize
                .map { m -> m.toDouble() }
                .map { m -> m * zoomRatio}

        // TODO: Everything is over engineered (for learning purposes): fix it soon!
        val observableWidth = SimpleDoubleProperty(pxPaperSize.first()).asObject()
        val observableHeight = SimpleDoubleProperty(pxPaperSize.last()).asObject()

        val listOfObservables: List<ObservableValue<Double>> = listOf(observableWidth, observableHeight)

        val orientedWidth = setPaperOrientation(listOfObservables, "width")
        val orientedHeight = setPaperOrientation(listOfObservables, "height")

        paperSize = mutableMapOf(orientedWidth, orientedHeight)

        canvas.setMaxSize(paperSize["width"]!!.value, paperSize["height"]!!.value)
        canvas.setPrefSize(paperSize["width"]!!.value, paperSize["height"]!!.value)
    }

    /**
     * Prepare and return the canvas/page
     * with proper measures, pointer, color and change orientation button(to be fixed soon)
     * @return StackPane
     */
    fun getPaper(): StackPane {
        // Update the paper origin taking in account the menu height
        StackPane.setMargin(canvas, Insets(Menu.height,0.0,0.0,0.0))

        //Custom Pointer
        val pointer = Image("/images/crosshair.png")
        canvas.cursor = ImageCursor(pointer, pointer.width / 2, pointer.height / 2)

        //Set paper as a specific type
        setPaperSize(PaperSizes.getSize(paperType), canvas)

        // Shadow under the paper
        val paperShadow = DropShadow()
        paperShadow.radius = 20.0
        paperShadow.offsetX = 3.0
        paperShadow.offsetY = 3.0
        paperShadow.color = Color.rgb(0, 0, 0, 0.5)

        canvas.effect = paperShadow

        // Set paper color and position
        canvas.background = Background(
                BackgroundFill(
                        Color.WHITE,
                        CornerRadii.EMPTY,
                        Insets(0.0)))

        // Bring paper to the front
        canvas.toFront()

        return canvas
    }

    /**
     * Change the orientation of the paper/canvas
     * resizing its width and the its height
     * @params canvas StackPane
     */
    fun changePaperOrientation() {
        paperOrientation += 1

        val paperSizeList = listOf(paperSize["width"]!!, paperSize["height"]!!)

        val orientedWidth = setPaperOrientation(paperSizeList, "width")
        val orientedHeight = setPaperOrientation(paperSizeList, "height")

        paperSize = mutableMapOf(orientedWidth, orientedHeight)

        setPaperSize(PaperSizes.getSize(paperType), canvas)
    }
}