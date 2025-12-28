package io.github.kingg22.kobra.builder.action.handler

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import io.github.kingg22.kobra.builder.action.GoToBuilderAdditionalAction
import io.github.kingg22.kobra.builder.action.RegenerateBuilderAdditionalAction
import io.github.kingg22.kobra.builder.factory.GenerateBuilderPopupListFactory
import io.github.kingg22.kobra.builder.finder.BuilderFinder
import io.github.kingg22.kobra.builder.gui.displayer.GenerateBuilderPopupDisplayer
import io.github.kingg22.kobra.builder.psi.PsiHelper
import io.github.kingg22.kobra.builder.psi.PsiHelper.navigateToClass
import io.github.kingg22.kobra.builder.verifier.BuilderVerifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verifyNoMoreInteractions
import javax.swing.JList

@ExtendWith(MockitoExtension::class)
class GenerateBuilderActionHandlerTest {
    @InjectMocks
    private lateinit var builderActionHandler: GenerateBuilderActionHandler

    @Mock
    private lateinit var builderVerifier: MockedStatic<BuilderVerifier>

    @Mock
    private lateinit var builderFinder: MockedStatic<BuilderFinder>

    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var popupListFactory: GenerateBuilderPopupListFactory

    @Mock
    private lateinit var popupDisplayer: GenerateBuilderPopupDisplayer

    @Mock
    private lateinit var displayChoosers: DisplayChoosers

    @Mock
    private lateinit var psiClass: PsiClass

    @Mock
    private lateinit var builderClass: PsiClass

    @Mock
    private lateinit var editor: Editor

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var dataContext: DataContext

    @Mock
    private lateinit var list: JList<*>

    @BeforeEach
    fun setUp() {
        given(dataContext.getData(CommonDataKeys.PROJECT)).willReturn(project)
    }

    @Test
    fun shouldDisplayPopupWhenBuilderIsFoundAndInvokedInsideNotBuilderClass() {
        // given
        psiHelper.`when`<PsiClass> { PsiHelper.getPsiClassFromEditor(editor, project) }.thenReturn(psiClass)
        builderVerifier.`when`<Boolean> { BuilderVerifier.isBuilder(psiClass) }.thenReturn(false)
        builderFinder.`when`<PsiClass> { BuilderFinder.findBuilderForClass(psiClass) }.thenReturn(builderClass)
        given(popupListFactory.popupList).willReturn(list)

        // when
        builderActionHandler.doExecute(editor, null, dataContext)

        // then
        val runnableArgumentCaptor = argumentCaptor<Runnable>()
        verify(popupDisplayer).displayPopupChooser(eq(editor), eq(list), runnableArgumentCaptor.capture())

        testRunnableWhenGoToBuilderIsSelected(runnableArgumentCaptor)
        testRunnableWhenRegenerateBuilderIsSelected(runnableArgumentCaptor)
    }

    private fun testRunnableWhenGoToBuilderIsSelected(runnableArgumentCaptor: KArgumentCaptor<Runnable>) {
        // given
        given(list.getSelectedValue()).willReturn(GoToBuilderAdditionalAction())

        // when
        runnableArgumentCaptor.lastValue.run()

        // then
        navigateToClass(builderClass)
    }

    private fun testRunnableWhenRegenerateBuilderIsSelected(runnableArgumentCaptor: KArgumentCaptor<Runnable>) {
        // given
        given(list.getSelectedValue()).willReturn(RegenerateBuilderAdditionalAction())

        // when
        runnableArgumentCaptor.lastValue.run()

        verify(displayChoosers).run(builderClass)
    }

    @Test
    fun shouldDirectlyCallDisplayChoosersWhenBuilderNotFoundAndInvokedInsideNotBuilderClass() {
        // given
        psiHelper.`when`<PsiClass> { PsiHelper.getPsiClassFromEditor(editor, project) }.thenReturn(psiClass)
        builderVerifier.`when`<Boolean> { BuilderVerifier.isBuilder((psiClass)) }.thenReturn(false)
        builderFinder.`when`<PsiClass?> { BuilderFinder.findBuilderForClass(psiClass) }.thenReturn(null)

        // when
        builderActionHandler.doExecute(editor, null, dataContext)

        verify(displayChoosers).run(null)
    }

    @Test
    fun shouldNotDoAnythingWhenNotBuilderClassFoundAndInvokedInsideBuilder() {
        // given
        psiHelper.`when`<PsiClass> { PsiHelper.getPsiClassFromEditor(editor, project) }.thenReturn(builderClass)
        builderVerifier.`when`<Boolean> { BuilderVerifier.isBuilder(builderClass) }.thenReturn(true)
        builderFinder
            .`when`<PsiClass> { BuilderFinder.findClassForBuilder(builderClass) }
            .thenReturn(psiClass)

        // when
        builderActionHandler.doExecute(editor, null, dataContext)

        // then
        verifyNothingIsDone(psiHelper)
    }

    @Test
    fun shouldNotDoAnythingWhenNotBuilderClassNotFoundAndInvokedInsideBuilder() {
        // given
        psiHelper.`when`<PsiClass> { PsiHelper.getPsiClassFromEditor(editor, project) }
            .thenReturn(builderClass)
        builderVerifier
            .`when`<Boolean> { BuilderVerifier.isBuilder(builderClass) }
            .thenReturn(true)
        builderFinder
            .`when`<PsiClass?> { BuilderFinder.findClassForBuilder(builderClass) }
            .thenReturn(null)

        // when
        builderActionHandler.doExecute(editor, null, dataContext)

        // then
        verifyNothingIsDone(psiHelper)
    }

    private fun verifyNothingIsDone(psiHelperMockedStatic: MockedStatic<PsiHelper>) {
        psiHelperMockedStatic.verify({ navigateToClass(any()) }, never())

        verify(displayChoosers, never()).run(any())

        verifyNoMoreInteractions(popupDisplayer)
    }
}
