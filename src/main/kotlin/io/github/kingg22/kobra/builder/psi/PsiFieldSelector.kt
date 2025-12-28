package io.github.kingg22.kobra.builder.psi

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import io.github.kingg22.kobra.builder.factory.PsiElementClassMemberFactory
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier

public object PsiFieldSelector {
    @JvmStatic
    public fun selectFieldsToIncludeInBuilder(
        psiClass: PsiClass,
        innerBuilder: Boolean,
        useSingleField: Boolean,
        hasButMethod: Boolean,
    ): List<PsiElementClassMember<*>> = psiClass.allFields
        .filterNotNull()
        .filter { psiField ->
            "serialVersionUID" != psiField.name && isAppropriate(
                psiClass,
                psiField,
                innerBuilder,
                useSingleField,
                hasButMethod,
            )
        }.map { psiField ->
            PsiElementClassMemberFactory.createPsiElementClassMember(psiField)
        }

    private fun isAppropriate(
        psiClass: PsiClass,
        psiField: PsiField,
        innerBuilder: Boolean,
        useSingleField: Boolean,
        hasButMethod: Boolean,
    ): Boolean {
        if (useSingleField && hasButMethod) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) &&
                PsiFieldVerifier.hasGetterMethod(psiField, psiClass)
        } else if (useSingleField) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)
        } else if (!innerBuilder) {
            return PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) ||
                PsiFieldVerifier.isSetInConstructor(psiField, psiClass)
        }
        return true
    }
}
