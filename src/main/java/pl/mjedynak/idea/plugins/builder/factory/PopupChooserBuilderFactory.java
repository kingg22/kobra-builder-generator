package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import javax.swing.JList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PopupChooserBuilderFactory {

    private PopupChooserBuilderFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Contract("_ -> new")
    public static @NotNull PopupChooserBuilder<?> getPopupChooserBuilder(@NotNull JList<?> list) {
        return new PopupChooserBuilder<>(list);
    }
}
