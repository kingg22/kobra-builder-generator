package pl.mjedynak.idea.plugins.builder.psi;

import static com.intellij.openapi.util.text.StringUtil.isVowel;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.settings.CodeStyleSettings;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;
import pl.mjedynak.idea.plugins.builder.writer.BuilderContext;

public class BuilderPsiClassBuilder {

    private static final String SPACE = " ";
    private static final String A_PREFIX = " a";
    private static final String AN_PREFIX = " an";
    private static final String SEMICOLON = ",";

    private final ButMethodCreator butMethodCreator;
    private final CopyConstructorCreator copyConstructorCreator;
    private final MethodCreator methodCreator;

    private final PsiClass srcClass;
    private final String builderClassName;

    private final List<PsiField> psiFieldsForSetters;
    private final List<PsiField> psiFieldsForConstructor;
    private final List<PsiField> allSelectedPsiFields;
    private final @Nullable PsiMethod bestConstructor;

    @SuppressWarnings("NullAway")
    private @NotNull PsiClass builderClass;

    private final PsiElementFactory elementFactory;
    private final String srcClassName;
    private final String srcClassFieldName;

    private final boolean useSingleField;
    private final boolean isInline;
    private final boolean copyConstructor;

    private BuilderPsiClassBuilder(@NotNull BuilderContext context) {
        JavaPsiFacade javaPsiFacade = PsiHelper.getJavaPsiFacade(context.project());
        elementFactory = javaPsiFacade.getElementFactory();
        srcClass = context.psiClassFromEditor();
        builderClassName = context.className();
        srcClassName = context.psiClassFromEditor().getName();
        srcClassFieldName = StringUtils.uncapitalize(srcClassName);
        psiFieldsForSetters = context.psiFieldsForBuilder().fieldsForSetters();
        psiFieldsForConstructor = context.psiFieldsForBuilder().fieldsForConstructor();
        allSelectedPsiFields = context.psiFieldsForBuilder().allSelectedFields();
        useSingleField = context.useSingleField();
        bestConstructor = context.psiFieldsForBuilder().bestConstructor();
        methodCreator = new MethodCreator(elementFactory, builderClassName);
        butMethodCreator = new ButMethodCreator(elementFactory);
        copyConstructorCreator = new CopyConstructorCreator(elementFactory);
        isInline = allSelectedPsiFields.size() == psiFieldsForConstructor.size();
        copyConstructor = context.hasAddCopyConstructor();
    }

    public static @NotNull BuilderPsiClassBuilder aBuilder(@NotNull BuilderContext context) {
        BuilderPsiClassBuilder builder = new BuilderPsiClassBuilder(context);
        JavaDirectoryService javaDirectoryService = PsiHelper.getJavaDirectoryService();
        builder.builderClass = javaDirectoryService.createClass(context.targetDirectory(), builder.builderClassName);
        PsiModifierList modifierList = builder.builderClass.getModifierList();
        modifierList.setModifierProperty(PsiModifier.FINAL, true);
        return builder;
    }

    public static @NotNull BuilderPsiClassBuilder anInnerBuilder(@NotNull BuilderContext context) {
        BuilderPsiClassBuilder builder = new BuilderPsiClassBuilder(context);
        builder.builderClass = builder.elementFactory.createClass(builder.builderClassName);
        PsiModifierList modifierList = builder.builderClass.getModifierList();
        modifierList.setModifierProperty(PsiModifier.FINAL, true);
        modifierList.setModifierProperty(PsiModifier.STATIC, true);
        return builder;
    }

    public BuilderPsiClassBuilder withFields() {
        if (useSingleField) {
            String fieldText = "private " + srcClassName + " " + srcClassFieldName + ";";
            PsiField singleField = elementFactory.createFieldFromText(fieldText, srcClass);
            builderClass.add(singleField);
        } else if (isInnerBuilder(builderClass)) {
            PsiFieldsModifier.modifyFieldsForInnerClass(allSelectedPsiFields, builderClass);
        } else {
            PsiFieldsModifier.modifyFields(psiFieldsForSetters, psiFieldsForConstructor, builderClass);
        }
        return this;
    }

