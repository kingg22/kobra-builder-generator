package io.github.kingg22.kobra.builder.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

public class BuilderGeneratorSettingsConfigurable : Configurable {
    private var mySettingsComponent: BuilderGeneratorSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String = "Builder Generator Default Settings"

    override fun getPreferredFocusedComponent(): JComponent = mySettingsComponent!!.preferredFocusedComponent

    override fun createComponent(): JComponent {
        mySettingsComponent = BuilderGeneratorSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: BuilderGeneratorSettingsState = BuilderGeneratorSettingsState.instance
        var modified = mySettingsComponent!!.defaultMethodPrefix != settings.defaultMethodPrefix
        modified = modified or (mySettingsComponent!!.isInnerBuilder != settings.isInnerBuilder)
        modified = modified or (mySettingsComponent!!.isButMethod != settings.isButMethod)
        modified = modified or (mySettingsComponent!!.isUseSinglePrefix != settings.isUseSinglePrefix)
        modified = modified or (mySettingsComponent!!.isAddCopyConstructor != settings.isAddCopyConstructor)
        return modified
    }

    override fun apply() {
        val settings: BuilderGeneratorSettingsState = BuilderGeneratorSettingsState.instance
        settings.defaultMethodPrefix = mySettingsComponent!!.defaultMethodPrefix
        settings.isInnerBuilder = mySettingsComponent!!.isInnerBuilder
        settings.isButMethod = mySettingsComponent!!.isButMethod
        settings.isUseSinglePrefix = mySettingsComponent!!.isUseSinglePrefix
        settings.isAddCopyConstructor = mySettingsComponent!!.isAddCopyConstructor
    }

    override fun reset() {
        val settings: BuilderGeneratorSettingsState = BuilderGeneratorSettingsState.instance
        mySettingsComponent!!.defaultMethodPrefix = settings.defaultMethodPrefix
        mySettingsComponent!!.isInnerBuilder = settings.isInnerBuilder
        mySettingsComponent!!.isButMethod = settings.isButMethod
        mySettingsComponent!!.isUseSinglePrefix = settings.isUseSinglePrefix
        mySettingsComponent!!.isAddCopyConstructor = settings.isAddCopyConstructor
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
