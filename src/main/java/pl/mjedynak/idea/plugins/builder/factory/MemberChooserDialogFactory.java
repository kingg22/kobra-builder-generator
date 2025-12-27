package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.project.Project;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MemberChooserDialogFactory {

    private MemberChooserDialogFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    static final String TITLE = "Select Fields to Be Available in Builder";

    public static @NotNull MemberChooser<PsiElementClassMember<?>> getMemberChooserDialog(
            @NotNull List<PsiElementClassMember<?>> elements, @NotNull Project project) {
        PsiElementClassMember<?>[] psiElementClassMembers = elements.toArray(new PsiElementClassMember[0]);
        MemberChooser<PsiElementClassMember<?>> memberChooserDialog =
                createNewInstance(project, psiElementClassMembers);
        memberChooserDialog.setCopyJavadocVisible(false);
        memberChooserDialog.selectElements(psiElementClassMembers);
        memberChooserDialog.setTitle(TITLE);
        return memberChooserDialog;
    }

    @Contract("_, _ -> new")
    private static @NotNull MemberChooser<PsiElementClassMember<?>> createNewInstance(
            @NotNull Project project, PsiElementClassMember<?>[] psiElementClassMembers) {
        return new MemberChooser<>(psiElementClassMembers, false, true, project, false);
    }
}
