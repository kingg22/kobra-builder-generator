package pl.mjedynak.idea.plugins.builder.gui.displayer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import javax.swing.JList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.idea.plugins.builder.factory.PopupChooserBuilderFactory;

@ExtendWith(MockitoExtension.class)
public class GenerateBuilderPopupDisplayerTest {

    private static final String TITLE = "Builder already exists";

    @InjectMocks
    private GenerateBuilderPopupDisplayer popupDisplayer;

    @Mock
    private MockedStatic<PopupChooserBuilderFactory> popupChooserBuilderFactory;

    @Mock
    private JList<?> list;

    @SuppressWarnings("rawtypes")
    @Mock
    private PopupChooserBuilder popupChooserBuilder;

    @Mock
    private Editor editor;

    @Mock
    private JBPopup popup;

    @Test
    void shouldInvokePopupChooserBuilder() {
        // given
        popupChooserBuilderFactory
                .when(() -> PopupChooserBuilderFactory.getPopupChooserBuilder(list))
                .thenReturn(popupChooserBuilder);
        given(popupChooserBuilder.setTitle(TITLE)).willReturn(popupChooserBuilder);
        given(popupChooserBuilder.setItemChosenCallback(any(Runnable.class))).willReturn(popupChooserBuilder);
        given(popupChooserBuilder.setMovable(true)).willReturn(popupChooserBuilder);
        given(popupChooserBuilder.createPopup()).willReturn(popup);

        // when
        popupDisplayer.displayPopupChooser(editor, list, () -> {});

        // then
        verify(popup).showInBestPositionFor(editor);
    }
}
