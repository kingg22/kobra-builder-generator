package pl.mjedynak.idea.plugins.builder.psi;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.settings.CodeStyleSettings;

public class MethodCreator {

    private final PsiElementFactory elementFactory;
    private final String builderClassName;

    public MethodCreator(PsiElementFactory elementFactory, String builderClassName) {
        this.elementFactory = elementFactory;
        this.builderClassName = builderClassName;
    }

    public PsiMethod createMethod(
            @NotNull PsiField psiField, String methodPrefix, String srcClassFieldName, boolean useSingleField) {
        String fieldName = psiField.getName();
        String fieldType = psiField.getType().getPresentableText();
        String fieldNamePrefix = CodeStyleSettings.FIELD_NAME_PREFIX;
        String fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix, "");
        String parameterNamePrefix = CodeStyleSettings.PARAMETER_NAME_PREFIX;
        String parameterName = parameterNamePrefix + fieldNameWithoutPrefix;
        String methodName = MethodNameCreator.createMethodName(methodPrefix, fieldNameWithoutPrefix);
        String methodText;
        if (useSingleField) {
            String setterName = MethodNameCreator.createMethodName("set", fieldNameWithoutPrefix);
            methodText = "public " + builderClassName + " " + methodName + "(" + fieldType + " " + parameterName
                    + ") { " + srcClassFieldName + "." + setterName + "(" + fieldName + "); return this; }";
        } else {
            methodText = "public " + builderClassName + " " + methodName + "(" + fieldType + " " + parameterName
                    + ") { this." + fieldName + " = " + parameterName + "; return this; }";
        }
        return elementFactory.createMethodFromText(methodText, psiField);
    }
}
