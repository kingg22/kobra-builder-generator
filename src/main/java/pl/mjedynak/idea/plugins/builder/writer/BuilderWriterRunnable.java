package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.psi.BuilderPsiClassBuilder;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class BuilderWriterRunnable implements Runnable {

    private final BuilderPsiClassBuilder builderPsiClassBuilder;
    private final BuilderContext context;
    private final PsiClass existingBuilder;

    public BuilderWriterRunnable(
            BuilderPsiClassBuilder builderPsiClassBuilder, BuilderContext context, PsiClass existingBuilder) {
        this.builderPsiClassBuilder = builderPsiClassBuilder;
        this.context = context;
        this.existingBuilder = existingBuilder;
    }

    @Override
    public void run() {
        Application application = PsiHelper.getApplication();
        application.runWriteAction(new BuilderWriterComputable(builderPsiClassBuilder, context, existingBuilder));
    }

    @VisibleForTesting
    BuilderPsiClassBuilder getBuilderPsiClassBuilder() {
        return builderPsiClassBuilder;
    }

    @VisibleForTesting
    BuilderContext getContext() {
        return context;
    }

    @VisibleForTesting
    PsiClass getExistingBuilder() {
        return existingBuilder;
    }
}
