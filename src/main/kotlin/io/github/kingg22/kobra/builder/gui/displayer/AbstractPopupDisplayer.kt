package io.github.kingg22.kobra.builder.gui.displayer

import com.intellij.openapi.editor.Editor
import io.github.kingg22.kobra.builder.factory.PopupChooserBuilderFactory
import javax.swing.JList

public abstract class AbstractPopupDisplayer {
    public fun displayPopupChooser(editor: Editor, list: JList<*>, runnable: Runnable) {
        PopupChooserBuilderFactory.getPopupChooserBuilder(list)
            .setTitle(this.title)
            .setItemChosenCallback(runnable)
            .setMovable(true)
            .createPopup()
            .showInBestPositionFor(editor)
    }

    protected abstract val title: String
}
