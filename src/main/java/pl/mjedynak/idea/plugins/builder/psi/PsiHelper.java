package pl.mjedynak.idea.plugins.builder.psi;

import static com.intellij.ide.util.EditSourceUtil.getDescriptor;

import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.refactoring.util.RefactoringMessageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiHelper {

    private PsiHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static @Nullable PsiClass getPsiClassFromEditor(@NotNull Editor editor, @NotNull Project project) {
        PsiClass psiClass = null;
        PsiFile psiFile = getPsiFile(editor, project);
        if (psiFile instanceof PsiClassOwner psiClassOwner) {
            PsiClass[] classes = psiClassOwner.getClasses();
            if (classes.length == 1) {
                psiClass = classes[0];
            }
        }
        return psiClass;
    }

    public static @Nullable PsiFile getPsiFile(@NotNull Editor editor, @NotNull Project project) {
        return PsiUtilBase.getPsiFileInEditor(editor, project);
    }

    public static PsiShortNamesCache getPsiShortNamesCache(@NotNull Project project) {
        return PsiShortNamesCache.getInstance(project);
    }

    public static @Nullable PsiDirectory getDirectoryFromModuleAndPackageName(
            @NotNull Module module, @NotNull String packageName) {
        PsiDirectory baseDir = PackageUtil.findPossiblePackageDirectoryInModule(module, packageName);
        return PackageUtil.findOrCreateDirectoryForPackage(module, packageName, baseDir, true);
    }

    public static void navigateToClass(@Nullable PsiClass psiClass) {
        if (psiClass != null) {
            Navigatable navigatable = getDescriptor(psiClass);
            if (navigatable != null) {
                navigatable.navigate(true);
            }
        }
    }

    public static @Nullable String checkIfClassCanBeCreated(
            @NotNull PsiDirectory targetDirectory, @NotNull String className) {
        return RefactoringMessageUtil.checkCanCreateClass(targetDirectory, className);
    }

    public static JavaDirectoryService getJavaDirectoryService() {
        return JavaDirectoryService.getInstance();
    }

    public static @Nullable PsiPackage getPackage(@NotNull PsiDirectory psiDirectory) {
        return getJavaDirectoryService().getPackage(psiDirectory);
    }

    public static JavaPsiFacade getJavaPsiFacade(@NotNull Project project) {
        return JavaPsiFacade.getInstance(project);
    }

    public static CommandProcessor getCommandProcessor() {
        return CommandProcessor.getInstance();
    }

    public static @NotNull Application getApplication() {
        return ApplicationManager.getApplication();
    }

    public static @Nullable Module findModuleForPsiClass(@NotNull PsiClass psiClass, @NotNull Project project) {
        return ModuleUtil.findModuleForFile(psiClass.getContainingFile().getVirtualFile(), project);
    }
}
