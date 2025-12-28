package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiModifier

public object PsiFieldsModifier {
    @JvmStatic
    public fun modifyFields(
        psiFieldsForSetters: MutableList<PsiField>,
        psiFieldsForConstructor: MutableList<PsiField>,
        builderClass: PsiClass,
    ) {
        for (psiFieldsForSetter in psiFieldsForSetters) {
            removeModifiers(psiFieldsForSetter, builderClass)
        }
        for (psiFieldForConstructor in psiFieldsForConstructor) {
            removeModifiers(psiFieldForConstructor, builderClass)
        }
    }

    @JvmStatic
    public fun modifyFieldsForInnerClass(allFields: MutableList<PsiField>, innerBuilderClass: PsiClass) {
        for (field in allFields) {
            removeModifiers(field, innerBuilderClass)
        }
    }

    private fun removeModifiers(psiField: PsiField, builderClass: PsiClass) {
        val copy: PsiElement = copyField(psiField, builderClass)
        builderClass.add(copy)
    }

    private fun copyField(psiField: PsiField, builderClass: PsiClass): PsiElement {
        val builderField = PsiElementFactory.getInstance(builderClass.project)
            .createField(psiField.name, psiField.type)
        if (builderField.modifierList != null) {
            builderField.modifierList!!.setModifierProperty(PsiModifier.PRIVATE, true)
        }

        return builderField
    }
}
