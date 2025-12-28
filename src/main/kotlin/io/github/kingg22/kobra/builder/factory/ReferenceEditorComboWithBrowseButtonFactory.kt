package io.github.kingg22.kobra.builder.factory

import com.intellij.openapi.project.Project
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import org.jetbrains.annotations.Contract

public object ReferenceEditorComboWithBrowseButtonFactory {
    @Contract("_, _, _ -> new")
    @JvmStatic
    public fun getReferenceEditorComboWithBrowseButton(
        project: Project,
        packageName: String?,
        recentsKey: String,
    ): ReferenceEditorComboWithBrowseButton =
        ReferenceEditorComboWithBrowseButton(null, packageName, project, true, recentsKey)
}
