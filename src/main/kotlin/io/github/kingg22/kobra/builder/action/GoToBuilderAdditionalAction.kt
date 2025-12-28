package io.github.kingg22.kobra.builder.action

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

public class GoToBuilderAdditionalAction : AbstractBuilderAdditionalAction() {
    override fun getText(): String = TEXT

    override fun getIcon(): Icon = ICON

    override fun execute() {}

    public companion object {
        private const val TEXT = "Go to builder..."
        private val ICON = getIcon("/actions/intentionBulb.png", GoToBuilderAdditionalAction::class.java)
    }
}
