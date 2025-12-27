package pl.mjedynak.idea.plugins.builder.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PackageChooserDialogFactoryTest {

    @Mock
    private MockedStatic<PackageChooserDialogFactory> packageChooserDialogFactory;

    @Mock
    private Project project;

    @Mock
    private PackageChooserDialog packageChooserDialog;

    @Test
    void shouldCreatePackageChooserDialogWithPassedTitle() {
        // given
        String title = "title";
        given(packageChooserDialog.getTitle()).willReturn(title);
        packageChooserDialogFactory
                .when(() -> PackageChooserDialogFactory.getPackageChooserDialog(title, project))
                .thenReturn(packageChooserDialog);

        // when
        PackageChooserDialog result = PackageChooserDialogFactory.getPackageChooserDialog(title, project);

        // then
        assertThat(result).isEqualTo(packageChooserDialog);
        assertThat(result.getTitle()).isEqualTo(title);
    }
}
