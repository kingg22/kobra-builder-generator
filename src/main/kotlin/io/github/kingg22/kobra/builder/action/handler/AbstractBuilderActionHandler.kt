package io.github.kingg22.kobra.builder.action.handler

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.factory.AbstractPopupListFactory
import io.github.kingg22.kobra.builder.finder.BuilderFinder
import io.github.kingg22.kobra.builder.gui.displayer.AbstractPopupDisplayer
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.verifier.BuilderVerifier
import org.jetbrains.annotations.VisibleForTesting

public abstract class AbstractBuilderActionHandler : EditorActionHandler {
    protected val popupDisplayer: AbstractPopupDisplayer
    protected val popupListFactory: AbstractPopupListFactory
    protected lateinit var displayChoosers: DisplayChoosers
    private val reInitDisplayChoosers: Boolean

    public constructor(popupDisplayer: AbstractPopupDisplayer, popupListFactory: AbstractPopupListFactory) {
        this.popupDisplayer = popupDisplayer
        this.popupListFactory = popupListFactory
        this.reInitDisplayChoosers = true
    }

    @VisibleForTesting
    internal constructor(
        popupDisplayer: AbstractPopupDisplayer,
        popupListFactory: AbstractPopupListFactory,
        displayChoosers: DisplayChoosers,
    ) {
        this.popupDisplayer = popupDisplayer
        this.popupListFactory = popupListFactory
        this.displayChoosers = displayChoosers
        this.reInitDisplayChoosers = false
    }

    public override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        val project = if (dataContext == null) editor.project else dataContext.getData(CommonDataKeys.PROJECT)
        if (project == null) return
        val psiClassFromEditor = PsiHelper.getPsiClassFromEditor(editor, project)
        if (psiClassFromEditor != null) {
            if (reInitDisplayChoosers) displayChoosers = DisplayChoosers(psiClassFromEditor, project, editor)
            forwardToSpecificAction(editor, psiClassFromEditor, dataContext)
        }
    }

    private fun forwardToSpecificAction(editor: Editor, psiClassFromEditor: PsiClass, dataContext: DataContext?) {
        val isBuilder = BuilderVerifier.isBuilder(psiClassFromEditor)
        val classToGo = findClassToGo(psiClassFromEditor, isBuilder)
        if (classToGo != null) {
            doActionWhenClassToGoIsFound(editor, psiClassFromEditor, dataContext, isBuilder, classToGo)
        } else {
            doActionWhenClassToGoIsNotFound(editor, psiClassFromEditor, dataContext, isBuilder)
        }
    }

    private fun findClassToGo(psiClassFromEditor: PsiClass, isBuilder: Boolean): PsiClass? {
        if (isBuilder) {
            return BuilderFinder.findClassForBuilder(psiClassFromEditor)
        }
        return BuilderFinder.findBuilderForClass(psiClassFromEditor)
    }

    protected abstract fun doActionWhenClassToGoIsFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
        classToGo: PsiClass,
    )

    protected abstract fun doActionWhenClassToGoIsNotFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
    )
}
