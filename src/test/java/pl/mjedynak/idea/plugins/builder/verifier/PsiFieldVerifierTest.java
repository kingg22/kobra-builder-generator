package pl.mjedynak.idea.plugins.builder.verifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mock.Strictness.LENIENT;
import static org.mockito.Mockito.mock;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PsiFieldVerifierTest {

    private PsiMethod[] constructors;
    private PsiMethod[] methods;
    private PsiParameter[] parameters;

    @Mock(strictness = LENIENT)
    private PsiField psiField;

    @Mock
    private PsiClass psiClass;

    @Mock
    private PsiMethod constructor;

    @Mock
    private PsiParameterList parameterList;

    @Mock(strictness = LENIENT)
    private PsiParameter parameter;

    @Mock
    private PsiType psiType;

    @Mock(strictness = LENIENT)
    private PsiMethod method;

    @Mock
    private PsiModifierList modifierList;

    private String name;

    @BeforeEach
    public void setUp() {
        constructors = new PsiMethod[1];
        constructors[0] = constructor;
        methods = new PsiMethod[1];
        methods[0] = method;
        parameters = new PsiParameter[1];
        parameters[0] = parameter;
        name = "name";
    }

    @Test
    void shouldNotVerifyThatFieldIsSetInConstructorIfConstructorDoesNotExist() {
        // given
        given(psiClass.getConstructors()).willReturn(new PsiMethod[0]);

        // when
        boolean result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotVerifyThatFieldIsSetInConstructorIfConstructorHasDifferentParameterName() {
        // given
        prepareBehaviourForReturningParameter();
        given(parameter.getType()).willReturn(psiType);
        given(psiField.getType()).willReturn(psiType);
        given(parameter.getName()).willReturn(name);
        given(psiField.getName()).willReturn("differentName");

        // when
        boolean result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotVerifyThatFieldIsSetInConstructorIfConstructorHasDifferentParameterType() {
        // given
        PsiType differentPsiType = mock(PsiType.class);
        prepareBehaviourForReturningParameter();
        given(parameter.getType()).willReturn(psiType);
        given(psiField.getType()).willReturn(differentPsiType);
        given(parameter.getName()).willReturn(name);
        given(psiField.getName()).willReturn(name);

        // when
        boolean result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldVerifyThatFieldIsSetInConstructorIfConstructorHasTheSameParameterTypeAndName() {
        // given
        prepareBehaviourForReturningParameter();
        given(parameter.getType()).willReturn(psiType);
        given(psiField.getType()).willReturn(psiType);
        given(parameter.getName()).willReturn(name);
        given(psiField.getName()).willReturn(name);

        // when
        boolean result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldVerifyThatFieldIsSetInSetterMethodIfItIsNotPrivateAndHasCorrectParameter() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(method.getName()).willReturn("setField");

        // when
        boolean result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldVerifyThatFieldIsNotSetInSetterMethodIfItIsPrivate() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(modifierList.hasExplicitModifier(PsiModifier.PRIVATE)).willReturn(true);
        given(method.getName()).willReturn("setField");
        // when
        boolean result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldVerifyThatFieldIsNotSetInSetterMethodIfItIsNotPrivateButHasIncorrectParameter() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(method.getName()).willReturn("setAnotherField");
        // when
        boolean result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldVerifyThatFieldHasGetterMethodAvailableIfTheMethodIsNotPrivateAndHasCorrectName() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(method.getName()).willReturn("getField");

        // when
        boolean result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldVerifyThatFieldHasNoGetterMethodAvailableIfTheMethodIsPrivate() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(modifierList.hasExplicitModifier(PsiModifier.PRIVATE)).willReturn(true);
        given(method.getName()).willReturn("setField");
        // when
        boolean result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldVerifyThatFieldHasNoGetterMethodAvailableIfTheMethodIsNotPrivateButHasIncorrectName() {
        // given
        given(psiClass.getAllMethods()).willReturn(methods);
        given(method.getModifierList()).willReturn(modifierList);
        given(psiField.getName()).willReturn("field");
        given(method.getName()).willReturn("getAnotherField");
        // when
        boolean result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass);

        // then
        assertThat(result).isFalse();
    }

    private void prepareBehaviourForReturningParameter() {
        given(psiClass.getConstructors()).willReturn(constructors);
        given(constructor.getParameterList()).willReturn(parameterList);
        given(parameterList.getParameters()).willReturn(parameters);
    }
}
