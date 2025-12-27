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
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.factory.AbstractPopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.AbstractPopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

public abstract class AbstractBuilderActionHandler extends EditorActionHandler {

    protected final @NotNull AbstractPopupDisplayer popupDisplayer;
    protected final @NotNull AbstractPopupListFactory popupListFactory;
    protected DisplayChoosers displayChoosers;
    private final boolean reInitDisplayChoosers;

    public AbstractBuilderActionHandler(
            @NotNull AbstractPopupDisplayer popupDisplayer, @NotNull AbstractPopupListFactory popupListFactory) {
        this.popupDisplayer = popupDisplayer;
        this.popupListFactory = popupListFactory;
        this.reInitDisplayChoosers = true;
    }

    @VisibleForTesting
    AbstractBuilderActionHandler(
            @NotNull AbstractPopupDisplayer popupDisplayer,
            @NotNull AbstractPopupListFactory popupListFactory,
            DisplayChoosers displayChoosers) {
        this.popupDisplayer = popupDisplayer;
        this.popupListFactory = popupListFactory;
        this.displayChoosers = displayChoosers;
        this.reInitDisplayChoosers = false;
    }

    @SuppressWarnings("NullAway.Init")
    @Override
    public final void doExecute(@NotNull Editor editor, @Nullable Caret caret, @Nullable DataContext dataContext) {
        Project project = dataContext == null ? editor.getProject() : dataContext.getData(CommonDataKeys.PROJECT);
        if (project == null) return;
        PsiClass psiClassFromEditor = PsiHelper.getPsiClassFromEditor(editor, project);
        if (psiClassFromEditor != null) {
            if (reInitDisplayChoosers) displayChoosers = new DisplayChoosers(psiClassFromEditor, project, editor);
            forwardToSpecificAction(editor, psiClassFromEditor, dataContext);
        }
    }

    private void forwardToSpecificAction(
            @NotNull Editor editor, @NotNull PsiClass psiClassFromEditor, @Nullable DataContext dataContext) {
        boolean isBuilder = BuilderVerifier.isBuilder(psiClassFromEditor);
        PsiClass classToGo = findClassToGo(psiClassFromEditor, isBuilder);
        if (classToGo != null) {
            doActionWhenClassToGoIsFound(editor, psiClassFromEditor, dataContext, isBuilder, classToGo);
        } else {
            doActionWhenClassToGoIsNotFound(editor, psiClassFromEditor, dataContext, isBuilder);
        }
    }

    private @Nullable PsiClass findClassToGo(PsiClass psiClassFromEditor, boolean isBuilder) {
        if (isBuilder) {
            return BuilderFinder.findClassForBuilder(psiClassFromEditor);
        }
        return BuilderFinder.findBuilderForClass(psiClassFromEditor);
    }

    protected abstract void doActionWhenClassToGoIsFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder,
            @NotNull PsiClass classToGo);

    protected abstract void doActionWhenClassToGoIsNotFound(
            @NotNull Editor editor,
            @NotNull PsiClass psiClassFromEditor,
            @Nullable DataContext dataContext,
            boolean isBuilder);
}
