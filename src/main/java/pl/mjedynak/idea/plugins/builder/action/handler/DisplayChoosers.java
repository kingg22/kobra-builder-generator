package pl.mjedynak.idea.plugins.builder.action.handler;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.factory.CreateBuilderDialogFactory;
import pl.mjedynak.idea.plugins.builder.factory.MemberChooserDialogFactory;
import pl.mjedynak.idea.plugins.builder.factory.PsiFieldsForBuilderFactory;
import pl.mjedynak.idea.plugins.builder.gui.CreateBuilderDialog;
import pl.mjedynak.idea.plugins.builder.psi.PsiFieldSelector;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForBuilder;
import pl.mjedynak.idea.plugins.builder.writer.BuilderContext;
import pl.mjedynak.idea.plugins.builder.writer.BuilderWriter;

public class DisplayChoosers {

    private @NotNull PsiClass psiClassFromEditor;
    private @NotNull Project project;
    private @NotNull Editor editor;
    private final @NotNull BuilderWriter builderWriter;

    public DisplayChoosers(@NotNull BuilderWriter builderWriter) {
        this.builderWriter = builderWriter;
    }

    public void run(@Nullable PsiClass existingBuilder) {
        CreateBuilderDialog createBuilderDialog = showDialog(existingBuilder);
        if (createBuilderDialog != null && createBuilderDialog.isOK()) {
            PsiDirectory targetDirectory = createBuilderDialog.getTargetDirectory();
            String className = createBuilderDialog.getClassName();
            String methodPrefix = createBuilderDialog.getMethodPrefix();
            boolean innerBuilder = createBuilderDialog.isInnerBuilder();
            boolean useSingleField = createBuilderDialog.useSingleField();
            boolean hasButMethod = createBuilderDialog.hasButMethod();
            List<PsiElementClassMember<?>> fieldsToDisplay =
                    getFieldsToIncludeInBuilder(psiClassFromEditor, innerBuilder, useSingleField, hasButMethod);
            MemberChooser<PsiElementClassMember<?>> memberChooserDialog =
                    MemberChooserDialogFactory.getMemberChooserDialog(fieldsToDisplay, project);
            memberChooserDialog.show();
            writeBuilderIfNecessary(
                    targetDirectory,
                    className,
                    methodPrefix,
                    memberChooserDialog,
                    createBuilderDialog,
                    existingBuilder);
        }
    }

    private void writeBuilderIfNecessary(
            PsiDirectory targetDirectory,
            String className,
            String methodPrefix,
            @NotNull MemberChooser<PsiElementClassMember<?>> memberChooserDialog,
            CreateBuilderDialog createBuilderDialog,
            PsiClass existingBuilder) {
        if (memberChooserDialog.isOK()) {
            List<PsiElementClassMember<?>> selectedElements = memberChooserDialog.getSelectedElements();
            if (selectedElements == null) return;
            PsiFieldsForBuilder psiFieldsForBuilder =
                    PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(selectedElements, psiClassFromEditor);
            BuilderContext context = new BuilderContext(
                    project,
                    psiFieldsForBuilder,
                    targetDirectory,
                    className,
                    psiClassFromEditor,
                    methodPrefix,
                    createBuilderDialog.isInnerBuilder(),
                    createBuilderDialog.hasButMethod(),
                    createBuilderDialog.useSingleField(),
                    createBuilderDialog.hasAddCopyConstructor());
            builderWriter.writeBuilder(context, existingBuilder);
        }
    }

    private @Nullable CreateBuilderDialog showDialog(PsiClass existingBuilder) {
        PsiFile file = PsiHelper.getPsiFile(editor, project);
        if (file == null) return null;
        CreateBuilderDialog dialog = CreateBuilderDialogFactory.createBuilderDialog(
                psiClassFromEditor, project, PsiHelper.getPackage(file.getContainingDirectory()), existingBuilder);
        dialog.show();
        return dialog;
    }

    private static @NotNull List<PsiElementClassMember<?>> getFieldsToIncludeInBuilder(
            PsiClass clazz, boolean innerBuilder, boolean useSingleField, boolean hasButMethod) {
        return PsiFieldSelector.selectFieldsToIncludeInBuilder(clazz, innerBuilder, useSingleField, hasButMethod);
    }

    public void setPsiClassFromEditor(@NotNull PsiClass psiClassFromEditor) {
        this.psiClassFromEditor = psiClassFromEditor;
    }

    public void setProject(@NotNull Project project) {
        this.project = project;
    }

    public void setEditor(@NotNull Editor editor) {
        this.editor = editor;
    }
}
