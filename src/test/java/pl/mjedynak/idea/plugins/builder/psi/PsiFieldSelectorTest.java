package pl.mjedynak.idea.plugins.builder.psi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.idea.plugins.builder.factory.PsiElementClassMemberFactory;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

@ExtendWith(MockitoExtension.class)
public class PsiFieldSelectorTest {

    @Mock
    private MockedStatic<PsiElementClassMemberFactory> psiElementClassMemberFactory;

    @Mock
    private MockedStatic<PsiFieldVerifier> psiFieldVerifier;

    @Mock
    private PsiClass psiClass;

    @Mock
    private PsiField psiField;

    @BeforeEach
    public void setUp() {
        PsiField[] fieldsArray = new PsiField[1];
        fieldsArray[0] = psiField;
        given(psiClass.getAllFields()).willReturn(fieldsArray);
        psiElementClassMemberFactory
                .when(() -> PsiElementClassMemberFactory.createPsiElementClassMember(any(PsiField.class)))
                .thenReturn(mock(PsiElementClassMember.class));
    }

    @Test
    void shouldSelectFieldIfVerifierAcceptsItAsSetInSetter() {
        doTest(false, true, false, false, false, false, 1);
    }

    @Test
    void shouldSelectFieldIfVerifierAcceptsItAsSetInConstructor() {
        doTest(true, false, false, false, false, false, 1);
    }

    @Test
    void shouldNotSelectFieldIfVerifierDoesNotAcceptsItAsSetInConstructorOrInSetter() {
        doTest(false, false, true, false, false, false, 0);
    }

    @Test
    void shouldSelectAllFieldsIfInnerBuilder() {
        doTest(false, false, false, true, false, false, 1);
    }

    @Test
    void shouldNeverSelectSerialVersionUIDField() {
        given(psiField.getName()).willReturn("serialVersionUID");
        doTest(true, true, true, false, false, false, 0);
    }

    @Test
    void shouldSelectFieldIfUseSingleFieldAndHasSetter() {
        doTest(false, true, false, false, true, false, 1);
    }

    @Test
    void shouldNotSelectFieldIfUseSingleFieldAndHasNoSetter() {
        doTest(true, false, true, false, true, false, 0);
    }

    @Test
    void shouldSelectFieldIfUseSingleFieldAndButMethodAndHasSetterAndGetter() {
        doTest(false, true, true, false, true, true, 1);
    }

    @Test
    void shouldNotSelectFieldIfUseSingleFieldAndButMethodAndHasSetterAndNoGetter() {
        doTest(true, true, false, false, true, true, 0);
    }

    private void doTest(
            boolean isSetInConstructor,
            boolean isSetInSetter,
            boolean hasGetter,
            boolean isInnerBuilder,
            boolean useSingleField,
            boolean hasButMethod,
            int size) {
        // given
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInConstructor(psiField, psiClass))
                .thenReturn(isSetInConstructor);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass))
                .thenReturn(isSetInSetter);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.hasGetterMethod(psiField, psiClass))
                .thenReturn(hasGetter);

        // when
        List<PsiElementClassMember<?>> result =
                PsiFieldSelector.selectFieldsToIncludeInBuilder(psiClass, isInnerBuilder, useSingleField, hasButMethod);

        // then
        assertThat(result).hasSize(size);
    }
}
