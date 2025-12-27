package pl.mjedynak.idea.plugins.builder.writer;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class BuilderWriter {

    @VisibleForTesting
    static final String CREATE_BUILDER_STRING = "Create Builder";

    private BuilderWriter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void writeBuilder(@NotNull BuilderContext context, @Nullable PsiClass existingBuilder) {
        CommandProcessor commandProcessor = PsiHelper.getCommandProcessor();
        commandProcessor.executeCommand(
                context.project(), new BuilderWriterRunnable(context, existingBuilder), CREATE_BUILDER_STRING, null);
    }
}
