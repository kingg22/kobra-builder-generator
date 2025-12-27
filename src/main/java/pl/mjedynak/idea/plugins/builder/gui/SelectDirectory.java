package pl.mjedynak.idea.plugins.builder.gui;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class SelectDirectory implements Runnable {

    private final @NotNull CreateBuilderDialog createBuilderDialog;
    private @NotNull final Module module;
    private @NotNull final String packageName;
    private final @NotNull String className;
    private final @Nullable PsiClass existingBuilder;

    public SelectDirectory(
            @NotNull CreateBuilderDialog createBuilderDialog,
            @NotNull Module module,
            @NotNull String packageName,
            @NotNull String className,
            @Nullable PsiClass existingBuilder) {
        this.createBuilderDialog = createBuilderDialog;
        this.module = module;
        this.packageName = packageName;
        this.className = className;
        this.existingBuilder = existingBuilder;
    }

    @Override
    public void run() {
        PsiDirectory targetDirectory = PsiHelper.getDirectoryFromModuleAndPackageName(module, packageName);
        if (targetDirectory != null) {
            throwExceptionIfClassCannotBeCreated(targetDirectory);
            createBuilderDialog.setTargetDirectory(targetDirectory);
        }
    }

    private void throwExceptionIfClassCannotBeCreated(PsiDirectory targetDirectory) {
        if (!isClassToCreateSameAsBuilderToDelete(targetDirectory)) {
            String errorString = PsiHelper.checkIfClassCanBeCreated(targetDirectory, className);
            if (errorString != null) {
                throw new IncorrectOperationException(errorString);
            }
        }
    }

    private boolean isClassToCreateSameAsBuilderToDelete(@NotNull PsiDirectory targetDirectory) {
        return existingBuilder != null
                && existingBuilder.getContainingFile() != null
                && existingBuilder.getContainingFile().getContainingDirectory() != null
                && existingBuilder
                        .getContainingFile()
                        .getContainingDirectory()
                        .getName()
                        .equals(targetDirectory.getName())
                && existingBuilder.getName() != null
                && existingBuilder.getName().equals(className);
    }
}
