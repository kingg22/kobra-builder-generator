package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.BuilderPsiClassBuilder;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

record BuilderWriterComputable(
        @NotNull BuilderContext context, @Nullable PsiClass existingBuilder) implements Computable<PsiElement> {

    @Override
    public @Nullable PsiElement compute() {
        return createBuilder();
    }

    private @Nullable PsiElement createBuilder() {
        try {
            GuiHelper.includeCurrentPlaceAsChangePlace(context.project());
            PsiClass targetClass;
            if (existingBuilder != null) {
                existingBuilder.delete();
            }
            if (context.isInner()) {
                targetClass = getInnerBuilderPsiClass();
                context.psiClassFromEditor().add(targetClass);
            } else {
                targetClass = getBuilderPsiClass();
                navigateToClassAndPositionCursor(context.project(), targetClass);
            }
            return targetClass;
        } catch (IncorrectOperationException e) {
            showErrorMessage(context.project(), context.className(), e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private PsiClass getInnerBuilderPsiClass() {
        BuilderPsiClassBuilder builder = BuilderPsiClassBuilder.anInnerBuilder(context)
                .withFields()
                .withConstructor()
                .withInitializingMethod()
                .withSetMethods(context.methodPrefix());
        addButMethodIfNecessary(builder);
        addCopyConstructorIfNecessary(builder);
        return builder.build();
    }

    private PsiClass getBuilderPsiClass() {
        BuilderPsiClassBuilder builder = BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withInitializingMethod()
                .withSetMethods(context.methodPrefix());
        addButMethodIfNecessary(builder);
        addCopyConstructorIfNecessary(builder);
        return builder.build();
    }

    private void addButMethodIfNecessary(BuilderPsiClassBuilder builder) {
        if (context.hasButMethod()) {
            builder.withButMethod();
        }
    }

    private void addCopyConstructorIfNecessary(BuilderPsiClassBuilder builder) {
        if (context.hasAddCopyConstructor()) {
            builder.withCopyConstructor();
        }
    }

    private void navigateToClassAndPositionCursor(@NotNull Project project, @NotNull PsiClass targetClass) {
        GuiHelper.positionCursor(project, targetClass.getContainingFile(), targetClass.getLBrace());
    }

    private void showErrorMessage(Project project, String className, @Nullable String message) {
        BuilderWriterErrorRunnable builderWriterErrorRunnable =
                new BuilderWriterErrorRunnable(project, className, message);

        Application application = PsiHelper.getApplication();
        application.invokeLater(builderWriterErrorRunnable);
    }
}
