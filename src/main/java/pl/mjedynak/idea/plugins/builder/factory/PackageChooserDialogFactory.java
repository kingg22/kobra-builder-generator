package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PackageChooserDialogFactory {

    private PackageChooserDialogFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Contract("_, _ -> new")
    public static @NotNull PackageChooserDialog getPackageChooserDialog(
            @NotNull String message, @NotNull Project project) {
        return new PackageChooserDialog(message, project);
    }
}
