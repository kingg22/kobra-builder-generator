package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameterList
import io.github.kingg22.kobra.builder.settings.CodeStyleSettings
import org.apache.commons.lang3.StringUtils

// TODO convert in helper util class
public class ButMethodCreator(private val elementFactory: PsiElementFactory) {
    public fun butMethod(
        builderClassName: String?,
        builderClass: PsiClass,
        srcClass: PsiClass?,
        srcClassFieldName: String?,
        useSingleField: Boolean,
    ): PsiMethod {
        val methods = builderClass.methods
        val text = StringBuilder("public $builderClassName but() { return ")
        for (method in methods) {
            val parameterList = method.parameterList
            if (methodIsNotConstructor(builderClassName, method)) {
                appendMethod(text, method, parameterList, srcClassFieldName, useSingleField)
            }
        }
        deleteLastDot(text)
        text.append("; }")
        return elementFactory.createMethodFromText(text.toString(), srcClass)
    }

    private fun appendMethod(
        text: StringBuilder,
        method: PsiMethod,
        parameterList: PsiParameterList,
        srcClassFieldName: String?,
        useSingleField: Boolean,
    ) {
        if (isInitializingMethod(parameterList)) {
            text.append(method.name).append("().")
        } else {
            val parameterName = parameterList.parameters[0].name
            val parameterNamePrefix: String = CodeStyleSettings.PARAMETER_NAME_PREFIX
            val parameterNameWithoutPrefix = parameterName.replaceFirst(parameterNamePrefix.toRegex(), "")
            val fieldNamePrefix: String = CodeStyleSettings.FIELD_NAME_PREFIX
            text.append(method.name).append("(")
            if (useSingleField) {
                text.append(srcClassFieldName)
                    .append(".get")
                    .append(StringUtils.capitalize(parameterNameWithoutPrefix))
                    .append("()")
            } else {
                text.append(fieldNamePrefix).append(parameterNameWithoutPrefix)
            }
            text.append(").")
        }
    }

    private fun isInitializingMethod(parameterList: PsiParameterList): Boolean = parameterList.parametersCount <= 0

    private fun deleteLastDot(text: StringBuilder) {
        text.deleteCharAt(text.length - 1)
    }

    private fun methodIsNotConstructor(builderClassName: String?, method: PsiMethod): Boolean =
        method.name != builderClassName
}
