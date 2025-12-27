package pl.mjedynak.idea.plugins.builder.finder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class ClassFinder {

    private ClassFinder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static @Nullable PsiClass findClass(@NotNull String pattern, @NotNull Project project) {
        GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
        PsiShortNamesCache psiShortNamesCache = PsiHelper.getPsiShortNamesCache(project);
        PsiClass[] classesArray = psiShortNamesCache.getClassesByName(pattern, projectScope);
        return getPsiClass(classesArray);
    }

    private static @Nullable PsiClass getPsiClass(@NotNull PsiClass @NotNull [] classesArray) {
        return (classesArray.length != 0) ? classesArray[0] : null;
    }
}
