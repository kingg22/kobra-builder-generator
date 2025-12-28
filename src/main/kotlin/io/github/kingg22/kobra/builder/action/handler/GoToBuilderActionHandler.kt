package io.github.kingg22.kobra.builder.action.handler

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.factory.GoToBuilderPopupListFactory
import io.github.kingg22.kobra.builder.gui.displayer.GoToBuilderPopupDisplayer
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.jetbrains.annotations.VisibleForTesting

public class GoToBuilderActionHandler : AbstractBuilderActionHandler {
    public constructor() : super(GoToBuilderPopupDisplayer(), GoToBuilderPopupListFactory())

    @VisibleForTesting
    internal constructor(
        popupDisplayer: GoToBuilderPopupDisplayer,
        popupListFactory: GoToBuilderPopupListFactory,
        displayChoosers: DisplayChoosers,
    ) : super(popupDisplayer, popupListFactory, displayChoosers)

    override fun doActionWhenClassToGoIsFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
        classToGo: PsiClass,
    ) {
        PsiHelper.navigateToClass(classToGo)
    }

    override fun doActionWhenClassToGoIsNotFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
    ) {
        if (!isBuilder) {
            displayPopup(editor)
        }
    }

    private fun displayPopup(editor: Editor) {
        val popupList = popupListFactory.popupList
        popupDisplayer.displayPopupChooser(editor, popupList) { displayChoosers.run(null) }
    }
}
