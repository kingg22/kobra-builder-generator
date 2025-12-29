package io.github.kingg22.kobra.builder.writer

import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.psi.model.PsiFieldsForBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class BuilderWriterTest {

    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var existingBuilder: PsiClass

    @Mock
    private lateinit var srcClass: PsiClass

    @Mock
    private lateinit var psiFieldsForBuilder: PsiFieldsForBuilder

    @Test
    fun shouldExecuteCommandWithRunnable() {
        // given
        val commandProcessor: CommandProcessor = mock()
        psiHelper.`when`<CommandProcessor> { PsiHelper.commandProcessor }
            .thenReturn(commandProcessor)

        val context = BuilderContext(
            project = project,
            psiFieldsForBuilder = psiFieldsForBuilder,
            targetDirectory = null,
            className = "TestBuilder",
            psiClassFromEditor = srcClass,
            methodPrefix = "with",
            isInnerBuilder = false,
            hasButMethod = false,
            useSingleField = false,
            hasAddCopyConstructor = false,
        )

        // when
        BuilderWriter.writeBuilder(context, existingBuilder)

        // then
        val runnableCaptor = argumentCaptor<BuilderWriterRunnable>()

        verify(commandProcessor).executeCommand(
            eq(project),
            runnableCaptor.capture(),
            eq(BuilderWriter.CREATE_BUILDER_STRING),
            isNull(),
        )

        assertThat(runnableCaptor.lastValue.context)
            .isEqualTo(context)

        assertThat(runnableCaptor.lastValue.existingBuilder)
            .isEqualTo(existingBuilder)
    }
}
