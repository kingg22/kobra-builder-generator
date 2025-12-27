package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PsiElementClassMemberFactory {

    private PsiElementClassMemberFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Contract("_ -> new")
    public static @NotNull PsiElementClassMember<?> createPsiElementClassMember(@NotNull PsiField psiField) {
        return new PsiFieldMember(psiField);
    }
}
