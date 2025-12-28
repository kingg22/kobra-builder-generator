package io.github.kingg22.kobra.builder.action

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

public class RegenerateBuilderAdditionalAction : AbstractBuilderAdditionalAction() {
    override fun getText(): String = TEXT

    override fun getIcon(): Icon = ICON

    override fun execute() {}

    public companion object {
        private const val TEXT = "Regenerate builder..."
        private val ICON = getIcon("/actions/intentionBulb.png", RegenerateBuilderAdditionalAction::class.java)
    }
}
