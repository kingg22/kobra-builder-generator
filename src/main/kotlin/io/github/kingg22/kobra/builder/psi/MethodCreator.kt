package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import io.github.kingg22.kobra.builder.settings.CodeStyleSettings

// TODO convert in helper util class
public class MethodCreator(private val elementFactory: PsiElementFactory, private val builderClassName: String?) {
    public fun createMethod(
        psiField: PsiField,
        methodPrefix: String?,
        srcClassFieldName: String?,
        useSingleField: Boolean,
    ): PsiMethod {
        val fieldName = psiField.name
        val fieldType = psiField.type.presentableText
        val fieldNamePrefix: String = CodeStyleSettings.FIELD_NAME_PREFIX
        val fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix.toRegex(), "")
        val parameterNamePrefix: String = CodeStyleSettings.PARAMETER_NAME_PREFIX
        val parameterName = parameterNamePrefix + fieldNameWithoutPrefix
        val methodName = MethodNameCreator.createMethodName(methodPrefix, fieldNameWithoutPrefix)
        val methodText = if (useSingleField) {
            val setterName = MethodNameCreator.createMethodName("set", fieldNameWithoutPrefix)
            "public $builderClassName $methodName($fieldType $parameterName) { $srcClassFieldName.$setterName($fieldName); return this; }"
        } else {
            "public $builderClassName $methodName($fieldType $parameterName) { this.$fieldName = $parameterName; return this; }"
        }
        return elementFactory.createMethodFromText(methodText, psiField)
    }
}
