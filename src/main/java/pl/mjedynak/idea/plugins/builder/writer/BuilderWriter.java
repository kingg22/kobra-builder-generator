package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.psi.BuilderPsiClassBuilder;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class BuilderWriter {

    @VisibleForTesting
    static final String CREATE_BUILDER_STRING = "Create Builder";

    private final BuilderPsiClassBuilder builderPsiClassBuilder;

    public BuilderWriter(BuilderPsiClassBuilder builderPsiClassBuilder) {
        this.builderPsiClassBuilder = builderPsiClassBuilder;
    }

    public void writeBuilder(@NotNull BuilderContext context, PsiClass existingBuilder) {
        CommandProcessor commandProcessor = PsiHelper.getCommandProcessor();
        commandProcessor.executeCommand(
                context.project(),
                new BuilderWriterRunnable(builderPsiClassBuilder, context, existingBuilder),
                CREATE_BUILDER_STRING,
                this);
    }
}
