package io.github.kingg22.kobra.builder.factory

import io.github.kingg22.kobra.builder.renderer.ActionCellRenderer
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.VisibleForTesting
import javax.swing.JList

public abstract class AbstractPopupListFactory {
    @get:VisibleForTesting
    public var actionCellRenderer: ActionCellRenderer? = null
        private set

    public val popupList: JList<*> get() {
        val list = createList()
        list.setCellRenderer(cellRenderer())
        return list
    }

    @Contract(value = "->new")
    protected abstract fun createList(): JList<*>

    private fun cellRenderer(): ActionCellRenderer {
        if (actionCellRenderer == null) {
            actionCellRenderer = ActionCellRenderer()
        }
        return actionCellRenderer!!
    }
}
