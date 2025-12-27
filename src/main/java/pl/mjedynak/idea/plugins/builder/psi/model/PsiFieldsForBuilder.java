package pl.mjedynak.idea.plugins.builder.psi.model;

import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import java.util.List;

public record PsiFieldsForBuilder(
        List<PsiField> fieldsForSetters,
        List<PsiField> fieldsForConstructor,
        List<PsiField> allSelectedFields,
        PsiMethod bestConstructor) {

    public PsiFieldsForBuilder(
            List<PsiField> fieldsForSetters,
            List<PsiField> fieldsForConstructor,
            List<PsiField> allSelectedFields,
            PsiMethod bestConstructor) {
        this.fieldsForSetters = ImmutableList.copyOf(fieldsForSetters);
        this.fieldsForConstructor = ImmutableList.copyOf(fieldsForConstructor);
        this.allSelectedFields = ImmutableList.copyOf(allSelectedFields);
        this.bestConstructor = bestConstructor;
    }
}
