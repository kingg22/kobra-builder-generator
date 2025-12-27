package pl.mjedynak.idea.plugins.builder.factory;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.psi.BestConstructorSelector;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForBuilder;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

public class PsiFieldsForBuilderFactory {

    private PsiFieldsForBuilderFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Contract("_, _ -> new")
    public static @NotNull PsiFieldsForBuilder createPsiFieldsForBuilder(
            @NotNull List<PsiElementClassMember<?>> psiElementClassMembers, @NotNull PsiClass psiClass) {
        List<PsiField> allSelectedPsiFields = Lists.newArrayList();
        List<PsiField> psiFieldsFoundInSetters = Lists.newArrayList();
        for (PsiElementClassMember<?> psiElementClassMember : psiElementClassMembers) {
            PsiElement psiElement = psiElementClassMember.getPsiElement();
            if (psiElement instanceof PsiField) {
                allSelectedPsiFields.add((PsiField) psiElement);
                if (PsiFieldVerifier.isSetInSetterMethod((PsiField) psiElement, psiClass)) {
                    psiFieldsFoundInSetters.add((PsiField) psiElement);
                }
            }
        }
        List<PsiField> psiFieldsToFindInConstructor = getSubList(allSelectedPsiFields, psiFieldsFoundInSetters);
        List<PsiField> psiFieldsForConstructor = Lists.newArrayList();
        PsiMethod bestConstructor = BestConstructorSelector.getBestConstructor(psiFieldsToFindInConstructor, psiClass);
        if (bestConstructor != null) {
            buildPsiFieldsForConstructor(psiFieldsForConstructor, allSelectedPsiFields, bestConstructor);
        }
        List<PsiField> psiFieldsForSetters = getSubList(psiFieldsFoundInSetters, psiFieldsForConstructor);

        return new PsiFieldsForBuilder(
                psiFieldsForSetters, psiFieldsForConstructor, allSelectedPsiFields, bestConstructor);
    }

    private static void buildPsiFieldsForConstructor(
            @NotNull List<PsiField> psiFieldsForConstructor,
            @NotNull List<PsiField> allSelectedPsiFields,
            @NotNull PsiMethod bestConstructor) {
        for (PsiField selectedPsiField : allSelectedPsiFields) {
            if (PsiFieldVerifier.checkConstructor(selectedPsiField, bestConstructor)) {
                psiFieldsForConstructor.add(selectedPsiField);
            }
        }
    }

    private static @NotNull List<PsiField> getSubList(
            @NotNull List<PsiField> inputList, @NotNull List<PsiField> listToRemove) {
        List<PsiField> newList = Lists.newArrayList();
        for (PsiField inputPsiField : inputList) {
            boolean setterMustBeAdded = true;
            for (PsiField psiFieldToRemove : listToRemove) {
                if (psiFieldToRemove.getName().equals(inputPsiField.getName())) {
                    setterMustBeAdded = false;
                }
            }
            if (setterMustBeAdded) {
                newList.add(inputPsiField);
            }
        }
        return newList;
    }
}
