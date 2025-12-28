package io.github.kingg22.kobra.builder.writer

import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.jetbrains.annotations.VisibleForTesting

@JvmRecord
public data class BuilderWriterRunnable(
    @JvmField @get:VisibleForTesting val context: BuilderContext,
    @JvmField @get:VisibleForTesting val existingBuilder: PsiClass?,
) : Runnable {
    override fun run() {
        PsiHelper.application.runWriteAction(BuilderWriterComputable(context, existingBuilder))
    }
}
