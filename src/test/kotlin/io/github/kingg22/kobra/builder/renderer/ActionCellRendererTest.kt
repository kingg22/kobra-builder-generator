package io.github.kingg22.kobra.builder.renderer

import com.intellij.codeInsight.navigation.GotoTargetHandler.AdditionalAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import javax.swing.Icon
import javax.swing.JList

@ExtendWith(MockitoExtension::class)
class ActionCellRendererTest {
    private lateinit var actionCellRenderer: ActionCellRenderer

    @Mock
    private lateinit var list: JList<*>

    @Mock
    private lateinit var action: AdditionalAction

    private var anyBooleanValue = false
    private var anyIndex = 0

    @BeforeEach
    fun setUp() {
        actionCellRenderer = ActionCellRenderer()
        anyBooleanValue = false
        anyIndex = 0
    }

    @Test
    fun shouldGetTextAndIconFromActionWhenRendering() {
        // given
        val icon: Icon = mock()
        val actionText = "actionText"
        given(action.text).willReturn(actionText)
        given(action.icon).willReturn(icon)

        // when
        val result = actionCellRenderer.getListCellRendererComponent(
            list,
            action,
            anyIndex,
            anyBooleanValue,
            anyBooleanValue,
        )

        // then
        assertThat(actionCellRenderer.text).isEqualTo(actionText)
        assertThat(actionCellRenderer.icon).isEqualTo(icon)
        assertThat(result).isNotNull()
        assertThat(result).isInstanceOf(ActionCellRenderer::class.java)
    }
}
