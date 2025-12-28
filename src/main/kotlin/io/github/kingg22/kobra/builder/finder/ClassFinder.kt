package io.github.kingg22.kobra.builder.finder

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import io.github.kingg22.kobra.builder.psi.PsiHelper

public object ClassFinder {
    @JvmStatic
    public fun findClass(pattern: String, project: Project): PsiClass? {
        val projectScope = GlobalSearchScope.projectScope(project)
        val psiShortNamesCache = PsiHelper.getPsiShortNamesCache(project)
        val classesArray = psiShortNamesCache.getClassesByName(pattern, projectScope)
        return getPsiClass(classesArray)
    }

    private fun getPsiClass(classesArray: Array<PsiClass>): PsiClass? = if (classesArray.isNotEmpty()) {
        classesArray[0]
    } else {
        null
    }
}
