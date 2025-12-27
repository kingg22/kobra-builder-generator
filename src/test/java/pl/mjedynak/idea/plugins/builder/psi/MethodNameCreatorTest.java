package pl.mjedynak.idea.plugins.builder.psi;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MethodNameCreatorTest {

    @Test
    void shouldCreateMethodIfPrefixIsEmpty() {
        // when
        String result = MethodNameCreator.createMethodName(EMPTY, "userName");

        // then
        assertThat(result).isEqualTo("userName");
    }

    @Test
    void shouldCreateMethodWithCapitalizedFieldNameIfPrefixIsNotEmpty() {
        // when
        String result = MethodNameCreator.createMethodName("with", "field");

        // then
        assertThat(result).isEqualTo("withField");
    }
}
