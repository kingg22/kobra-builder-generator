package io.github.kingg22.kobra.builder.action.handler

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.action.GoToBuilderAdditionalAction
import io.github.kingg22.kobra.builder.action.RegenerateBuilderAdditionalAction
import io.github.kingg22.kobra.builder.factory.GenerateBuilderPopupListFactory
import io.github.kingg22.kobra.builder.gui.displayer.GenerateBuilderPopupDisplayer
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.jetbrains.annotations.VisibleForTesting

public class GenerateBuilderActionHandler : AbstractBuilderActionHandler {
    public constructor() : super(GenerateBuilderPopupDisplayer(), GenerateBuilderPopupListFactory())

    @VisibleForTesting
    internal constructor(
        popupDisplayer: GenerateBuilderPopupDisplayer,
        popupListFactory: GenerateBuilderPopupListFactory,
        displayChoosers: DisplayChoosers,
    ) : super(popupDisplayer, popupListFactory, displayChoosers)

    override fun doActionWhenClassToGoIsFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
        classToGo: PsiClass,
    ) {
        if (!isBuilder) {
            displayPopup(editor, classToGo)
        }
    }

    override fun doActionWhenClassToGoIsNotFound(
        editor: Editor,
        psiClassFromEditor: PsiClass,
        dataContext: DataContext?,
        isBuilder: Boolean,
    ) {
        if (!isBuilder) {
            displayChoosers.run(null)
        }
    }

    private fun displayPopup(editor: Editor, classToGo: PsiClass?) {
        val popupList = popupListFactory.popupList
        popupDisplayer.displayPopupChooser(editor, popupList) {
            if (popupList.getSelectedValue() is GoToBuilderAdditionalAction) {
                PsiHelper.navigateToClass(classToGo)
            } else if (popupList.getSelectedValue() is RegenerateBuilderAdditionalAction) {
                displayChoosers.run(classToGo)
            }
        }
    }
}
