package io.github.kingg22.kobra.builder.gui

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.util.IncorrectOperationException
import io.github.kingg22.kobra.builder.psi.PsiHelper

public class SelectDirectory(
    private val createBuilderDialog: CreateBuilderDialog,
    private val module: Module,
    private val packageName: String,
    private val className: String,
    private val existingBuilder: PsiClass?,
) : Runnable {
    override fun run() {
        val targetDirectory = PsiHelper.getDirectoryFromModuleAndPackageName(module, packageName)
        if (targetDirectory != null) {
            throwExceptionIfClassCannotBeCreated(targetDirectory)
            createBuilderDialog.targetDirectory = targetDirectory
        }
    }

    private fun throwExceptionIfClassCannotBeCreated(targetDirectory: PsiDirectory) {
        if (!isClassToCreateSameAsBuilderToDelete(targetDirectory)) {
            val errorString = PsiHelper.checkIfClassCanBeCreated(targetDirectory, className)
            if (errorString != null) {
                throw IncorrectOperationException(errorString)
            }
        }
    }

    private fun isClassToCreateSameAsBuilderToDelete(targetDirectory: PsiDirectory): Boolean =
        existingBuilder != null &&
            existingBuilder.containingFile != null &&
            existingBuilder.containingFile.containingDirectory != null &&
            existingBuilder.containingFile.containingDirectory.name == targetDirectory.name &&
            existingBuilder.name != null &&
            existingBuilder.name == className
}
