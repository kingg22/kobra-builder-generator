package io.github.kingg22.kobra.builder.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("SdkSettingsPlugin.xml")])
public class BuilderGeneratorSettingsState : PersistentStateComponent<BuilderGeneratorSettingsState?> {
    public var defaultMethodPrefix: String = "with"
    public var isInnerBuilder: Boolean = false
    public var isButMethod: Boolean = false
    public var isUseSinglePrefix: Boolean = false
    public var isAddCopyConstructor: Boolean = false

    override fun getState(): BuilderGeneratorSettingsState = this

    override fun loadState(state: BuilderGeneratorSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    public companion object {
        @JvmStatic
        public val instance: BuilderGeneratorSettingsState
            get() {
                return try {
                    ApplicationManager.getApplication().getService(BuilderGeneratorSettingsState::class.java)
                } catch (_: NullPointerException) {
                    BuilderGeneratorSettingsState()
                }
            }
    }
}
