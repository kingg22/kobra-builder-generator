package io.github.kingg22.kobra.builder.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

public class BuilderGeneratorSettingsComponent {
    private val defaultMethodPrefixText = JBTextField()
    private val innerBuilderCheckBox = JBCheckBox("Inner builder")
    private val butMethodCheckBox = JBCheckBox("'but' method'")
    private val useSinglePrefixCheckBox = JBCheckBox("Use single prefix")
    private val addCopyConstructorCheckBox = JBCheckBox("Add copy constructor")
    public val panel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(JBLabel("Default prefix: "), defaultMethodPrefixText, 1, false)
        .addComponent(innerBuilderCheckBox, 1)
        .addComponent(butMethodCheckBox, 1)
        .addComponent(useSinglePrefixCheckBox, 1)
        .addComponent(addCopyConstructorCheckBox, 1)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    public val preferredFocusedComponent: JComponent get() = defaultMethodPrefixText

    public var defaultMethodPrefix: String
        get() = defaultMethodPrefixText.text
        set(defaultMethodPrefix) {
            defaultMethodPrefixText.setText(defaultMethodPrefix)
        }

    public var isInnerBuilder: Boolean
        get() = innerBuilderCheckBox.isSelected
        set(isInnerBuilder) {
            innerBuilderCheckBox.setSelected(isInnerBuilder)
        }

    public var isButMethod: Boolean
        get() = butMethodCheckBox.isSelected
        set(isButMethod) {
            butMethodCheckBox.setSelected(isButMethod)
        }

    public var isUseSinglePrefix: Boolean
        get() = useSinglePrefixCheckBox.isSelected
        set(isUseSinglePrefix) {
            useSinglePrefixCheckBox.setSelected(isUseSinglePrefix)
        }

    public var isAddCopyConstructor: Boolean
        get() = addCopyConstructorCheckBox.isSelected
        set(addCopyConstructor) {
            addCopyConstructorCheckBox.setSelected(addCopyConstructor)
        }
}
