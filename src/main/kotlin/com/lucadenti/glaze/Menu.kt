/*
  @author Luca Denti
  Created on Jul 24, 2018
*/
package com.lucadenti.glaze

import javafx.event.EventHandler
import javafx.geometry.Insets

import javafx.geometry.Pos
import javafx.scene.ImageCursor

import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.layout.StackPane.setAlignment
import javafx.scene.paint.Color

import kotlin.properties.Delegates

object Menu {
    // PROPERTIES
    private val glazeMenu = StackPane()

    const val height: Double = 50.0

    var menuWidth: Double by Delegates.observable(Glaze.screenSize["width"]!!) { _, _, newValue ->
        glazeMenu.setMaxSize(newValue, height)
        glazeMenu.setPrefSize(newValue, height)
    }

    /**
     * Set the menu width using active screen measure
     */
    fun setScreen() {
        Glaze.observableScreenSize.subscribe {
            glazeMenu.setMaxSize(it["width"]!!, height)
            glazeMenu.setPrefSize(it["width"]!!, height)
        }
    }

    fun getWidth(): Double {
        var result = 0.0
        Glaze.observableScreenSize.subscribe {
            result = it["width"]!!
        }
        return result
    }

    fun getMenu(): StackPane {
        // Set menu size, color and position
        glazeMenu.setMaxSize(menuWidth!!, height)
        glazeMenu.setPrefSize(menuWidth!!, height)

        setAlignment(glazeMenu, Pos.TOP_CENTER)

        glazeMenu.id = "menu_bar"

        // Add shadow under the menu
        val shadow = DropShadow()
        shadow.radius = 10.0
        shadow.offsetX = 0.0
        shadow.offsetY = 5.0
        shadow.color = Color.rgb(30, 30, 30, 0.8)

        glazeMenu.effect = shadow

        glazeMenu.stylesheets.add("/styles/Menu.css")

        // Add buttons on menu
        changeOrientationBtn(glazeMenu)
        setSeparator(glazeMenu)
        fullScreenBtn(glazeMenu)
        pointerRadarBtn(glazeMenu)

        return glazeMenu
    }

    private fun setSeparator(menu: Pane) {
        val separatorImage = Image("/images/separator.png")
        val separator = ImageView()

        separator.image = separatorImage
        separator.fitHeight = 30.0

        setAlignment(separator, Pos.CENTER_LEFT)
        StackPane.setMargin(separator, Insets(0.0, 0.0, 0.0, 30.0))

        menu.children.add(separator)
    }

    /**
     * Helper function to add style classes, position, actions and events to an unspecified menu button
     */
    private fun setButton(
            menu: Pane,
            styleClass: String,
            subClass: String?,
            button: Button,
            position: Pos,
            insets: List<Double>,
            action: () -> Unit,
            clickActions: List<() -> Unit>) {

        button.styleClass.add(styleClass)
        if (subClass != null) button.styleClass.add(subClass)

        setAlignment(button, position)
        StackPane.setMargin(button, Insets(insets[0], insets[1], insets[2], insets[3]))

        menu.children.add(button)

        button.onAction = EventHandler { action() }

        button.onMouseEntered = EventHandler {
            val cursorHover = Image("/images/desktop_cursor_hover.png")
            button.cursor = ImageCursor(cursorHover, cursorHover.width / 2, cursorHover.height / 2)
        }

        button.onMouseClicked = EventHandler {
            Sound.playClick()
            for (action in clickActions) {
                action()
            }
        }
    }

    /**
     * Add button specialization to change paper orientation to vertical
     * @param menu Pane
     * @param verticalBtn Button
     * @param horizontalBtn Button
     */
    private fun setVerticalBtn(menu: Pane, verticalBtn: Button, horizontalBtn: Button) {
        setButton(
                menu,
                "orientation_btn",
                "vertical",
                verticalBtn,
                Pos.CENTER_LEFT,
                listOf(0.0, 0.0, 0.0, 20.0),
                {Paper.changePaperOrientation()},
                listOf({orientationBtnBehaviour(verticalBtn, horizontalBtn)})
        )
    }

