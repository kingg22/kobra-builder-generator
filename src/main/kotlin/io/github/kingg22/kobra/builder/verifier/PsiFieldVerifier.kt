package io.github.kingg22.kobra.builder.verifier

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiModifierList
import com.intellij.psi.PsiParameter
import io.github.kingg22.kobra.builder.settings.CodeStyleSettings
import org.apache.commons.text.WordUtils
import org.jetbrains.annotations.VisibleForTesting

public object PsiFieldVerifier {
    @VisibleForTesting
    public const val SET_PREFIX: String = "set"

    @VisibleForTesting
    public const val GET_PREFIX: String = "get"

    @JvmStatic
    public fun isSetInConstructor(psiField: PsiField, psiClass: PsiClass): Boolean {
        var result = false
        val constructors = psiClass.constructors
        var i = 0
        while (i < constructors.size && !result) {
            result = checkConstructor(psiField, constructors[i]!!)
            i++
        }
        return result
    }

    @JvmStatic
    public fun checkConstructor(psiField: PsiField, constructor: PsiMethod): Boolean {
        val parameterList = constructor.parameterList
        val parameters = parameterList.parameters
        return iterateOverParameters(psiField, parameters)
    }

    private fun iterateOverParameters(psiField: PsiField, parameters: Array<PsiParameter>): Boolean {
        var result = false
        var i = 0
        while (i < parameters.size && !result) {
            result = areNameAndTypeEqual(psiField, parameters[i])
            i++
        }
        return result
    }

    public fun areNameAndTypeEqual(psiField: PsiField, parameter: PsiParameter): Boolean {
        val parameterNamePrefix: String = CodeStyleSettings.PARAMETER_NAME_PREFIX
        val parameterName = parameter.name
        val parameterNameWithoutPrefix = parameterName.replace(parameterNamePrefix, "")
        val fieldNamePrefix: String = CodeStyleSettings.FIELD_NAME_PREFIX
        val fieldName = psiField.name
        val fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix.toRegex(), "")
        return parameterNameWithoutPrefix == fieldNameWithoutPrefix &&
            parameter.type == psiField.type
    }

    @JvmStatic
    public fun isSetInSetterMethod(psiField: PsiField, psiClass: PsiClass): Boolean =
        methodIsNotPrivateAndHasProperPrefixAndProperName(psiField, psiClass, SET_PREFIX)

    @JvmStatic
    public fun hasGetterMethod(psiField: PsiField, psiClass: PsiClass): Boolean =
        methodIsNotPrivateAndHasProperPrefixAndProperName(psiField, psiClass, GET_PREFIX)

    private fun methodIsNotPrivateAndHasProperPrefixAndProperName(
        psiField: PsiField,
        psiClass: PsiClass,
        prefix: String,
    ): Boolean {
        var result = false
        for (method in psiClass.allMethods) {
            if (methodIsNotPrivate(method) && methodHaProperPrefixAndProperName(psiField, method, prefix)) {
                result = true
                break
            }
        }
        return result
    }

    private fun methodIsNotPrivate(method: PsiMethod): Boolean {
        val modifierList = method.modifierList
        return modifierListHasNoPrivateModifier(modifierList)
    }

    private fun methodHaProperPrefixAndProperName(psiField: PsiField, method: PsiMethod, prefix: String): Boolean {
        val fieldNamePrefix = CodeStyleSettings.FIELD_NAME_PREFIX
        val fieldNameWithoutPrefix = psiField.name.replace(fieldNamePrefix, "")
        return method.name == prefix + WordUtils.capitalize(fieldNameWithoutPrefix)
    }

    private fun modifierListHasNoPrivateModifier(modifierList: PsiModifierList): Boolean =
        !modifierList.hasExplicitModifier(PsiModifier.PRIVATE)
}