    public BuilderPsiClassBuilder withConstructor() {
        PsiMethod constructor;
        if (useSingleField) {
            constructor = elementFactory.createMethodFromText(
                    builderClassName + "(){ " + srcClassFieldName + " = new " + srcClassName + "(); }", srcClass);
        } else {
            constructor = elementFactory.createConstructor();
        }

        constructor
                .getModifierList()
                .setModifierProperty(copyConstructor ? PsiModifier.PUBLIC : PsiModifier.PRIVATE, true);

        builderClass.add(constructor);
        return this;
    }

    public BuilderPsiClassBuilder withInitializingMethod() {
        String prefix = isVowel(srcClassName.toLowerCase(Locale.ENGLISH).charAt(0)) ? AN_PREFIX : A_PREFIX;
        PsiMethod staticMethod = elementFactory.createMethodFromText(
                "public static " + builderClassName + prefix + srcClassName + "() { return new " + builderClassName
                        + "(); }",
                srcClass);
        builderClass.add(staticMethod);
        return this;
    }

    public BuilderPsiClassBuilder withSetMethods(String methodPrefix) {
        if (useSingleField || isInnerBuilder(builderClass)) {
            for (PsiField psiFieldForAssignment : allSelectedPsiFields) {
                createAndAddMethod(psiFieldForAssignment, methodPrefix);
            }
        } else {
            for (PsiField psiFieldForSetter : psiFieldsForSetters) {
                createAndAddMethod(psiFieldForSetter, methodPrefix);
            }
            for (PsiField psiFieldForConstructor : psiFieldsForConstructor) {
                createAndAddMethod(psiFieldForConstructor, methodPrefix);
            }
        }
        return this;
    }

    private boolean isInnerBuilder(@NotNull PsiClass aClass) {
        return aClass.hasModifierProperty(PsiModifier.STATIC);
    }

    public BuilderPsiClassBuilder withButMethod() {
        PsiMethod method =
                butMethodCreator.butMethod(builderClassName, builderClass, srcClass, srcClassFieldName, useSingleField);
        builderClass.add(method);
        return this;
    }

    public BuilderPsiClassBuilder withCopyConstructor() {
        final PsiMethod method = copyConstructorCreator.copyConstructor(
                builderClass, srcClass, isInnerBuilder(builderClass), useSingleField);
        builderClass.add(method);
        return this;
    }