    /**
     * Add button specialization to change paper orientation to horizontal
     * @param menu Pane
     * @param verticalBtn Button
     * @param horizontalBtn Button
     */
    private fun setHorizontalBtn(menu: Pane, verticalBtn: Button, horizontalBtn: Button) {
        setButton(
                menu,
                "orientation_btn",
                "horizontal",
                horizontalBtn,
                Pos.CENTER_LEFT,
                listOf(0.0, 0.0, 0.0, 62.0),
                {Paper.changePaperOrientation()},
                listOf({orientationBtnBehaviour(verticalBtn, horizontalBtn)})
        )
    }

    /**
     * Set button to go fullscreen
     * @param menu Pane
     * @param fullScreenBtn Button
     */
    private fun setFullscreenBtn(menu: Pane, fullScreenBtn: Button) {
        setButton(
                menu,
                "fullscreen_btn",
                null,
                fullScreenBtn,
                Pos.CENTER_RIGHT,
                listOf(0.0, 20.0, 0.0, 0.0),
                {Glaze.toggleFullScreen()},
                listOf({setScreen()}, {fullscreenBtnBehaviour(fullScreenBtn)})
        )
    }

    /**
     * Set button to inspect mouse events
     * @param menu Pane
     * @param pointerRadarBtn Button
     */
    private fun setPointerRadarBtn(menu: Pane, pointerRadarBtn: Button) {
        setButton(
                menu,
                "pointer_radar_btn",
                null,
                pointerRadarBtn,
                Pos.CENTER_LEFT,
                listOf(0.0, 0.0, 0.0, 120.0),
                {Glaze.togglePointerRadarBox()},
                listOf({setScreen()}, {LeftSide.setSize()}, {pointerRadarBtnBehaviour(pointerRadarBtn)})
        )
    }

    /**
     * Set the orientation button
     * @param menu StackPane
     */
    private fun changeOrientationBtn(menu: Pane) {
        // To create buttons
        val verticalBtn = Button()
        val horizontalBtn = Button()

        // To set buttons and images
        setVerticalBtn(menu, verticalBtn, horizontalBtn)
        setHorizontalBtn(menu, verticalBtn, horizontalBtn)

        // Actions to be performed by default
        orientationBtnBehaviour(verticalBtn, horizontalBtn)
    }

    fun fullScreenBtn(menu: Pane) {
        // To create buttons
        val fullScreenBtn = Button()

        // To set buttons and images
        setFullscreenBtn(menu, fullScreenBtn)

        // Actions to be performed by default
        fullscreenBtnBehaviour(fullScreenBtn)
    }

    fun pointerRadarBtn(menu: Pane) {
        // To create buttons
        val pointerRadarBtn = Button()

        // To set buttons and images
        setPointerRadarBtn(menu, pointerRadarBtn)

        // Actions to be performed by default
        pointerRadarBtnBehaviour(pointerRadarBtn)
    }

    private fun orientationBtnBehaviour(verticalBtn: Button, horizontalBtn: Button) {
        horizontalBtn.isDisable = Paper.paperOrientation % 2 == 0
        verticalBtn.isDisable = !horizontalBtn.isDisable
    }

    private fun fullscreenBtnBehaviour(fullScreenBtn: Button) {
        if (Glaze.isFullScreen) {
            fullScreenBtn.styleClass.let {it.add("active") && it.remove("windowed")}
        } else {
            fullScreenBtn.styleClass.let {it.add("windowed") && it.remove("active")}
        }
    }

    private fun pointerRadarBtnBehaviour(pointerRadarBtn: Button) {
        if (Glaze.isPointerRadarBoxVisible) {
            pointerRadarBtn.styleClass.let {it.add("active") && it.remove("idle")}
        } else {
            pointerRadarBtn.styleClass.let {it.add("idle") && it.remove("active")}
        }
    }
}