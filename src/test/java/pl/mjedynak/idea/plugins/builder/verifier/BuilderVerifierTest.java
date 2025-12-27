package pl.mjedynak.idea.plugins.builder.verifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.intellij.psi.PsiClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BuilderVerifierTest {

    @Mock
    private PsiClass psiClass;

    @Test
    void shouldVerifyThatClassIsNotABuilderWhenItsDoesNotHaveBuilderSuffix() {
        // given
        given(psiClass.getName()).willReturn("AnyNameThatDoesn'tHaveBuilderAtTheEnd");

        // when
        boolean result = BuilderVerifier.isBuilder(psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldVerifyThatClassIsABuilderWhenItHasBuilderSuffix() {
        // given
        given(psiClass.getName()).willReturn("AnyNameThatEndsWithBuilder");

        // when
        boolean result = BuilderVerifier.isBuilder(psiClass);

        // then
        assertThat(result).isTrue();
    }
}
