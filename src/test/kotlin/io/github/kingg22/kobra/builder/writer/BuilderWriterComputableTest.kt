package io.github.kingg22.kobra.builder.writer

import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import io.github.kingg22.kobra.builder.gui.helper.GuiHelper
import io.github.kingg22.kobra.builder.psi.BuilderPsiClassBuilder
import io.github.kingg22.kobra.builder.psi.BuilderPsiClassBuilderFactory
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.psi.model.PsiFieldsForBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class BuilderWriterComputableTest {

    private lateinit var builderWriterComputable: BuilderWriterComputable
    private lateinit var context: BuilderContext

    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var guiHelper: MockedStatic<GuiHelper>

    @Mock
    private lateinit var builderFactory: BuilderPsiClassBuilderFactory

    @Mock
    private lateinit var builderPsiClassBuilder: BuilderPsiClassBuilder

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var srcClass: PsiClass

    @Mock
    private lateinit var builderClass: PsiClass

    @Mock
    private lateinit var psiFile: PsiFile

    @Mock
    private lateinit var psiFieldsForBuilder: PsiFieldsForBuilder

    @BeforeEach
    fun setUp() {
        context = BuilderContext(
            project = project,
            psiFieldsForBuilder = psiFieldsForBuilder,
            targetDirectory = null,
            className = "TestBuilder",
            psiClassFromEditor = srcClass,
            methodPrefix = METHOD_PREFIX,
            isInnerBuilder = false,
            hasButMethod = false,
            useSingleField = false,
            hasAddCopyConstructor = false,
        )

        builderWriterComputable = BuilderWriterComputable(context, builderClass, builderFactory)
    }

    @Test
    fun shouldIncludeCurrentPlaceAsChangePlaceAndNavigateToCreatedBuilder() {
        // given
        given(builderFactory.aBuilder(context)).willReturn(builderPsiClassBuilder)

        mockBuilder(false)

        // when
        val result = builderWriterComputable.compute()

        // then
        guiHelper.verify { GuiHelper.includeCurrentPlaceAsChangePlace(project) }
        guiHelper.verify { GuiHelper.positionCursor(project, psiFile, builderClass) }

        assertThat(result)
            .isNotNull
            .isInstanceOf(PsiClass::class.java)
            .isEqualTo(builderClass)
    }

    @Test
    fun shouldIncludeCurrentPlaceAsChangePlaceAndCreateInnerBuilder() {
        // given
        val innerContext = context.copy(isInnerBuilder = true)

        builderWriterComputable = BuilderWriterComputable(innerContext, builderClass, builderFactory)

        given(builderFactory.anInnerBuilder(innerContext)).willReturn(builderPsiClassBuilder)

        mockBuilder()

        // when
        val result = builderWriterComputable.compute()

        // then
        guiHelper.verify { GuiHelper.includeCurrentPlaceAsChangePlace(project) }

        assertThat(result)
            .isNotNull
            .isInstanceOf(PsiClass::class.java)
            .isEqualTo(builderClass)
    }

    @Test
    fun shouldInvokeBuilderWriterErrorRunnableWhenExceptionOccurs() {
        // given
        given(builderFactory.aBuilder(context)).willThrow(IncorrectOperationException::class.java)

        val application: Application = mock()
        psiHelper.`when`<Application> { PsiHelper.application }.thenReturn(application)

        // when
        builderWriterComputable.compute()

        // then
        verify(application)
            .invokeLater(isA(BuilderWriterErrorRunnable::class.java))
    }

    private fun mockBuilder(isInnerClass: Boolean = true) {
        given(builderPsiClassBuilder.withFields())
            .willReturn(builderPsiClassBuilder)
        given(builderPsiClassBuilder.withConstructor())
            .willReturn(builderPsiClassBuilder)
        given(builderPsiClassBuilder.withInitializingMethod())
            .willReturn(builderPsiClassBuilder)
        given(builderPsiClassBuilder.withSetMethods(METHOD_PREFIX))
            .willReturn(builderPsiClassBuilder)
        given(builderPsiClassBuilder.build())
            .willReturn(builderClass)
        if (!isInnerClass) {
            given(builderClass.containingFile).willReturn(psiFile)
        }
    }

    companion object {
        private const val METHOD_PREFIX = "with"
    }
}
