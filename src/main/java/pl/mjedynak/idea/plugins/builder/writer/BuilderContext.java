package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import java.util.Arrays;
import java.util.Objects;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForBuilder;

public record BuilderContext(
        Project project,
        PsiFieldsForBuilder psiFieldsForBuilder,
        PsiDirectory targetDirectory,
        String className,
        PsiClass psiClassFromEditor,
        String methodPrefix,
        boolean isInner,
        boolean hasButMethod,
        boolean useSingleField,
        boolean hasAddCopyConstructor) {
    @Override
    public int hashCode() {
        Object[] objects = {project, psiFieldsForBuilder, targetDirectory, className, psiClassFromEditor, methodPrefix};
        return Arrays.hashCode(objects);
    }

    @SuppressWarnings("EqualsIncompatibleType")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderContext other)) {
            return false;
        }
        return Objects.equals(this.project, other.project)
                && Objects.equals(this.psiFieldsForBuilder, other.psiFieldsForBuilder)
                && Objects.equals(this.targetDirectory, other.targetDirectory)
                && Objects.equals(this.className, other.className)
                && Objects.equals(this.psiClassFromEditor, other.psiClassFromEditor)
                && Objects.equals(this.methodPrefix, other.methodPrefix);
    }
}
