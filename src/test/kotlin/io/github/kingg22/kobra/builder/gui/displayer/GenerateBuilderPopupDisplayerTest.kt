package io.github.kingg22.kobra.builder.gui.displayer

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.PopupChooserBuilder
import io.github.kingg22.kobra.builder.factory.PopupChooserBuilderFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import javax.swing.JList

@ExtendWith(MockitoExtension::class)
class GenerateBuilderPopupDisplayerTest {
    @InjectMocks
    private lateinit var popupDisplayer: GenerateBuilderPopupDisplayer

    @Mock
    private lateinit var popupChooserBuilderFactory: MockedStatic<PopupChooserBuilderFactory>

    @Mock
    private lateinit var list: JList<*>

    @Mock
    private lateinit var popupChooserBuilder: PopupChooserBuilder<*>

    @Mock
    private lateinit var editor: Editor

    @Mock
    private lateinit var popup: JBPopup

    @Test
    fun shouldInvokePopupChooserBuilder() {
        // given
        popupChooserBuilderFactory
            .`when`<PopupChooserBuilder<*>> { PopupChooserBuilderFactory.getPopupChooserBuilder(list) }
            .thenReturn(popupChooserBuilder)
        given(popupChooserBuilder.setTitle(TITLE))
            .willReturn(popupChooserBuilder)
        given(popupChooserBuilder.setItemChosenCallback(any<Runnable>()))
            .willReturn(popupChooserBuilder)
        given(popupChooserBuilder.setMovable(true))
            .willReturn(popupChooserBuilder)
        given(popupChooserBuilder.createPopup()).willReturn(popup)

        // when
        popupDisplayer.displayPopupChooser(editor, list) {}

        verify(popup).showInBestPositionFor(editor)
    }

    companion object {
        private const val TITLE = "Builder already exists"
    }
}
