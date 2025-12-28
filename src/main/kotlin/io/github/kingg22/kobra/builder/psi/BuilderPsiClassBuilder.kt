package io.github.kingg22.kobra.builder.psi

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.*
import io.github.kingg22.kobra.builder.settings.CodeStyleSettings
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier
import io.github.kingg22.kobra.builder.writer.BuilderContext
import org.apache.commons.lang3.StringUtils

public class BuilderPsiClassBuilder private constructor(context: BuilderContext) {
    private val butMethodCreator: ButMethodCreator
    private val copyConstructorCreator: CopyConstructorCreator
    private val methodCreator: MethodCreator
    private val srcClass: PsiClass = context.psiClassFromEditor
    private val builderClassName = context.className
    private val psiFieldsForSetters = context.psiFieldsForBuilder.fieldsForSetters.toMutableList()
    private val psiFieldsForConstructor = context.psiFieldsForBuilder.fieldsForConstructor.toMutableList()
    private val allSelectedPsiFields = context.psiFieldsForBuilder.allSelectedFields.toMutableList()
    private val bestConstructor = context.psiFieldsForBuilder.bestConstructor
    private lateinit var builderClass: PsiClass
    private val elementFactory: PsiElementFactory
    private val srcClassName = context.psiClassFromEditor.name
    private val srcClassFieldName: String = StringUtils.uncapitalize(srcClassName)
    private val useSingleField: Boolean = context.useSingleField
    private val isInline: Boolean = allSelectedPsiFields.size == psiFieldsForConstructor.size
    private val copyConstructor = context.hasAddCopyConstructor

    init {
        val javaPsiFacade = PsiHelper.getJavaPsiFacade(context.project)
        elementFactory = javaPsiFacade.elementFactory
        methodCreator = MethodCreator(elementFactory, builderClassName)
        butMethodCreator = ButMethodCreator(elementFactory)
        copyConstructorCreator = CopyConstructorCreator(elementFactory)
    }

    public fun withFields(): BuilderPsiClassBuilder {
        if (useSingleField) {
            val fieldText = "private $srcClassName $srcClassFieldName;"
            val singleField = elementFactory.createFieldFromText(fieldText, srcClass)
            builderClass.add(singleField)
        } else if (isInnerBuilder(builderClass)) {
            PsiFieldsModifier.modifyFieldsForInnerClass(allSelectedPsiFields, builderClass)
        } else {
            PsiFieldsModifier.modifyFields(psiFieldsForSetters, psiFieldsForConstructor, builderClass)
        }
        return this
    }

    public fun withConstructor(): BuilderPsiClassBuilder {
        val constructor = if (useSingleField) {
            elementFactory.createMethodFromText(
                "$builderClassName(){ $srcClassFieldName = new $srcClassName(); }",
                srcClass,
            )
        } else {
            elementFactory.createConstructor()
        }

        constructor
            .modifierList
            .setModifierProperty(if (copyConstructor) PsiModifier.PUBLIC else PsiModifier.PRIVATE, true)

        builderClass.add(constructor)
        return this
    }

    public fun withInitializingMethod(): BuilderPsiClassBuilder {
        val prefix: String = if (StringUtil.isVowel(srcClassName!!.lowercase()[0])) AN_PREFIX else A_PREFIX
        val staticMethod =
            elementFactory.createMethodFromText(
                "public static $builderClassName$prefix$srcClassName() { return new $builderClassName(); }",
                srcClass,
            )
        builderClass.add(staticMethod)
        return this
    }

    public fun withSetMethods(methodPrefix: String?): BuilderPsiClassBuilder {
        if (useSingleField || isInnerBuilder(builderClass)) {
            for (psiFieldForAssignment in allSelectedPsiFields) {
                createAndAddMethod(psiFieldForAssignment, methodPrefix)
            }
        } else {
            for (psiFieldForSetter in psiFieldsForSetters) {
                createAndAddMethod(psiFieldForSetter, methodPrefix)
            }
            for (psiFieldForConstructor in psiFieldsForConstructor) {
                createAndAddMethod(psiFieldForConstructor, methodPrefix)
            }
        }
        return this
    }

    public fun withButMethod(): BuilderPsiClassBuilder = apply {
        val method = butMethodCreator.butMethod(
            builderClassName,
            builderClass,
            srcClass,
            srcClassFieldName,
            useSingleField,
        )
        builderClass.add(method)
    }

    public fun withCopyConstructor(): BuilderPsiClassBuilder = apply {
        val method = copyConstructorCreator.copyConstructor(
            builderClass,
            srcClass,
            isInnerBuilder(builderClass),
            useSingleField,
        )
        builderClass.add(method)
    }

    public fun build(): PsiClass = if (useSingleField) {
        buildUseSingleField()
    } else if (isInline) {
        buildIsInline()
    } else {
        buildDefault()
    }

    private fun isInnerBuilder(aClass: PsiClass): Boolean = aClass.hasModifierProperty(PsiModifier.STATIC)

