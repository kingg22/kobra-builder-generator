package io.github.kingg22.kobra.builder.gui

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import io.github.kingg22.kobra.builder.psi.PsiHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class SelectDirectoryTest {
    private lateinit var selectDirectory: SelectDirectory

    @Mock
    private lateinit var createBuilderDialog: CreateBuilderDialog

    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var module: Module

    @Mock
    private lateinit var targetDirectory: PsiDirectory

    @Mock
    private lateinit var existingBuilder: PsiClass

    @BeforeEach
    fun setUp() {
        psiHelper
            .`when`<PsiDirectory?> {
                PsiHelper.getDirectoryFromModuleAndPackageName(module, PACKAGE_NAME)
            }
            .thenReturn(targetDirectory)
    }

    @Test
    fun shouldDoNothingIfTargetDirectoryReturnedByPsiHelperIsNull() {
        // given
        selectDirectory = SelectDirectory(createBuilderDialog, module, PACKAGE_NAME, CLASS_NAME, null)
        psiHelper
            .`when`<PsiDirectory?> {
                PsiHelper.getDirectoryFromModuleAndPackageName(
                    module,
                    PACKAGE_NAME,
                )
            }
            .thenReturn(null)

        // when
        selectDirectory.run()

        // then
        Mockito.verifyNoInteractions(createBuilderDialog)
    }

    @Test
    fun shouldSetTargetDirectoryOnCaller() {
        // given
        selectDirectory = SelectDirectory(createBuilderDialog, module, PACKAGE_NAME, CLASS_NAME, null)
        psiHelper
            .`when`<String?> {
                PsiHelper.checkIfClassCanBeCreated(
                    targetDirectory,
                    CLASS_NAME,
                )
            }
            .thenReturn(null)

        // when
        selectDirectory.run()

        verify(createBuilderDialog).targetDirectory = targetDirectory
    }

    @Test
    fun shouldThrowExceptionWhenPsiHelperCheckReturnsErrorString() {
        val exception: Throwable = assertThrows(
            IncorrectOperationException::class.java,
        ) {
            // given
            selectDirectory = SelectDirectory(createBuilderDialog, module, PACKAGE_NAME, CLASS_NAME, null)
            psiHelper
                .`when`<String?> {
                    PsiHelper.checkIfClassCanBeCreated(
                        targetDirectory,
                        CLASS_NAME,
                    )
                }
                .thenReturn(ERROR_MESSAGE)

            // when
            selectDirectory.run()
        }
        // then
        assertThat(exception).hasMessageContaining(ERROR_MESSAGE)
    }

    @Test
    fun shouldThrowExceptionWhenPsiHelperThrowsIncorrectOperationException() {
        // given
        selectDirectory = SelectDirectory(createBuilderDialog, module, PACKAGE_NAME, CLASS_NAME, null)
        val exception = IncorrectOperationException(ERROR_MESSAGE)
        psiHelper
            .`when`<String?> {
                PsiHelper.checkIfClassCanBeCreated(
                    targetDirectory,
                    CLASS_NAME,
                )
            }
            .thenThrow(exception)

        // when
        val expectedException: Throwable = assertThrows(
            IncorrectOperationException::class.java,
        ) { selectDirectory.run() }

        // then
        assertThat(expectedException).hasMessageContaining(ERROR_MESSAGE)
    }

    @Test
    fun shouldNotCheckIfClassCanBeCreatedIfExistingBuilderMustBeDeletedAndClassToCreateIsTheSame() {
        // given
        selectDirectory = SelectDirectory(createBuilderDialog, module, PACKAGE_NAME, CLASS_NAME, existingBuilder)
        mockIsClassToCreateSameAsBuilderToDelete()

        // when
        selectDirectory.run()

        psiHelper.verify({ PsiHelper.checkIfClassCanBeCreated(any(), anyString()) }, never())
    }

    private fun mockIsClassToCreateSameAsBuilderToDelete() {
        val containingFile: PsiFile = mock()
        val containingDirectory: PsiDirectory = mock()
        given(existingBuilder.containingFile).willReturn(containingFile)
        given(containingFile.containingDirectory)
            .willReturn(containingDirectory)
        given(containingDirectory.name).willReturn(DIRECTORY_NAME)
        given(existingBuilder.name).willReturn(CLASS_NAME)
        given(targetDirectory.name).willReturn(DIRECTORY_NAME)
    }

    companion object {
        private const val PACKAGE_NAME = "packageName"
        private const val CLASS_NAME = "className"
        private const val ERROR_MESSAGE = "errorMessage"
        private const val DIRECTORY_NAME = "directoryName"
    }
}
