package pl.mjedynak.idea.plugins.builder.gui.displayer;

import com.intellij.openapi.editor.Editor;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.factory.PopupChooserBuilderFactory;

public abstract class AbstractPopupDisplayer {

    public void displayPopupChooser(@NotNull Editor editor, @NotNull JList<?> list, @NotNull Runnable runnable) {
        PopupChooserBuilderFactory.getPopupChooserBuilder(list)
                .setTitle(getTitle())
                .setItemChosenCallback(runnable)
                .setMovable(true)
                .createPopup()
                .showInBestPositionFor(editor);
    }

    @NotNull
    protected abstract String getTitle();
}
