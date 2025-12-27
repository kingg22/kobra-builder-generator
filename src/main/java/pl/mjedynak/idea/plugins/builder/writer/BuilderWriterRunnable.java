package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class BuilderWriterRunnable implements Runnable {

    private final BuilderContext context;
    private final @Nullable PsiClass existingBuilder;

    public BuilderWriterRunnable(BuilderContext context, @Nullable PsiClass existingBuilder) {
        this.context = context;
        this.existingBuilder = existingBuilder;
    }

    @Override
    public void run() {
        Application application = PsiHelper.getApplication();
        application.runWriteAction(new BuilderWriterComputable(context, existingBuilder));
    }

    @VisibleForTesting
    BuilderContext getContext() {
        return context;
    }

    @VisibleForTesting
    @Nullable
    PsiClass getExistingBuilder() {
        return existingBuilder;
    }
}
