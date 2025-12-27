package pl.mjedynak.idea.plugins.builder.gui;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.factory.PackageChooserDialogFactory;

public class ChooserDisplayerActionListener implements ActionListener {

    private final @NotNull ReferenceEditorComboWithBrowseButton comboWithBrowseButton;
    private final @NotNull Project project;

    public ChooserDisplayerActionListener(
            @NotNull ReferenceEditorComboWithBrowseButton comboWithBrowseButton, @NotNull Project project) {
        this.comboWithBrowseButton = comboWithBrowseButton;
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PackageChooserDialog chooser = PackageChooserDialogFactory.getPackageChooserDialog(
                CodeInsightBundle.message("dialog.create.class.package.chooser.title"), project);
        chooser.selectPackage(comboWithBrowseButton.getText());
        chooser.show();
        PsiPackage aPackage = chooser.getSelectedPackage();
        if (aPackage != null) {
            comboWithBrowseButton.setText(aPackage.getQualifiedName());
        }
    }
}
