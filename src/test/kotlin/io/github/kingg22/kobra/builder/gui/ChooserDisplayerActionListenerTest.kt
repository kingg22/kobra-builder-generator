package io.github.kingg22.kobra.builder.gui

import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiPackage
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import io.github.kingg22.kobra.builder.factory.PackageChooserDialogFactory
import io.github.kingg22.kobra.builder.factory.PackageChooserDialogFactory.getPackageChooserDialog
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.awt.event.ActionEvent

@ExtendWith(MockitoExtension::class)
class ChooserDisplayerActionListenerTest {
    @InjectMocks
    private lateinit var chooserDisplayerActionListener: ChooserDisplayerActionListener

    @Mock
    private lateinit var comboWithBrowseButton: ReferenceEditorComboWithBrowseButton

    @Mock
    private lateinit var packageChooserDialogFactory: MockedStatic<PackageChooserDialogFactory>

    @Mock
    private lateinit var project: Project

    @Test
    fun shouldShowChooserAndSetText() {
        // given
        val anyEvent: ActionEvent = mock()
        val chooser: PackageChooserDialog = mock()
        val psiPackage: PsiPackage = mock()
        val text = "text"
        val name = "name"

        packageChooserDialogFactory
            .`when`<PackageChooserDialog> {
                getPackageChooserDialog(anyString(), eq(project))
            }.thenReturn(chooser)
        given(comboWithBrowseButton.text).willReturn(text)
        given(chooser.getSelectedPackage()).willReturn(psiPackage)
        given(psiPackage.qualifiedName).willReturn(name)

        // when
        chooserDisplayerActionListener.actionPerformed(anyEvent)

        verify(chooser).selectPackage(text)
        verify(chooser).show()
        verify(comboWithBrowseButton).text = name
    }
}
