package io.github.kingg22.kobra.builder.writer

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

public class BuilderWriterErrorRunnable(
    private val project: Project?,
    private val className: String,
    message: String?,
) : Runnable {
    private val message: String = message ?: INTENTION_ERROR_CANNOT_CREATE_CLASS_MESSAGE

    override fun run() {
        Messages.showErrorDialog(
            project,
            CodeInsightBundle.message(message, className),
            CodeInsightBundle.message(INTENTION_ERROR_CANNOT_CREATE_CLASS_TITLE),
        )
    }

    public companion object {
        private const val INTENTION_ERROR_CANNOT_CREATE_CLASS_MESSAGE = "intention.error.cannot.create.class.message"
        private const val INTENTION_ERROR_CANNOT_CREATE_CLASS_TITLE = "intention.error.cannot.create.class.title"
    }
}
