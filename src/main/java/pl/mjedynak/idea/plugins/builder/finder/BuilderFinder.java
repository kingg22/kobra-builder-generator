package pl.mjedynak.idea.plugins.builder.finder;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class BuilderFinder {

    @VisibleForTesting
    static final String SEARCH_PATTERN = "Builder";

    public static final String EMPTY_STRING = "";

    private BuilderFinder() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Nullable
    public static PsiClass findBuilderForClass(@NotNull PsiClass psiClass) {
        PsiClass innerBuilderClass = tryFindInnerBuilder(psiClass);
        if (innerBuilderClass != null) {
            return innerBuilderClass;
        } else {
            String className = psiClass.getName();
            if (className == null) return null;
            return findClass(psiClass, className + SEARCH_PATTERN);
        }
    }

    @Nullable
    private static PsiClass tryFindInnerBuilder(@NotNull PsiClass psiClass) {
        PsiClass innerBuilderClass = null;
        PsiClass[] allInnerClasses = psiClass.getAllInnerClasses();
        for (PsiClass innerClass : allInnerClasses) {
            String innerClassName = innerClass.getName();
            if (innerClassName == null) continue;
            if (innerClassName.contains(SEARCH_PATTERN)) {
                innerBuilderClass = innerClass;
                break;
            }
        }
        return innerBuilderClass;
    }

    @Nullable
    public static PsiClass findClassForBuilder(@NotNull PsiClass psiClass) {
        String className = psiClass.getName();
        if (className == null) return null;
        String searchName = className.replaceFirst(SEARCH_PATTERN, EMPTY_STRING);
        return findClass(psiClass, searchName);
    }

    @Nullable
    private static PsiClass findClass(@NotNull PsiClass psiClass, @NotNull String searchName) {
        PsiClass result = null;
        if (typeIsCorrect(psiClass)) {
            result = ClassFinder.findClass(searchName, psiClass.getProject());
        }
        return result;
    }

    private static boolean typeIsCorrect(@NotNull PsiClass psiClass) {
        return !psiClass.isAnnotationType() && !psiClass.isEnum() && !psiClass.isInterface();
    }
}
