package pl.mjedynak.idea.plugins.builder.gui.helper;

import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiHelper {

    private GuiHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void showMessageDialog(
            @Nullable Project project, @NotNull String message, @NotNull String title, @Nullable Icon icon) {
        Messages.showMessageDialog(project, message, title, icon);
    }

    public static void includeCurrentPlaceAsChangePlace(@NotNull Project project) {
        IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace();
    }

    public static void positionCursor(
            @NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement) {
        CodeInsightUtil.positionCursor(project, psiFile, psiElement);
    }
}
