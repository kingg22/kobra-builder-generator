package io.github.kingg22.kobra.builder.factory

import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Contract

public object PackageChooserDialogFactory {
    @JvmStatic
    @Contract("_, _ -> new")
    public fun getPackageChooserDialog(message: String, project: Project): PackageChooserDialog =
        PackageChooserDialog(message, project)
}
