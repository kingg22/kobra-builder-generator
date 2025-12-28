package io.github.kingg22.kobra.builder.verifier

import com.intellij.psi.PsiClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BuilderVerifierTest {
    @Mock
    private lateinit var psiClass: PsiClass

    @Test
    fun shouldVerifyThatClassIsNotABuilderWhenItsDoesNotHaveBuilderSuffix() {
        // given
        given(psiClass.name).willReturn("AnyNameThatDoesn'tHaveBuilderAtTheEnd")

        // when
        val result = BuilderVerifier.isBuilder(psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldVerifyThatClassIsABuilderWhenItHasBuilderSuffix() {
        given(psiClass.name).willReturn("AnyNameThatEndsWithBuilder")

        // when
        val result = BuilderVerifier.isBuilder(psiClass)

        // then
        assertThat(result).isTrue()
    }
}
