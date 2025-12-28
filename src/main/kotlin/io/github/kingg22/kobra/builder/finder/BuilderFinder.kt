package io.github.kingg22.kobra.builder.finder

import com.intellij.psi.PsiClass
import org.jetbrains.annotations.VisibleForTesting

public object BuilderFinder {
    @VisibleForTesting
    internal const val SEARCH_PATTERN: String = "Builder"

    public const val EMPTY_STRING: String = ""

    @JvmStatic
    public fun findBuilderForClass(psiClass: PsiClass): PsiClass? {
        val innerBuilderClass: PsiClass? = tryFindInnerBuilder(psiClass)
        if (innerBuilderClass != null) {
            return innerBuilderClass
        } else {
            val className = psiClass.name ?: return null
            return findClass(psiClass, className + SEARCH_PATTERN)
        }
    }

    @JvmStatic
    public fun findClassForBuilder(psiClass: PsiClass): PsiClass? {
        val className = psiClass.name ?: return null
        val searchName = className.replaceFirst(SEARCH_PATTERN.toRegex(), EMPTY_STRING)
        return findClass(psiClass, searchName)
    }

    private fun tryFindInnerBuilder(psiClass: PsiClass): PsiClass? {
        var innerBuilderClass: PsiClass? = null
        val allInnerClasses = psiClass.allInnerClasses
        for (innerClass in allInnerClasses) {
            val innerClassName = innerClass.name ?: continue
            if (innerClassName.contains(SEARCH_PATTERN)) {
                innerBuilderClass = innerClass
                break
            }
        }
        return innerBuilderClass
    }

    private fun findClass(psiClass: PsiClass, searchName: String): PsiClass? = if (typeIsCorrect(psiClass)) {
        ClassFinder.findClass(searchName, psiClass.project)
    } else {
        null
    }

    private fun typeIsCorrect(psiClass: PsiClass): Boolean =
        !psiClass.isAnnotationType && !psiClass.isEnum && !psiClass.isInterface
}
