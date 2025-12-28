package io.github.kingg22.kobra.builder.factory

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiPackage
import io.github.kingg22.kobra.builder.gui.CreateBuilderDialog
import org.jetbrains.annotations.Contract

public object CreateBuilderDialogFactory {
    private const val BUILDER_SUFFIX = "Builder"
    private const val DIALOG_NAME = "CreateBuilder"

    @Contract("_, _, _, _ -> new")
    @JvmStatic
    public fun createBuilderDialog(
        sourceClass: PsiClass,
        project: Project,
        srcPackage: PsiPackage?,
        existingBuilder: PsiClass?,
    ): CreateBuilderDialog = CreateBuilderDialog(
        project,
        DIALOG_NAME,
        sourceClass,
        sourceClass.name + BUILDER_SUFFIX,
        srcPackage,
        existingBuilder,
    )
}
