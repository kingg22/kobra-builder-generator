package io.github.kingg22.kobra.builder.gui

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.project.Project
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import io.github.kingg22.kobra.builder.factory.PackageChooserDialogFactory
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

public class ChooserDisplayerActionListener(
    private val comboWithBrowseButton: ReferenceEditorComboWithBrowseButton,
    private val project: Project,
) : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        val chooser: PackageChooserDialog = PackageChooserDialogFactory.getPackageChooserDialog(
            CodeInsightBundle.message("dialog.create.class.package.chooser.title"),
            project,
        )
        chooser.selectPackage(comboWithBrowseButton.text)
        chooser.show()
        val aPackage = chooser.getSelectedPackage()
        if (aPackage != null) {
            comboWithBrowseButton.text = aPackage.qualifiedName
        }
    }
}
