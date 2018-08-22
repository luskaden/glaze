/*
  @author Luca Denti
  Created on Jul 24, 2018
*/
package com.lucadenti.glaze

import io.reactivex.Observable
import javafx.geometry.Insets

import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.ImageCursor
import javafx.scene.layout.*

import javafx.stage.Screen
import javafx.stage.Stage
import kotlin.math.nextDown
import kotlin.math.nextUp
import kotlin.properties.Delegates

object Glaze {
    /*
        Temporary stage object: once setScene(scene) is called,
        the mainStage will be updated
    */
    var mainStage: Stage = Stage()

    /**
     * To add a global watcher to listen for properties' state change
     */
    private val glazeWatcher = Watcher(this)
    private val changes = glazeWatcher.changes()

    // PROPERTIES
    var screenSize: MutableMap<String, Double> = mutableMapOf("width" to 0.0, "height" to 0.0)
    var observableScreenSize = Observable.just(screenSize)

    var isScreenMaximized by Delegates.observable(true) { _, _, _
        ->
        showStage(mainStage)
        glazeWatcher.emit(changes)
    }

    var isFullScreen by Delegates.observable(false) { _, _, _
        ->
        showStage(mainStage)
        LeftSide.addText(false)
        glazeWatcher.emit(changes)
    }

    var isPointerRadarBoxVisible by Delegates.observable(false) { _, _, _
        ->
        showLeftBox()
        glazeWatcher.emit(changes)
    }

    fun getChanges(): Observable<Glaze> {
        return changes
    }

    /**
     * Set the screen measures starting from primary monitor
     */
    private val cacheScreenSize = {
        val screen = Screen.getPrimary().visualBounds
        screenSize = mutableMapOf("width" to screen.width, "height" to screen.height)
    }()

    // Modules
    val desk = StackPane()
    private val menu = Menu.getMenu()
    private val paper = Paper.getPaper()
    val pointerRadarBox = LeftSide.getBox()

    /**
     * Set scene, desk with size and add a paper to draw on it
     * @param stage Stage
     */
    @JvmStatic
    fun setScene(stage: Stage) {
        mainStage = stage

        // set initial width and height starting from primary monitor
        cacheScreenSize

        // configuration constants
        val properties = PomProperties
        val stageTitle: String = properties.data.name.capitalize()

        // Create the desk where to set the drawing paper
        // desk = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));

        //desk.setMaxSize(screenSize["width"]!!.minus(20.0), screenSize["height"]!!.minus(20.0))
        //desk.setPrefSize(screenSize["width"]!!.minus(20.0), screenSize["height"]!!.minus(20.0))
        desk.setMaxSize(screenSize["width"]!!, screenSize["height"]!!)
        desk.setPrefSize(screenSize["width"]!!, screenSize["height"]!!)

        desk.background = Background(
                BackgroundFill(
                        Color.rgb(60, 60, 60),
                        CornerRadii.EMPTY,
                        Insets.EMPTY))

        // Put paper over the desk
        desk.children.add(paper)

        // Put menu over the desk
        desk.children.add(menu)

        // Radar Box
        showLeftBox()

        // Define Scene or the window, pratically
        //val width = screenSize["width"]!!.minus(2.0)
        //val height = screenSize["height"]!!.minus(36.0)

        val width = screenSize["width"]!!
        val height = screenSize["height"]!!.minus(36.0)// for windows 10 toolbar

        // Add desk to the scene
//        val screens = Screen.getScreens()
//
//        for (screen in screens) {
//            scene.width = screen.bounds.width
//        }

        val scene = Scene(desk, width, height, Color.DARKGRAY)

        scene.stylesheets.add("/styles/Scene.css")

        // Cursor for the desk
        val desktopCursor = Image("/images/desktop_cursor_round.png")
        scene.cursor = ImageCursor(desktopCursor, desktopCursor.width / 2, desktopCursor.height / 2)

        // Create the tablet pen
        Pen.getPen()

        //Call the pointer cursor
        stage.title = stageTitle
        stage.scene = scene

        showMaximized()

        showStage(stage)
    }

    fun showMaximized() {
        mainStage.maximizedProperty().addListener { _, _, newValue ->
            isScreenMaximized = newValue
        }
    }

    fun getWidth(stage: Stage): Double {
        val screens = Screen.getScreens()
        var widths = mutableListOf<Double>()
        var result = 0.0

        for (screen in screens) {
            widths.add(screen.bounds.width + 18.0)
        }

        for (width in widths.iterator()) {
            result = if (width.nextUp() != null) width.nextUp() else width.nextDown()
        }

        return result
    }

    fun showStage(stage: Stage) {
        stage.isFullScreen = isFullScreen
        stage.show()

        val width = getWidth(stage)
        val height = stage.height

        screenSize["width"] = width
        screenSize["height"] = height

        // This works when called and subscribed
        observableScreenSize = Observable.just(screenSize)

        // Menu part -> TODO: some alternatives? Listeners? Observables?
        Menu.menuWidth = screenSize["width"]!!
    }

    fun showLeftBox() {
        desk.children.let { if (isPointerRadarBoxVisible) it.add(pointerRadarBox) else it.remove(pointerRadarBox) }
    }

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
    }

    fun togglePointerRadarBox() {
        isPointerRadarBoxVisible = !isPointerRadarBoxVisible
    }
}