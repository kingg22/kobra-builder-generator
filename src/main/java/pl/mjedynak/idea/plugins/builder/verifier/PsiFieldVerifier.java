package pl.mjedynak.idea.plugins.builder.verifier;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import pl.mjedynak.idea.plugins.builder.settings.CodeStyleSettings;

public class PsiFieldVerifier {

    @VisibleForTesting
    static final String SET_PREFIX = "set";

    @VisibleForTesting
    static final String GET_PREFIX = "get";

    private PsiFieldVerifier() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isSetInConstructor(@NotNull PsiField psiField, @NotNull PsiClass psiClass) {
        boolean result = false;
        PsiMethod[] constructors = psiClass.getConstructors();
        for (int i = 0; i < constructors.length && !result; i++) {
            result = checkConstructor(psiField, constructors[i]);
        }
        return result;
    }

    public static boolean checkConstructor(@NotNull PsiField psiField, @NotNull PsiMethod constructor) {
        PsiParameterList parameterList = constructor.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        return iterateOverParameters(psiField, parameters);
    }

    private static boolean iterateOverParameters(
            @NotNull PsiField psiField, @NotNull PsiParameter @NotNull [] parameters) {
        boolean result = false;
        for (int i = 0; i < parameters.length && !result; i++) {
            result = areNameAndTypeEqual(psiField, parameters[i]);
        }
        return result;
    }

    public static boolean areNameAndTypeEqual(@NotNull PsiField psiField, @NotNull PsiParameter parameter) {
        String parameterNamePrefix = CodeStyleSettings.PARAMETER_NAME_PREFIX;
        String parameterName = parameter.getName();
        String parameterNameWithoutPrefix = parameterName.replace(parameterNamePrefix, "");
        String fieldNamePrefix = CodeStyleSettings.FIELD_NAME_PREFIX;
        String fieldName = psiField.getName();
        String fieldNameWithoutPrefix = fieldName.replaceFirst(fieldNamePrefix, "");
        return parameterNameWithoutPrefix.equals(fieldNameWithoutPrefix)
                && parameter.getType().equals(psiField.getType());
    }

    public static boolean isSetInSetterMethod(@NotNull PsiField psiField, @NotNull PsiClass psiClass) {
        return methodIsNotPrivateAndHasProperPrefixAndProperName(psiField, psiClass, SET_PREFIX);
    }

    public static boolean hasGetterMethod(@NotNull PsiField psiField, @NotNull PsiClass psiClass) {
        return methodIsNotPrivateAndHasProperPrefixAndProperName(psiField, psiClass, GET_PREFIX);
    }

    private static boolean methodIsNotPrivateAndHasProperPrefixAndProperName(
            @NotNull PsiField psiField, @NotNull PsiClass psiClass, @NotNull String prefix) {
        boolean result = false;
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (methodIsNotPrivate(method) && methodHaProperPrefixAndProperName(psiField, method, prefix)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean methodIsNotPrivate(@NotNull PsiMethod method) {
        PsiModifierList modifierList = method.getModifierList();
        return modifierListHasNoPrivateModifier(modifierList);
    }

    private static boolean methodHaProperPrefixAndProperName(
            @NotNull PsiField psiField, @NotNull PsiMethod method, @NotNull String prefix) {
        String fieldNamePrefix = CodeStyleSettings.FIELD_NAME_PREFIX;
        String fieldNameWithoutPrefix = psiField.getName().replace(fieldNamePrefix, EMPTY);
        return method.getName().equals(prefix + WordUtils.capitalize(fieldNameWithoutPrefix));
    }

    private static boolean modifierListHasNoPrivateModifier(@NotNull PsiModifierList modifierList) {
        return !modifierList.hasExplicitModifier(PsiModifier.PRIVATE);
    }
}
