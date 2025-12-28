package io.github.kingg22.kobra.builder.factory

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import io.github.kingg22.kobra.builder.psi.BestConstructorSelector
import io.github.kingg22.kobra.builder.psi.model.PsiFieldsForBuilder
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier
import org.jetbrains.annotations.Contract

public object PsiFieldsForBuilderFactory {
    @Contract("_, _ -> new")
    @JvmStatic
    public fun createPsiFieldsForBuilder(
        psiElementClassMembers: List<PsiElementClassMember<*>>,
        psiClass: PsiClass,
    ): PsiFieldsForBuilder {
        val allSelectedPsiFields: MutableList<PsiField> = mutableListOf()
        val psiFieldsFoundInSetters: MutableList<PsiField> = mutableListOf()
        for (psiElementClassMember in psiElementClassMembers) {
            val psiElement = psiElementClassMember.getPsiElement()
            if (psiElement is PsiField) {
                allSelectedPsiFields.add(psiElement)
                if (PsiFieldVerifier.isSetInSetterMethod(psiElement, psiClass)) {
                    psiFieldsFoundInSetters.add(psiElement)
                }
            }
        }
        val psiFieldsToFindInConstructor = getSubList(allSelectedPsiFields, psiFieldsFoundInSetters)
        val psiFieldsForConstructor: MutableList<PsiField> = mutableListOf()
        val bestConstructor = BestConstructorSelector.getBestConstructor(psiFieldsToFindInConstructor, psiClass)
        if (bestConstructor != null) {
            buildPsiFieldsForConstructor(psiFieldsForConstructor, allSelectedPsiFields, bestConstructor)
        }
        val psiFieldsForSetters = getSubList(psiFieldsFoundInSetters, psiFieldsForConstructor)

        return PsiFieldsForBuilder(
            psiFieldsForSetters,
            psiFieldsForConstructor,
            allSelectedPsiFields,
            bestConstructor,
        )
    }

    private fun buildPsiFieldsForConstructor(
        psiFieldsForConstructor: MutableList<PsiField>,
        allSelectedPsiFields: List<PsiField>,
        bestConstructor: PsiMethod,
    ) {
        for (selectedPsiField in allSelectedPsiFields) {
            if (PsiFieldVerifier.checkConstructor(selectedPsiField, bestConstructor)) {
                psiFieldsForConstructor.add(selectedPsiField)
            }
        }
    }

    private fun getSubList(inputList: List<PsiField>, listToRemove: List<PsiField>): List<PsiField> {
        val newList: MutableList<PsiField> = mutableListOf()
        for (inputPsiField in inputList) {
            var setterMustBeAdded = true
            for (psiFieldToRemove in listToRemove) {
                if (psiFieldToRemove.name == inputPsiField.name) {
                    setterMustBeAdded = false
                }
            }
            if (setterMustBeAdded) {
                newList.add(inputPsiField)
            }
        }
        return newList
    }
}
