package pl.mjedynak.idea.plugins.builder.action.handler;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.factory.AbstractPopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.AbstractPopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

public abstract class AbstractBuilderActionHandler extends EditorActionHandler {

    protected @NotNull AbstractPopupDisplayer popupDisplayer;
    protected @NotNull AbstractPopupListFactory popupListFactory;
    protected @NotNull DisplayChoosers displayChoosers;

    public AbstractBuilderActionHandler(
            @NotNull AbstractPopupDisplayer popupDisplayer,
            @NotNull AbstractPopupListFactory popupListFactory,
            @NotNull DisplayChoosers displayChoosers) {
        this.popupDisplayer = popupDisplayer;
        this.popupListFactory = popupListFactory;
        this.displayChoosers = displayChoosers;
    }

    @Override
    public final void doExecute(@NotNull Editor editor, @Nullable Caret caret, @Nullable DataContext dataContext) {
        if (dataContext == null) return;
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        if (project == null) return;
        PsiClass psiClassFromEditor = PsiHelper.getPsiClassFromEditor(editor, project);
        if (psiClassFromEditor != null) {
            prepareDisplayChoosers(editor, psiClassFromEditor, project);
            forwardToSpecificAction(editor, psiClassFromEditor, dataContext);
        }
    }

    private void prepareDisplayChoosers(
            @NotNull Editor editor, @NotNull PsiClass psiClassFromEditor, @NotNull Project project) {
        displayChoosers.setEditor(editor);
        displayChoosers.setProject(project);
        displayChoosers.setPsiClassFromEditor(psiClassFromEditor);
    }

    private void forwardToSpecificAction(
            @NotNull Editor editor, @NotNull PsiClass psiClassFromEditor, DataContext dataContext) {
        boolean isBuilder = BuilderVerifier.isBuilder(psiClassFromEditor);
        PsiClass classToGo = findClassToGo(psiClassFromEditor, isBuilder);
        if (classToGo != null) {
            doActionWhenClassToGoIsFound(editor, psiClassFromEditor, dataContext, isBuilder, classToGo);
        } else {
            doActionWhenClassToGoIsNotFound(editor, psiClassFromEditor, dataContext, isBuilder);
        }
    }

    private PsiClass findClassToGo(PsiClass psiClassFromEditor, boolean isBuilder) {
        if (isBuilder) {
            return BuilderFinder.findClassForBuilder(psiClassFromEditor);
        }
        return BuilderFinder.findBuilderForClass(psiClassFromEditor);
    }

    protected abstract void doActionWhenClassToGoIsFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            DataContext dataContext,
            boolean isBuilder,
            @NotNull PsiClass classToGo);

    protected abstract void doActionWhenClassToGoIsNotFound(
            @NotNull Editor editor, @NotNull PsiClass psiClassFromEditor, DataContext dataContext, boolean isBuilder);
}
