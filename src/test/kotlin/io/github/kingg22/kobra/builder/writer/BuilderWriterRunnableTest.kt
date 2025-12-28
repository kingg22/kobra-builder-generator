package io.github.kingg22.kobra.builder.writer

import com.intellij.openapi.application.Application
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class BuilderWriterRunnableTest {
    @InjectMocks
    private lateinit var builderWriterRunnable: BuilderWriterRunnable

    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var context: BuilderContext

    @Mock
    private lateinit var existingBuilder: PsiClass

    @Test
    fun shouldRunWriteActionWithBuilderWriterComputable() {
        // given
        val application: Application = mock()
        psiHelper.`when`<Application> { PsiHelper.application }.thenReturn(application)

        // when
        builderWriterRunnable.run()

        // then
        val builderWriterComputableArgumentCaptor = argumentCaptor<BuilderWriterComputable>()
        verify(application).runWriteAction(builderWriterComputableArgumentCaptor.capture())
        assertThat(builderWriterComputableArgumentCaptor.lastValue.context).isEqualTo(context)
        assertThat(builderWriterComputableArgumentCaptor.lastValue.existingBuilder).isEqualTo(existingBuilder)
    }
}
