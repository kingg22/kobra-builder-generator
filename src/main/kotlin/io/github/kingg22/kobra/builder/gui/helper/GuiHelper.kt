package io.github.kingg22.kobra.builder.gui.helper

import com.intellij.codeInsight.CodeInsightUtil
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

public object GuiHelper {
    @JvmStatic
    public fun showMessageDialog(project: Project?, message: String?, title: String, icon: Icon?) {
        Messages.showMessageDialog(project, message, title, icon)
    }

    @JvmStatic
    public fun includeCurrentPlaceAsChangePlace(project: Project) {
        IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace()
    }

    @JvmStatic
    public fun positionCursor(project: Project, psiFile: PsiFile, psiElement: PsiElement) {
        CodeInsightUtil.positionCursor(project, psiFile, psiElement)
    }
}
