package io.github.kingg22.kobra.builder.verifier

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiModifierList
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiParameterList
import com.intellij.psi.PsiType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class PsiFieldVerifierTest {
    private lateinit var constructors: Array<PsiMethod>
    private lateinit var methods: Array<PsiMethod>
    private lateinit var parameters: Array<PsiParameter>

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField: PsiField

    @Mock
    private lateinit var psiClass: PsiClass

    @Mock
    private lateinit var constructor: PsiMethod

    @Mock
    private lateinit var parameterList: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameter: PsiParameter

    @Mock
    private lateinit var psiType: PsiType

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var method: PsiMethod

    @Mock
    private lateinit var modifierList: PsiModifierList

    private var name: String? = null

    @BeforeEach
    fun setUp() {
        constructors = arrayOf(constructor)
        methods = arrayOf(method)
        parameters = arrayOf(parameter)
        name = "name"
    }

    @Test
    fun shouldNotVerifyThatFieldIsSetInConstructorIfConstructorDoesNotExist() {
        // given
        given(psiClass.constructors).willReturn(emptyArray())

        // when
        val result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldNotVerifyThatFieldIsSetInConstructorIfConstructorHasDifferentParameterName() {
        // given
        prepareBehaviourForReturningParameter()
        given(parameter.type).willReturn(psiType)
        given(psiField.type).willReturn(psiType)
        given(parameter.name).willReturn(name)
        given(psiField.name).willReturn("differentName")

        // when
        val result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldNotVerifyThatFieldIsSetInConstructorIfConstructorHasDifferentParameterType() {
        // given
        val differentPsiType: PsiType = mock()
        prepareBehaviourForReturningParameter()
        given(parameter.type).willReturn(psiType)
        given(psiField.type).willReturn(differentPsiType)
        given(parameter.name).willReturn(name)
        given(psiField.name).willReturn(name)

        // when
        val result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldVerifyThatFieldIsSetInConstructorIfConstructorHasTheSameParameterTypeAndName() {
        // given
        prepareBehaviourForReturningParameter()
        given(parameter.type).willReturn(psiType)
        given(psiField.type).willReturn(psiType)
        given(parameter.name).willReturn(name)
        given(psiField.name).willReturn(name)

        // when
        val result = PsiFieldVerifier.isSetInConstructor(psiField, psiClass)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun shouldVerifyThatFieldIsSetInSetterMethodIfItIsNotPrivateAndHasCorrectParameter() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(method.name).willReturn("setField")

        // when
        val result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun shouldVerifyThatFieldIsNotSetInSetterMethodIfItIsPrivate() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(modifierList.hasExplicitModifier(PsiModifier.PRIVATE)).willReturn(true)
        given(method.name).willReturn("setField")

        // when
        val result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldVerifyThatFieldIsNotSetInSetterMethodIfItIsNotPrivateButHasIncorrectParameter() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(method.name).willReturn("setAnotherField")

        // when
        val result = PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldVerifyThatFieldHasGetterMethodAvailableIfTheMethodIsNotPrivateAndHasCorrectName() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(method.name).willReturn("getField")

        // when
        val result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun shouldVerifyThatFieldHasNoGetterMethodAvailableIfTheMethodIsPrivate() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(modifierList.hasExplicitModifier(PsiModifier.PRIVATE)).willReturn(true)
        given(method.name).willReturn("setField")

        // when
        val result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun shouldVerifyThatFieldHasNoGetterMethodAvailableIfTheMethodIsNotPrivateButHasIncorrectName() {
        // given
        given(psiClass.allMethods).willReturn(methods)
        given(method.modifierList).willReturn(modifierList)
        given(psiField.name).willReturn("field")
        given(method.name).willReturn("getAnotherField")

        // when
        val result = PsiFieldVerifier.hasGetterMethod(psiField, psiClass)

        // then
        assertThat(result).isFalse()
    }

    private fun prepareBehaviourForReturningParameter() {
        given(psiClass.constructors).willReturn(constructors)
        given(constructor.parameterList).willReturn(parameterList)
        given(parameterList.parameters).willReturn(parameters)
    }
}
