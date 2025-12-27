package pl.mjedynak.idea.plugins.builder.action.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import javax.swing.JList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.idea.plugins.builder.factory.GoToBuilderPopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.gui.displayer.GoToBuilderPopupDisplayer;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

@ExtendWith(MockitoExtension.class)
public class GoToBuilderActionHandlerTest {

    @InjectMocks
    private GoToBuilderActionHandler builderActionHandler;

    @Mock
    private MockedStatic<BuilderVerifier> builderVerifier;

    @Mock
    private MockedStatic<BuilderFinder> builderFinder;

    @Mock
    private MockedStatic<PsiHelper> psiHelper;

    @Mock
    private GoToBuilderPopupListFactory popupListFactory;

    @Mock
    private GoToBuilderPopupDisplayer popupDisplayer;

    @Mock
    private DisplayChoosers displayChoosers;

    @Mock
    private PsiClass psiClass;

    @Mock
    private PsiClass builderClass;

    @Mock
    private Editor editor;

    @Mock
    private Project project;

    @Mock
    private DataContext dataContext;

    @SuppressWarnings("rawtypes")
    @Mock
    private JList list;

    @BeforeEach
    public void setUp() {
        given(dataContext.getData(CommonDataKeys.PROJECT)).willReturn(project);
    }

    @Test
    void shouldNavigateToBuilderIfItExistsAndInvokedInsideNotBuilderClass() {
        // given
        psiHelper.when(() -> PsiHelper.getPsiClassFromEditor(editor, project)).thenReturn(psiClass);
        builderVerifier.when(() -> BuilderVerifier.isBuilder(psiClass)).thenReturn(false);
        builderFinder.when(() -> BuilderFinder.findBuilderForClass(psiClass)).thenReturn(builderClass);

        // when
        builderActionHandler.doExecute(editor, null, dataContext);

        // then
        psiHelper.verify(() -> PsiHelper.navigateToClass(builderClass));
    }

    @Test
    void shouldNavigateToNotBuilderClassIfItExistsAndInvokedInsideBuilder() {
        // given
        psiHelper.when(() -> PsiHelper.getPsiClassFromEditor(editor, project)).thenReturn(builderClass);
        builderVerifier.when(() -> BuilderVerifier.isBuilder(builderClass)).thenReturn(true);
        builderFinder
                .when(() -> BuilderFinder.findClassForBuilder(builderClass))
                .thenReturn(psiClass);

        // when
        builderActionHandler.doExecute(editor, null, dataContext);

        // then
        psiHelper.verify(() -> PsiHelper.navigateToClass(psiClass));
    }

    @Test
    void shouldDisplayPopupWhenBuilderNotFoundAndInvokedInsideNotBuilderClass() {
        // given
        psiHelper.when(() -> PsiHelper.getPsiClassFromEditor(editor, project)).thenReturn(psiClass);
        builderVerifier.when(() -> BuilderVerifier.isBuilder(psiClass)).thenReturn(false);
        builderFinder.when(() -> BuilderFinder.findBuilderForClass(psiClass)).thenReturn(null);
        given(popupListFactory.getPopupList()).willReturn(list);

        // when
        builderActionHandler.doExecute(editor, null, dataContext);

        // then
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(popupDisplayer).displayPopupChooser(eq(editor), eq(list), runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue().run();
        verify(displayChoosers).run(null);
    }

    @Test
    void shouldNotDoAnythingWhenNotBuilderClassNotFoundAndInvokedInsideBuilder() {
        // given
        psiHelper.when(() -> PsiHelper.getPsiClassFromEditor(editor, project)).thenReturn(builderClass);
        builderVerifier.when(() -> BuilderVerifier.isBuilder(builderClass)).thenReturn(true);
        builderFinder
                .when(() -> BuilderFinder.findClassForBuilder(builderClass))
                .thenReturn(null);

        // when
        builderActionHandler.doExecute(editor, null, dataContext);

        // then
        verifyNothingIsDone(psiHelper);
    }

    private void verifyNothingIsDone(MockedStatic<PsiHelper> psiHelper) {
        psiHelper.verify(() -> PsiHelper.navigateToClass(any(PsiClass.class)), never());
        verify(displayChoosers, never()).run(any(PsiClass.class));
        verifyNoMoreInteractions(popupDisplayer);
    }
}
