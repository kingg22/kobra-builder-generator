package io.github.kingg22.kobra.builder.action

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

public class GenerateBuilderAdditionalAction : AbstractBuilderAdditionalAction() {
    override fun getText(): String = TEXT

    override fun getIcon(): Icon = ICON

    override fun execute() {}

    public companion object {
        private const val TEXT = "Create New Builder..."
        private val ICON = getIcon("/actions/intentionBulb.png", GenerateBuilderAdditionalAction::class.java)
    }
}
