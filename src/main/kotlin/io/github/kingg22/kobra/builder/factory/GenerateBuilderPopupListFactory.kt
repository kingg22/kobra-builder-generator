package io.github.kingg22.kobra.builder.factory

import com.intellij.ui.components.JBList
import io.github.kingg22.kobra.builder.action.AbstractBuilderAdditionalAction
import io.github.kingg22.kobra.builder.action.GoToBuilderAdditionalAction
import io.github.kingg22.kobra.builder.action.RegenerateBuilderAdditionalAction

public class GenerateBuilderPopupListFactory : AbstractPopupListFactory() {
    override fun createList(): JBList<AbstractBuilderAdditionalAction> =
        JBList(GoToBuilderAdditionalAction(), RegenerateBuilderAdditionalAction())
}
