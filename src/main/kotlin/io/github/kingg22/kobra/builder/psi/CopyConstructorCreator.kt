package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PropertyUtilBase
import com.intellij.util.IncorrectOperationException

public class CopyConstructorCreator(private val elementFactory: PsiElementFactory) {
    public fun copyConstructor(
        builderClass: PsiClass,
        srcClass: PsiClass,
        isInnerBuilder: Boolean,
        useSingleField: Boolean,
    ): PsiMethod {
        val fields = builderClass.allFields
        val text = StringBuilder("public ${builderClass.nameIdentifier!!.text}(${srcClass.qualifiedName} other) { ")

        for (field in fields) {
            text.append("this.").append(field.name).append(" = other")

            if (srcClass.isRecord) {
                text.append(".").append(field.name).append("();")
            } else if (isInnerBuilder) {
                if (useSingleField) {
                    text.append(";")
                } else {
                    text.append(".").append(field.name).append(";")
                }
            } else {
                if (useSingleField) {
                    text.append(";")
                } else {
                    text.append(".")
                        .append(findFieldGetter(srcClass, field).name)
                        .append("();")
                }
            }
        }
        text.append(" }")

        return elementFactory.createMethodFromText(text.toString(), srcClass)
    }

    @Throws(IncorrectOperationException::class)
    private fun findFieldGetter(srcClass: PsiClass, field: PsiField): PsiMethod =
        srcClass.findMethodBySignature(PropertyUtilBase.generateGetterPrototype(field), true)
            ?: throw IncorrectOperationException("Could not create copy constructor as cannot get field getters")
}
