package io.github.kingg22.kobra.builder.renderer

import com.intellij.codeInsight.navigation.GotoTargetHandler.AdditionalAction
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

public class ActionCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component? {
        val result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        if (value != null && value is AdditionalAction) {
            setText(value.text)
            setIcon(value.icon)
        }
        return result
    }
}
