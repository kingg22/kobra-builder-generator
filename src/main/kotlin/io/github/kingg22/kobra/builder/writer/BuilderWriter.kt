package io.github.kingg22.kobra.builder.writer

import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.jetbrains.annotations.VisibleForTesting

public object BuilderWriter {
    @VisibleForTesting
    public const val CREATE_BUILDER_STRING: String = "Create Builder"

    @JvmStatic
    public fun writeBuilder(context: BuilderContext, existingBuilder: PsiClass?) {
        PsiHelper.commandProcessor.executeCommand(
            context.project,
            BuilderWriterRunnable(context, existingBuilder),
            CREATE_BUILDER_STRING,
            null,
        )
    }
}
