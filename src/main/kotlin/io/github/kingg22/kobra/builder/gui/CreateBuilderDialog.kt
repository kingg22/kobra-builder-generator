package io.github.kingg22.kobra.builder.gui

import com.intellij.CommonBundle
import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiNameHelper
import com.intellij.psi.PsiPackage
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.RecentsManager.getInstance
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ui.JBUI
import io.github.kingg22.kobra.builder.factory.ReferenceEditorComboWithBrowseButtonFactory
import io.github.kingg22.kobra.builder.gui.helper.GuiHelper
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.settings.BuilderGeneratorSettingsState
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Action
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

public class CreateBuilderDialog(
    private val project: Project,
    title: String?,
    private val sourceClass: PsiClass,
    targetClassName: String?,
    targetPackage: PsiPackage?,
    private val existingBuilder: PsiClass?,
) : DialogWrapper(project, true) {
    private val targetClassNameField: JTextField = JTextField(targetClassName)
    private val targetMethodPrefix: JTextField = JTextField(defaultStates.defaultMethodPrefix)
    private val targetPackageField: ReferenceEditorComboWithBrowseButton
    public lateinit var targetDirectory: PsiDirectory
    private lateinit var innerBuilder: JCheckBox
    private lateinit var butMethod: JCheckBox
    private lateinit var useSingleField: JCheckBox
    private lateinit var copyConstructor: JCheckBox

    init {
        fun setPreferredSize(field: JTextField) {
            val size = field.getPreferredSize()
            val fontMetrics = field.getFontMetrics(field.getFont())
            size.width = fontMetrics.charWidth('a') * WIDTH
            field.preferredSize = size
        }
        setPreferredSize(targetClassNameField)
        setPreferredSize(targetMethodPrefix)

        val targetPackageName = targetPackage?.qualifiedName ?: ""
        targetPackageField = ReferenceEditorComboWithBrowseButtonFactory.getReferenceEditorComboWithBrowseButton(
            project,
            targetPackageName,
            RECENTS_KEY,
        )
        targetPackageField.addActionListener(ChooserDisplayerActionListener(targetPackageField, project))
        setTitle(title)
    }

    override fun show() {
        super.init()
        super.show()
    }

    override fun createActions(): Array<out Action> = arrayOf(super.okAction, cancelAction)

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbConstraints = GridBagConstraints()

        panel.setBorder(IdeBorderFactory.createBorder())

        // Class name
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.weightx = 0.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("Class name"), gbConstraints)

        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(targetClassNameField, gbConstraints)
        targetClassNameField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onClassNameChanged()
            }
        })

        // Class name

        // Method prefix
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.gridy = 2
        gbConstraints.weightx = 0.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("Method prefix"), gbConstraints)

        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(targetMethodPrefix, gbConstraints)

        // Method prefix

        // Destination package
        gbConstraints.gridx = 0
        gbConstraints.gridy = 3
        gbConstraints.weightx = 0.0
        gbConstraints.gridwidth = 1
        panel.add(
            JLabel(CodeInsightBundle.message("dialog.create.class.destination.package.label")),
            gbConstraints,
        )

        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0

        addInnerPanelForDestinationPackageField(panel, gbConstraints)

        // Destination package

        // Inner builder
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.weightx = 0.0
        gbConstraints.gridy = 4
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("Inner builder"), gbConstraints)

        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST

        innerBuilder = JCheckBox()
        innerBuilder.setSelected(defaultStates.isInnerBuilder)
        innerBuilder.addActionListener { _ ->
            targetPackageField.setEnabled(!innerBuilder.isSelected)
        }
        panel.add(innerBuilder, gbConstraints)

        // Inner builder

        // but method
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.weightx = 0.0
        gbConstraints.gridy = 5
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("'but' method"), gbConstraints)

        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        butMethod = JCheckBox()
        butMethod.setSelected(defaultStates.isButMethod)
        panel.add(butMethod, gbConstraints)

        // but method

        // useSingleField
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.weightx = 0.0
        gbConstraints.gridy = 6
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("Use single field"), gbConstraints)

        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        useSingleField = JCheckBox()
        useSingleField.setSelected(defaultStates.isUseSinglePrefix)
        panel.add(useSingleField, gbConstraints)

        // useSingleField

        // copy constructor
        gbConstraints.insets = JBUI.insets(4, 8)
        gbConstraints.gridx = 0
        gbConstraints.weightx = 0.0
        gbConstraints.gridy = 7
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(JLabel("Add copy constructor"), gbConstraints)

        gbConstraints.gridx = 1
        gbConstraints.weightx = 1.0
        gbConstraints.gridwidth = 1
        gbConstraints.fill = GridBagConstraints.HORIZONTAL
        gbConstraints.anchor = GridBagConstraints.WEST
        copyConstructor = JCheckBox()
        copyConstructor.setSelected(defaultStates.isAddCopyConstructor)
        panel.add(copyConstructor, gbConstraints)

        // copy constructor
        return panel
    }

    override fun doOKAction() {
        getInstance(project).registerRecentEntry(RECENTS_KEY, targetPackageField.text)
        val module = PsiHelper.findModuleForPsiClass(sourceClass, project)
        checkNotNull(module) { "Cannot find module for class " + sourceClass.name }
        try {
            checkIfSourceClassHasZeroArgsConstructorWhenUsingSingleField()
            checkIfClassCanBeCreated(module)
            super.doOKAction()
        } catch (e: IncorrectOperationException) {
            GuiHelper.showMessageDialog(
                project,
                e.message,
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon(),
            )
        }
    }

    override fun getPreferredFocusedComponent(): JComponent = targetClassNameField

    public val className: String? get() = targetClassNameField.getText()
    public val methodPrefix: String? get() = targetMethodPrefix.getText()
    public val isInnerBuilder: Boolean get() = innerBuilder.isSelected

    public val hasButMethod: Boolean get() = butMethod.isSelected

    public val isSingleField: Boolean get() = useSingleField.isSelected

    public val hasAddCopyConstructor: Boolean get() = copyConstructor.isSelected

    private fun executeCommand(selectDirectory: SelectDirectory) {
        CommandProcessor.getInstance()
            .executeCommand(project, selectDirectory, CodeInsightBundle.message("create.directory.command"), null)
    }

    private val packageName: String get() = targetPackageField.text?.trim { it <= ' ' } ?: ""

    private fun addInnerPanelForDestinationPackageField(panel: JPanel, gbConstraints: GridBagConstraints?) {
        val innerPanel = createInnerPanelForDestinationPackageField()
        panel.add(innerPanel, gbConstraints)
    }

    private fun createInnerPanelForDestinationPackageField(): JPanel {
        val innerPanel = JPanel(BorderLayout())
        innerPanel.add(targetPackageField, BorderLayout.CENTER)
        return innerPanel
    }

    private fun checkIfClassCanBeCreated(module: Module) {
        if (!isInnerBuilder) {
            executeCommand(SelectDirectory(this, module, this.packageName, this.className!!, existingBuilder))
        }
    }

    private fun checkIfSourceClassHasZeroArgsConstructorWhenUsingSingleField() {
        if (isSingleField) {
            val constructors = sourceClass.constructors
            if (constructors.size == 0) {
                return
            }
            for (constructor in constructors) {
                if (constructor.parameterList.parametersCount == 0) {
                    return
                }
            }
            throw IncorrectOperationException("%s must define a default constructor".format(sourceClass.name))
        }
    }

    private fun onClassNameChanged() {
        okAction.isEnabled = PsiNameHelper.getInstance(project).isIdentifier(className)
    }

    public companion object {
        private const val RECENTS_KEY = "CreateBuilderDialog.RecentsKey"
        private const val WIDTH = 40
        private val defaultStates: BuilderGeneratorSettingsState get() = BuilderGeneratorSettingsState.instance
    }
}