    private void createAndAddMethod(PsiField psiField, String methodPrefix) {
        builderClass.add(methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, useSingleField));
    }

    public PsiClass build() {
        if (useSingleField) {
            return buildUseSingleField();
        } else if (isInline) {
            return buildIsInline();
        } else {
            return buildDefault();
        }
    }

    private PsiClass buildUseSingleField() {
        String buildMethodText = "public " + srcClassName + " build() { " + "return " + srcClassFieldName + ";" + " }";
        PsiMethod buildMethod = elementFactory.createMethodFromText(buildMethodText, srcClass);
        builderClass.add(buildMethod);
        return builderClass;
    }

    private PsiClass buildIsInline() {
        StringBuilder buildMethodText = new StringBuilder();
        buildMethodText.append("public ").append(srcClassName).append(" build() { ");
        buildMethodText.append("return ");
        appendConstructor(buildMethodText);
        buildMethodText.append(" }");
        PsiMethod buildMethod = elementFactory.createMethodFromText(buildMethodText.toString(), srcClass);
        builderClass.add(buildMethod);
        return builderClass;
    }

    private PsiClass buildDefault() {
        StringBuilder buildMethodText = new StringBuilder();
        buildMethodText.append("public ").append(srcClassName).append(" build() { ");
        buildMethodText
                .append(srcClassName)
                .append(SPACE)
                .append(srcClassFieldName)
                .append(" = ");
        appendConstructor(buildMethodText);
        appendSetMethodsOrAssignments(buildMethodText);
        buildMethodText.append("return ").append(srcClassFieldName).append(";");
        buildMethodText.append(" }");
        PsiMethod buildMethod = elementFactory.createMethodFromText(buildMethodText.toString(), srcClass);
        builderClass.add(buildMethod);
        return builderClass;
    }

    private void appendConstructor(@NotNull StringBuilder buildMethodText) {
        String constructorParameters = createConstructorParameters();
        buildMethodText
                .append("new ")
                .append(srcClassName)
                .append("(")
                .append(constructorParameters)
                .append(");");
    }

    private void appendSetMethodsOrAssignments(@NotNull StringBuilder buildMethodText) {
        appendSetMethods(buildMethodText, psiFieldsForSetters);
        if (isInnerBuilder(builderClass)) {
            Set<PsiField> fieldsSetViaAssignment = new HashSet<>(allSelectedPsiFields);
            fieldsSetViaAssignment.removeAll(psiFieldsForSetters);
            fieldsSetViaAssignment.removeAll(psiFieldsForConstructor);
            appendAssignments(buildMethodText, fieldsSetViaAssignment);
        }
    }

    private void appendSetMethods(
            @NotNull StringBuilder buildMethodText, @NotNull Collection<@NotNull PsiField> fieldsToBeSetViaSetter) {
        for (PsiField psiFieldsForSetter : fieldsToBeSetViaSetter) {
            String fieldNamePrefix = CodeStyleSettings.FIELD_NAME_PREFIX;
            String fieldName = psiFieldsForSetter.getName();
            String fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix, "");
            String fieldNameUppercase = StringUtils.capitalize(fieldNameWithoutPrefix);
            buildMethodText
                    .append(srcClassFieldName)
                    .append(".set")
                    .append(fieldNameUppercase)
                    .append("(")
                    .append(fieldName)
                    .append(");");
        }
    }

    private void appendAssignments(
            @NotNull StringBuilder buildMethodText, @NotNull Collection<@NotNull PsiField> fieldsSetViaAssignment) {
        for (PsiField field : fieldsSetViaAssignment) {
            buildMethodText
                    .append(srcClassFieldName)
                    .append(".")
                    .append(field.getName())
                    .append("=")
                    .append("this.")
                    .append(field.getName())
                    .append(";");
        }
    }

    private @NotNull String createConstructorParameters() {
        if (bestConstructor == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (PsiParameter psiParameter : bestConstructor.getParameterList().getParameters()) {
            boolean parameterHasMatchingField = false;
            for (PsiField psiField : psiFieldsForConstructor) {
                if (PsiFieldVerifier.areNameAndTypeEqual(psiField, psiParameter)) {
                    sb.append(psiField.getName()).append(SEMICOLON);
                    parameterHasMatchingField = true;
                    break;
                }
            }
            if (!parameterHasMatchingField) {
                sb.append(getDefaultValue(psiParameter.getType())).append(SEMICOLON);
            }
        }
        removeLastSemicolon(sb);
        return sb.toString();
    }

    private static @NotNull String getDefaultValue(@NotNull PsiType type) {
        if (type.equals(PsiTypes.booleanType())) {
            return "false";
        } else if (type.equals(PsiTypes.byteType())
                || type.equals(PsiTypes.shortType())
                || type.equals(PsiTypes.intType())) {
            return "0";
        } else if (type.equals(PsiTypes.longType())) {
            return "0L";
        } else if (type.equals(PsiTypes.floatType())) {
            return "0.0f";
        } else if (type.equals(PsiTypes.doubleType())) {
            return "0.0d";
        } else if (type.equals(PsiTypes.charType())) {
            return "'\\u0000'";
        }
        return "null";
    }

    private static void removeLastSemicolon(@NotNull StringBuilder sb) {
        if (sb.toString().endsWith(SEMICOLON)) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
