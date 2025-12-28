package io.github.kingg22.kobra.builder.psi

import io.github.kingg22.kobra.builder.psi.MethodNameCreator.createMethodName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MethodNameCreatorTest {
    @Test
    fun shouldCreateMethodIfPrefixIsEmpty() {
        // when
        val result = createMethodName("", "userName")

        // then
        assertThat(result).isEqualTo("userName")
    }

    @Test
    fun shouldCreateMethodWithCapitalizedFieldNameIfPrefixIsNotEmpty() {
        // when
        val result = createMethodName("with", "field")

        // then
        assertThat(result).isEqualTo("withField")
    }
}
