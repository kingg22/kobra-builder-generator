package pl.mjedynak.idea.plugins.builder.action.handler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.action.GoToBuilderAdditionalAction;
import pl.mjedynak.idea.plugins.builder.action.RegenerateBuilderAdditionalAction;
import pl.mjedynak.idea.plugins.builder.factory.GenerateBuilderPopupListFactory;
import pl.mjedynak.idea.plugins.builder.gui.displayer.GenerateBuilderPopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class GenerateBuilderActionHandler extends AbstractBuilderActionHandler {

    public GenerateBuilderActionHandler() {
        super(new GenerateBuilderPopupDisplayer(), new GenerateBuilderPopupListFactory());
    }

    @VisibleForTesting
    GenerateBuilderActionHandler(
            GenerateBuilderPopupDisplayer popupDisplayer,
            GenerateBuilderPopupListFactory popupListFactory,
            DisplayChoosers displayChoosers) {
        super(popupDisplayer, popupListFactory, displayChoosers);
    }

    @Override
    protected void doActionWhenClassToGoIsFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder,
            @NotNull PsiClass classToGo) {
        if (!isBuilder) {
            displayPopup(editor, classToGo);
        }
    }

    @Override
    protected void doActionWhenClassToGoIsNotFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder) {
        if (!isBuilder) {
            displayChoosers.run(null);
        }
    }

    private void displayPopup(@NotNull Editor editor, @Nullable PsiClass classToGo) {
        JList<?> popupList = popupListFactory.getPopupList();
        popupDisplayer.displayPopupChooser(editor, popupList, () -> {
            if (popupList.getSelectedValue() instanceof GoToBuilderAdditionalAction) {
                PsiHelper.navigateToClass(classToGo);
            } else if (popupList.getSelectedValue() instanceof RegenerateBuilderAdditionalAction) {
                displayChoosers.run(classToGo);
            }
        });
    }
}
