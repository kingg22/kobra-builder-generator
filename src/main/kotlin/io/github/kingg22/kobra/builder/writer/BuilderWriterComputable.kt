package io.github.kingg22.kobra.builder.writer

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import io.github.kingg22.kobra.builder.gui.helper.GuiHelper
import io.github.kingg22.kobra.builder.psi.BuilderPsiClassBuilder
import io.github.kingg22.kobra.builder.psi.BuilderPsiClassBuilderFactory
import io.github.kingg22.kobra.builder.psi.PsiHelper

@JvmRecord
internal data class BuilderWriterComputable(
    @JvmField val context: BuilderContext,
    @JvmField val existingBuilder: PsiClass?,
    private val builderPsiClassBuilderFactory: BuilderPsiClassBuilderFactory = BuilderPsiClassBuilder,
) : Computable<PsiElement> {
    override fun compute(): PsiElement? {
        try {
            GuiHelper.includeCurrentPlaceAsChangePlace(context.project)
            existingBuilder?.delete()
            val targetClass: PsiClass
            if (context.isInner) {
                targetClass = this.innerBuilderPsiClass
                context.psiClassFromEditor.add(targetClass)
            } else {
                targetClass = this.builderPsiClass
                navigateToClassAndPositionCursor(context.project, targetClass)
            }
            return targetClass
        } catch (e: IncorrectOperationException) {
            showErrorMessage(context.project, context.className, e.message)
            e.printStackTrace()
            return null
        }
    }

    private val innerBuilderPsiClass: PsiClass
        get() = builderPsiClassBuilderFactory
            .anInnerBuilder(context)
            .withFields()
            .withConstructor()
            .withInitializingMethod()
            .withSetMethods(context.methodPrefix)
            .addButMethodIfNecessary()
            .addCopyConstructorIfNecessary()
            .build()

    private val builderPsiClass: PsiClass
        get() = builderPsiClassBuilderFactory
            .aBuilder(context)
            .withFields()
            .withConstructor()
            .withInitializingMethod()
            .withSetMethods(context.methodPrefix)
            .addButMethodIfNecessary()
            .addCopyConstructorIfNecessary()
            .build()

    private fun BuilderPsiClassBuilder.addButMethodIfNecessary() = apply {
        if (context.hasButMethod) withButMethod()
    }

    private fun BuilderPsiClassBuilder.addCopyConstructorIfNecessary() = apply {
        if (context.hasAddCopyConstructor) withCopyConstructor()
    }

    private fun navigateToClassAndPositionCursor(project: Project, targetClass: PsiClass) {
        GuiHelper.positionCursor(project, targetClass.containingFile, targetClass)
    }

    private fun showErrorMessage(project: Project?, className: String, message: String?) {
        val builderWriterErrorRunnable = BuilderWriterErrorRunnable(project, className, message)

        PsiHelper.application.invokeLater(builderWriterErrorRunnable)
    }
}
