package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReferenceEditorComboWithBrowseButtonFactory {

    private ReferenceEditorComboWithBrowseButtonFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ReferenceEditorComboWithBrowseButton getReferenceEditorComboWithBrowseButton(
            @NotNull Project project, @Nullable String packageName, @NotNull String recentsKey) {
        return new ReferenceEditorComboWithBrowseButton(null, packageName, project, true, recentsKey);
    }
}
