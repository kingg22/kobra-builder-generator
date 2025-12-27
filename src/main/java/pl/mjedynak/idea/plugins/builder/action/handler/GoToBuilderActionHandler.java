package pl.mjedynak.idea.plugins.builder.action.handler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.factory.GoToBuilderPopupListFactory;
import pl.mjedynak.idea.plugins.builder.gui.displayer.GoToBuilderPopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class GoToBuilderActionHandler extends AbstractBuilderActionHandler {

    public GoToBuilderActionHandler(
            GoToBuilderPopupDisplayer popupDisplayer,
            GoToBuilderPopupListFactory popupListFactory,
            DisplayChoosers displayChoosersRunnable) {
        super(popupDisplayer, popupListFactory, displayChoosersRunnable);
    }

    @Override
    protected void doActionWhenClassToGoIsFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder,
            @NotNull PsiClass classToGo) {
        PsiHelper.navigateToClass(classToGo);
    }

    @Override
    protected void doActionWhenClassToGoIsNotFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder) {
        if (!isBuilder) {
            displayPopup(editor);
        }
    }

    private void displayPopup(@NotNull Editor editor) {
        JList<?> popupList = popupListFactory.getPopupList();
        popupDisplayer.displayPopupChooser(editor, popupList, () -> displayChoosers.run(null));
    }
}
