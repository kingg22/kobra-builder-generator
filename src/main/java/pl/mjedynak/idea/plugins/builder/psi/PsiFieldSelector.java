package pl.mjedynak.idea.plugins.builder.psi;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.factory.PsiElementClassMemberFactory;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

public class PsiFieldSelector {

    private PsiFieldSelector() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static @NotNull List<PsiElementClassMember<?>> selectFieldsToIncludeInBuilder(
            @NotNull PsiClass psiClass, boolean innerBuilder, boolean useSingleField, boolean hasButMethod) {
        List<PsiElementClassMember<?>> result = new ArrayList<>();

        List<PsiField> psiFields = stream(psiClass.getAllFields())
                .filter(psiField -> !"serialVersionUID".equals(psiField.getName()))
                .toList();
        Iterable<PsiField> filtered = psiFields.stream()
                .filter(psiField -> isAppropriate(psiClass, psiField, innerBuilder, useSingleField, hasButMethod))
                .collect(toList());

        for (PsiField psiField : filtered) {
            result.add(PsiElementClassMemberFactory.createPsiElementClassMember(psiField));
        }
        return result;
    }

    private static boolean isAppropriate(
            PsiClass psiClass, PsiField psiField, boolean innerBuilder, boolean useSingleField, boolean hasButMethod) {
        if (useSingleField && hasButMethod) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)
                    && PsiFieldVerifier.hasGetterMethod(psiField, psiClass);
        } else if (useSingleField) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass);
        } else if (!innerBuilder) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)
                    || PsiFieldVerifier.isSetInConstructor(psiField, psiClass);
        }
        return true;
    }
}
