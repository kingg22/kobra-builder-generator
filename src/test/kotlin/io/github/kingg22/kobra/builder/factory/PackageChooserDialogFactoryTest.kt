package io.github.kingg22.kobra.builder.factory

import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.project.Project
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PackageChooserDialogFactoryTest {
    @Mock
    private lateinit var packageChooserDialogFactory: MockedStatic<PackageChooserDialogFactory>

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var packageChooserDialog: PackageChooserDialog

    @Test
    fun shouldCreatePackageChooserDialogWithPassedTitle() {
        // given
        val title = "title"
        given(packageChooserDialog.title).willReturn(title)
        packageChooserDialogFactory.`when`<PackageChooserDialog> {
            PackageChooserDialogFactory.getPackageChooserDialog(title, project)
        }.thenReturn(packageChooserDialog)

        // when
        val result = PackageChooserDialogFactory.getPackageChooserDialog(title, project)

        assertThat(result).isEqualTo(packageChooserDialog)
        assertThat(result.title).isEqualTo(title)
    }
}
