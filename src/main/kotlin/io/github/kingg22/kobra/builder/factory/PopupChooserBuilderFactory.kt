package io.github.kingg22.kobra.builder.factory

import com.intellij.openapi.ui.popup.PopupChooserBuilder
import org.jetbrains.annotations.Contract
import javax.swing.JList

public object PopupChooserBuilderFactory {
    @Contract("_ -> new")
    @JvmStatic
    public fun getPopupChooserBuilder(list: JList<*>): PopupChooserBuilder<*> = PopupChooserBuilder(list)
}
