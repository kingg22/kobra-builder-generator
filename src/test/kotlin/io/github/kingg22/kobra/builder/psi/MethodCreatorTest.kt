package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MethodCreatorTest {
    private lateinit var methodCreator: MethodCreator

    @Mock
    private lateinit var elementFactory: PsiElementFactory

    @Mock
    private lateinit var psiField: PsiField

    @Mock
    private lateinit var type: PsiType

    @Mock
    private lateinit var method: PsiMethod

    private val srcClassFieldName = "className"

    @BeforeEach
    fun mockCodeStyleManager() {
        methodCreator = MethodCreator(elementFactory, "BuilderClassName")
    }

    private fun initOtherCommonMocks() {
        given(psiField.name).willReturn("name")
        given(type.presentableText).willReturn("String")
        given(psiField.type).willReturn(type)
    }

    @Test
    fun shouldCreateMethod() {
        // given
        initOtherCommonMocks()
        given(
            elementFactory.createMethodFromText(
                "public BuilderClassName withName(String name) { this.name = name; return this; }",
                psiField,
            ),
        ).willReturn(method)
        val methodPrefix = "with"

        // when
        val result = methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, false)

        assertThat(result).isEqualTo(method)
    }

    @Test
    fun shouldCreateMethodForSingleField() {
        // given
        initOtherCommonMocks()
        given(
            elementFactory.createMethodFromText(
                "public BuilderClassName withName(String name) { className.setName(name); return this; }",
                psiField,
            ),
        ).willReturn(method)
        val methodPrefix = "with"

        // when
        val result = methodCreator.createMethod(psiField, methodPrefix, srcClassFieldName, true)

        assertThat(result).isEqualTo(method)
    }
}
