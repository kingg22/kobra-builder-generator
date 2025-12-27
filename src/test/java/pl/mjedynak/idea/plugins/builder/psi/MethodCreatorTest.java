package pl.mjedynak.idea.plugins.builder.psi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MethodCreatorTest {

    private MethodCreator methodCreator;

    @Mock
    private PsiElementFactory elementFactory;

    @Mock
    private PsiField psiField;

    @Mock
    private PsiType type;

    @Mock
    private PsiMethod method;

    private final String srcClassFieldName = "className";

    @BeforeEach
    public void mockCodeStyleManager() {
        methodCreator = new MethodCreator(elementFactory, "BuilderClassName");
    }

    private void initOtherCommonMocks() {
        given(psiField.getName()).willReturn("name");
        given(type.getPresentableText()).willReturn("String");
        given(psiField.getType()).willReturn(type);
        // given(MethodNameCreator.createMethodName("with", "name")).willReturn("withName");
    }

    @Test
    void shouldCreateMethod() {
        // given
        initOtherCommonMocks();
        given(elementFactory.createMethodFromText(
                        "public BuilderClassName withName(String name) { this.name = name; return this; }", psiField))
                .willReturn(method);
        String methodPrefix = "with";

        // when
        PsiMethod result = methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, false);

        // then
        assertThat(result).isEqualTo(method);
    }

    @Test
    void shouldCreateMethodForSingleField() {
        // given
        initOtherCommonMocks();
        // given(MethodNameCreator.createMethodName("set", "name")).willReturn("setName");
        given(elementFactory.createMethodFromText(
                        "public BuilderClassName withName(String name) { className.setName(name); return this; }",
                        psiField))
                .willReturn(method);
        String methodPrefix = "with";

        // when
        PsiMethod result = methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, true);

        // then
        assertThat(result).isEqualTo(method);
    }
}
