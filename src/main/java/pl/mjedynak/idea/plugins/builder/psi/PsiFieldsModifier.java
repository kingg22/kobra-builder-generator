package pl.mjedynak.idea.plugins.builder.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiFieldsModifier {

    private PsiFieldsModifier() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void modifyFields(
            @NotNull List<@NotNull PsiField> psiFieldsForSetters,
            List<@NotNull PsiField> psiFieldsForConstructor,
            @NotNull PsiClass builderClass) {
        for (PsiField psiFieldsForSetter : psiFieldsForSetters) {
            removeModifiers(psiFieldsForSetter, builderClass);
        }
        for (PsiField psiFieldForConstructor : psiFieldsForConstructor) {
            removeModifiers(psiFieldForConstructor, builderClass);
        }
    }

    public static void modifyFieldsForInnerClass(
            @NotNull List<@NotNull PsiField> allFields, @NotNull PsiClass innerBuilderClass) {
        for (PsiField field : allFields) {
            removeModifiers(field, innerBuilderClass);
        }
    }

    private static void removeModifiers(@NotNull PsiField psiField, @NotNull PsiClass builderClass) {
        PsiElement copy = copyField(psiField, builderClass);
        builderClass.add(copy);
    }

    private static @NotNull PsiElement copyField(
            final @NotNull PsiField psiField, final @NotNull PsiClass builderClass) {
        PsiField builderField = PsiElementFactory.getInstance(builderClass.getProject())
                .createField(psiField.getName(), psiField.getType());
        if (builderField.getModifierList() != null) {
            builderField.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
        }

        return builderField;
    }
}
