package io.github.kingg22.kobra.builder.factory

import com.intellij.ui.components.JBList
import io.github.kingg22.kobra.builder.action.GenerateBuilderAdditionalAction

public class GoToBuilderPopupListFactory : AbstractPopupListFactory() {
    override fun createList(): JBList<GenerateBuilderAdditionalAction> = JBList(GenerateBuilderAdditionalAction())
}
