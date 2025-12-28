package io.github.kingg22.kobra.builder.action

import com.intellij.openapi.util.IconLoader.getIcon
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegenerateBuilderAdditionalActionTest {
    private lateinit var action: RegenerateBuilderAdditionalAction

    @BeforeEach
    fun setUp() {
        action = RegenerateBuilderAdditionalAction()
    }

    @Test
    fun shouldGetItsOwnText() {
        // when
        val result = action.getText()

        // then
        assertThat(result).isEqualTo(TEXT)
    }

    @Test
    fun shouldGetItsOwnIcon() {
        // when
        val result = action.getIcon()

        assertThat(result).isEqualTo(ICON)
    }

    @Test
    fun shouldDoNothingWhenInvokingExecute() {
        // this test has no assertion. Is it really useful, if so it needs rework.
        // when
        action.execute()
    }

    companion object {
        private const val TEXT = "Regenerate builder..."
        private val ICON = getIcon("/actions/intentionBulb.png", RegenerateBuilderAdditionalAction::class.java)
    }
}
