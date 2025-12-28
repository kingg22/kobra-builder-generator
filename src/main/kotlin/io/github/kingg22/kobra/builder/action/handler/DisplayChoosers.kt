package io.github.kingg22.kobra.builder.action.handler

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.ide.util.MemberChooser
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import io.github.kingg22.kobra.builder.factory.CreateBuilderDialogFactory
import io.github.kingg22.kobra.builder.factory.MemberChooserDialogFactory
import io.github.kingg22.kobra.builder.factory.PsiFieldsForBuilderFactory
import io.github.kingg22.kobra.builder.gui.CreateBuilderDialog
import io.github.kingg22.kobra.builder.psi.PsiFieldSelector
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.writer.BuilderContext
import io.github.kingg22.kobra.builder.writer.BuilderWriter

public class DisplayChoosers(
    private val psiClassFromEditor: PsiClass,
    private val project: Project,
    private val editor: Editor,
) {
    public fun run(existingBuilder: PsiClass?) {
        val createBuilderDialog = showDialog(existingBuilder)
        if (createBuilderDialog != null && createBuilderDialog.isOK) {
            val targetDirectory = createBuilderDialog.targetDirectory
            val className = createBuilderDialog.className.orEmpty()
            val methodPrefix = createBuilderDialog.methodPrefix
            val innerBuilder = createBuilderDialog.isInnerBuilder
            val useSingleField = createBuilderDialog.isSingleField
            val hasButMethod = createBuilderDialog.hasButMethod
            val fieldsToDisplay =
                getFieldsToIncludeInBuilder(psiClassFromEditor, innerBuilder, useSingleField, hasButMethod)
            val memberChooserDialog = MemberChooserDialogFactory.getMemberChooserDialog(fieldsToDisplay, project)
            memberChooserDialog.show()
            writeBuilderIfNecessary(
                targetDirectory,
                className,
                methodPrefix,
                memberChooserDialog,
                createBuilderDialog,
                existingBuilder,
            )
        }
    }

    private fun writeBuilderIfNecessary(
        targetDirectory: PsiDirectory?,
        className: String,
        methodPrefix: String?,
        memberChooserDialog: MemberChooser<PsiElementClassMember<*>>,
        createBuilderDialog: CreateBuilderDialog,
        existingBuilder: PsiClass?,
    ) {
        if (memberChooserDialog.isOK) {
            val selectedElements = memberChooserDialog.getSelectedElements() ?: return
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                selectedElements,
                psiClassFromEditor,
            )
            val context = BuilderContext(
                project,
                psiFieldsForBuilder,
                targetDirectory,
                className,
                psiClassFromEditor,
                methodPrefix,
                createBuilderDialog.isInnerBuilder,
                createBuilderDialog.hasButMethod,
                createBuilderDialog.isSingleField,
                createBuilderDialog.hasAddCopyConstructor,
            )
            BuilderWriter.writeBuilder(context, existingBuilder)
        }
    }

    private fun showDialog(existingBuilder: PsiClass?): CreateBuilderDialog? {
        val file: PsiFile = PsiHelper.getPsiFile(editor, project) ?: return null
        val dialog = CreateBuilderDialogFactory.createBuilderDialog(
            psiClassFromEditor,
            project,
            PsiHelper.getPackage(file.containingDirectory),
            existingBuilder,
        )
        dialog.show()
        return dialog
    }

    public companion object {
        private fun getFieldsToIncludeInBuilder(
            clazz: PsiClass,
            innerBuilder: Boolean,
            useSingleField: Boolean,
            hasButMethod: Boolean,
        ): List<PsiElementClassMember<*>> = PsiFieldSelector.selectFieldsToIncludeInBuilder(
            clazz,
            innerBuilder,
            useSingleField,
            hasButMethod,
        )
    }
}
