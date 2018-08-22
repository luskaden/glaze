package com.lucadenti.glaze

import io.reactivex.Observable
import jpen.PButtonEvent
import jpen.PKind
import jpen.PKindEvent
import jpen.PLevel
import jpen.PLevelEvent
import jpen.PScrollEvent
import jpen.PenManager
import jpen.event.PenListener
import jpen.demo.StatusReport
import javafx.embed.swing.SwingNode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import java.awt.geom.Point2D
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.properties.Delegates

object Pen : PenListener {
    /**
     * To add a global watcher to listen for properties' state change
     */
    private val penWatcher = Watcher(this)
    private val changes = penWatcher.changes()

    // PROPERTIES
    val paper = Paper.getPaper()

    const val PEN_STYLUS: Int = 1
    const val PEN_ERASER: Int = 2
    const val PEN_CURSOR: Int = 3

    var penType = PEN_CURSOR

    var penLocation = Point2D.Float()

    fun setPenType(pm: PenManager) {
        // penKind init
        var penKind = pm.pen.kind.type

        penType = when (penKind) {
            PKind.Type.CUSTOM -> 0
            PKind.Type.STYLUS -> PEN_STYLUS // #1
            PKind.Type.ERASER -> PEN_ERASER // #2
            PKind.Type.CURSOR -> PEN_CURSOR // #3
            else -> PEN_CURSOR   // #3
        }
    }

    fun setPenLocation(event: MouseEvent) {
        // TODO: Add all the info on left side box
        //println("Pen::MouseEvent: ${event}");

        if (penType == PEN_CURSOR) {
            penLocation.x = event.x.toFloat()
            penLocation.y = event.y.toFloat()
        }
        penWatcher.emit(changes)
    }

    fun getMouseEvents(paper: StackPane) {
        paper.addEventFilter(MouseEvent.ANY) {event ->
            setPenLocation(event)
        }
    }

    private fun createSwingContent(swingNode: SwingNode) {
        SwingUtilities.invokeLater {
            val penCanvas = JPanel()
            val pm = PenManager(penCanvas)

            pm.pen.addListener(this)

//            println("devices: " + pm.devices)
//            println("-----------------------------------------------")
//            println("status report: " + StatusReport(pm))

            pm.pen.setFrequencyLater(200)

            setPenType(pm)

            //setPenLocation(event)

            //AwtPenToolkit.addPenListener(penCanvas, this)
            //println(StatusReport(AwtPenToolkit.getPenManager()))
        }
    }

    fun getPen() {
        val swingNode: SwingNode = SwingNode()

        createSwingContent(swingNode)
        paper.children.add(swingNode)

        getMouseEvents(paper)
    }

    override fun penKindEvent(event: PKindEvent?) {
        //println(event)
    }

    override fun penButtonEvent(event: PButtonEvent?) {
        //println(event)
    }

    override fun penTock(event: Long) {
        //println(event)
    }

    override fun penLevelEvent(event: PLevelEvent?) {
        //println(event)
    }

    override fun penScrollEvent(event: PScrollEvent?) {
        //println(event)
    }

    fun getChanges(): Observable<Pen> {
        return changes
    }
}