    private fun createAndAddMethod(psiField: PsiField, methodPrefix: String?) {
        builderClass.add(methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, useSingleField))
    }

    private fun buildUseSingleField(): PsiClass {
        val buildMethodText = "public $srcClassName build() { return $srcClassFieldName; }"
        val buildMethod = elementFactory.createMethodFromText(buildMethodText, srcClass)
        builderClass.add(buildMethod)
        return builderClass
    }

    private fun buildIsInline(): PsiClass {
        val buildMethodText = StringBuilder()
        buildMethodText.append("public ").append(srcClassName).append(" build() { ")
        buildMethodText.append("return ")
        appendConstructor(buildMethodText)
        buildMethodText.append(" }")
        val buildMethod = elementFactory.createMethodFromText(buildMethodText.toString(), srcClass)
        builderClass.add(buildMethod)
        return builderClass
    }

    private fun buildDefault(): PsiClass {
        val buildMethodText = StringBuilder()
        buildMethodText.append("public ").append(srcClassName).append(" build() { ")
        buildMethodText.append(srcClassName)
            .append(SPACE)
            .append(srcClassFieldName)
            .append(" = ")
        appendConstructor(buildMethodText)
        appendSetMethodsOrAssignments(buildMethodText)
        buildMethodText.append("return ").append(srcClassFieldName).append(";")
        buildMethodText.append(" }")
        val buildMethod = elementFactory.createMethodFromText(buildMethodText.toString(), srcClass)
        builderClass.add(buildMethod)
        return builderClass
    }

    private fun appendConstructor(buildMethodText: StringBuilder) {
        val constructorParameters = createConstructorParameters()
        buildMethodText
            .append("new ")
            .append(srcClassName)
            .append("(")
            .append(constructorParameters)
            .append(");")
    }

    private fun appendSetMethodsOrAssignments(buildMethodText: StringBuilder) {
        appendSetMethods(buildMethodText, psiFieldsForSetters)
        if (isInnerBuilder(builderClass)) {
            val fieldsSetViaAssignment: MutableSet<PsiField> = HashSet(allSelectedPsiFields)
            fieldsSetViaAssignment.removeAll(psiFieldsForSetters.toSet())
            fieldsSetViaAssignment.removeAll(psiFieldsForConstructor.toSet())
            appendAssignments(buildMethodText, fieldsSetViaAssignment)
        }
    }

    private fun appendSetMethods(buildMethodText: StringBuilder, fieldsToBeSetViaSetter: MutableCollection<PsiField>) {
        for (psiFieldsForSetter in fieldsToBeSetViaSetter) {
            val fieldNamePrefix: String = CodeStyleSettings.FIELD_NAME_PREFIX
            val fieldName = psiFieldsForSetter.name
            val fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix.toRegex(), "")
            val fieldNameUppercase = StringUtils.capitalize(fieldNameWithoutPrefix)
            buildMethodText
                .append(srcClassFieldName)
                .append(".set")
                .append(fieldNameUppercase)
                .append("(")
                .append(fieldName)
                .append(");")
        }
    }

    private fun appendAssignments(
        buildMethodText: StringBuilder,
        fieldsSetViaAssignment: MutableCollection<PsiField>,
    ) {
        for (field in fieldsSetViaAssignment) {
            buildMethodText
                .append(srcClassFieldName)
                .append(".")
                .append(field.name)
                .append("=")
                .append("this.")
                .append(field.name)
                .append(";")
        }
    }

    private fun createConstructorParameters(): String {
        if (bestConstructor == null) {
            return ""
        }
        val sb = StringBuilder()
        for (psiParameter in bestConstructor.parameterList.parameters) {
            var parameterHasMatchingField = false
            for (psiField in psiFieldsForConstructor) {
                if (PsiFieldVerifier.areNameAndTypeEqual(psiField, psiParameter)) {
                    sb.append(psiField.name).append(SEMICOLON)
                    parameterHasMatchingField = true
                    break
                }
            }
            if (!parameterHasMatchingField) {
                sb.append(getDefaultValue(psiParameter.type)).append(SEMICOLON)
            }
        }
        removeLastSemicolon(sb)
        return sb.toString()
    }

    public companion object : BuilderPsiClassBuilderFactory {
        private const val SPACE = " "
        private const val A_PREFIX = " a"
        private const val AN_PREFIX = " an"
        private const val SEMICOLON = ","

        @JvmStatic
        public override fun aBuilder(context: BuilderContext): BuilderPsiClassBuilder {
            val builder = BuilderPsiClassBuilder(context)
            val javaDirectoryService: JavaDirectoryService = PsiHelper.javaDirectoryService
            builder.builderClass = javaDirectoryService.createClass(
                requireNotNull(context.targetDirectory) { "Target directory cannot be null." },
                builder.builderClassName,
            )
            val modifierList = requireNotNull(builder.builderClass.modifierList) { "Modifier list cannot be null." }
            modifierList.setModifierProperty(PsiModifier.FINAL, true)
            return builder
        }

        @JvmStatic
        public override fun anInnerBuilder(context: BuilderContext): BuilderPsiClassBuilder {
            val builder = BuilderPsiClassBuilder(context)
            builder.builderClass = builder.elementFactory.createClass(builder.builderClassName)
            val modifierList = requireNotNull(builder.builderClass.modifierList) { "Modifier list cannot be null." }
            modifierList.setModifierProperty(PsiModifier.FINAL, true)
            modifierList.setModifierProperty(PsiModifier.STATIC, true)
            return builder
        }

        private fun getDefaultValue(type: PsiType): String = when (type) {
            PsiTypes.booleanType() -> "false"
            PsiTypes.byteType(), PsiTypes.shortType(), PsiTypes.intType() -> "0"
            PsiTypes.longType() -> "0L"
            PsiTypes.floatType() -> "0.0f"
            PsiTypes.doubleType() -> "0.0d"
            PsiTypes.charType() -> "'\\u0000'"
            else -> "null"
        }

        private fun removeLastSemicolon(sb: StringBuilder) {
            if (sb.toString().endsWith(SEMICOLON)) {
                sb.deleteCharAt(sb.length - 1)
            }
        }
    }
}
