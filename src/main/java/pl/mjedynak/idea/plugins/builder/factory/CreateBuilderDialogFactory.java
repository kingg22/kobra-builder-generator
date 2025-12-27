package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.gui.CreateBuilderDialog;

public class CreateBuilderDialogFactory {

    private static final String BUILDER_SUFFIX = "Builder";
    private static final String DIALOG_NAME = "CreateBuilder";

    @Contract("_, _, _, _ -> new")
    public static @NotNull CreateBuilderDialog createBuilderDialog(
            @NotNull PsiClass sourceClass,
            @NotNull Project project,
            @Nullable PsiPackage srcPackage,
            @Nullable PsiClass existingBuilder) {
        return new CreateBuilderDialog(
                project, DIALOG_NAME, sourceClass, sourceClass.getName() + BUILDER_SUFFIX, srcPackage, existingBuilder);
    }
}
