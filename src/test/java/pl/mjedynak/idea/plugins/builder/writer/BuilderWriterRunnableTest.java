package pl.mjedynak.idea.plugins.builder.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

@ExtendWith(MockitoExtension.class)
public class BuilderWriterRunnableTest {

    private BuilderWriterRunnable builderWriterRunnable;

    @Mock
    private MockedStatic<PsiHelper> psiHelper;

    @Mock
    private BuilderContext context;

    @Mock
    private PsiClass existingBuilder;

    @BeforeEach
    public void setUp() {
        builderWriterRunnable = new BuilderWriterRunnable(context, existingBuilder);
    }

    @Test
    void shouldRunWriteActionWithBuilderWriterComputable() {
        // given
        Application application = mock(Application.class);
        psiHelper.when(PsiHelper::getApplication).thenReturn(application);

        // when
        builderWriterRunnable.run();

        // then
        ArgumentCaptor<BuilderWriterComputable> builderWriterComputableArgumentCaptor =
                ArgumentCaptor.forClass(BuilderWriterComputable.class);
        verify(application).runWriteAction(builderWriterComputableArgumentCaptor.capture());
        assertThat(builderWriterComputableArgumentCaptor.getValue().context()).isEqualTo(context);
        assertThat(builderWriterComputableArgumentCaptor.getValue().existingBuilder())
                .isEqualTo(existingBuilder);
    }
}
