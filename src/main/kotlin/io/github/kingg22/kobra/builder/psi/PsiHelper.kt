package io.github.kingg22.kobra.builder.psi

import com.intellij.ide.util.EditSourceUtil
import com.intellij.ide.util.PackageUtil
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPackage
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.util.PsiUtilBase
import com.intellij.refactoring.util.RefactoringMessageUtil

public object PsiHelper {
    @JvmStatic
    public fun getPsiClassFromEditor(editor: Editor, project: Project): PsiClass? {
        var psiClass: PsiClass? = null
        val psiFile: PsiFile? = getPsiFile(editor, project)
        if (psiFile is PsiClassOwner) {
            val classes = psiFile.classes
            if (classes.size == 1) {
                psiClass = classes[0]
            }
        }
        return psiClass
    }

    @JvmStatic
    public fun getPsiFile(editor: Editor, project: Project): PsiFile? = PsiUtilBase.getPsiFileInEditor(editor, project)

    @JvmStatic
    public fun getPsiShortNamesCache(project: Project): PsiShortNamesCache = PsiShortNamesCache.getInstance(project)

    @JvmStatic
    public fun getDirectoryFromModuleAndPackageName(module: Module, packageName: String): PsiDirectory? {
        val baseDir = PackageUtil.findPossiblePackageDirectoryInModule(module, packageName)
        return PackageUtil.findOrCreateDirectoryForPackage(module, packageName, baseDir, true)
    }

    @JvmStatic
    public fun navigateToClass(psiClass: PsiClass?) {
        if (psiClass != null) {
            val navigatable = EditSourceUtil.getDescriptor(psiClass)
            navigatable?.navigate(true)
        }
    }

    @JvmStatic
    public fun checkIfClassCanBeCreated(targetDirectory: PsiDirectory, className: String): String? =
        RefactoringMessageUtil.checkCanCreateClass(targetDirectory, className)

    @JvmStatic
    public fun getPackage(psiDirectory: PsiDirectory): PsiPackage? = javaDirectoryService.getPackage(psiDirectory)

    @JvmStatic
    public fun getJavaPsiFacade(project: Project): JavaPsiFacade = JavaPsiFacade.getInstance(project)

    @JvmStatic
    public fun findModuleForPsiClass(psiClass: PsiClass, project: Project): Module? =
        ModuleUtil.findModuleForFile(psiClass.containingFile.virtualFile, project)

    @JvmStatic
    public val commandProcessor: CommandProcessor
        get() = CommandProcessor.getInstance()

    @JvmStatic
    public val application: Application
        get() = ApplicationManager.getApplication()

    @JvmStatic
    public val javaDirectoryService: JavaDirectoryService
        get() = JavaDirectoryService.getInstance()
}
