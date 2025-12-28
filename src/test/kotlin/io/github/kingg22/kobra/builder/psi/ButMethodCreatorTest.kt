package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiParameterList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class ButMethodCreatorTest {

    @InjectMocks
    private lateinit var butMethodCreator: ButMethodCreator

    @Mock
    private lateinit var psiElementFactory: PsiElementFactory

    @Mock
    private lateinit var builderClass: PsiClass

    @Mock
    private lateinit var srcClass: PsiClass

    @Mock
    private lateinit var createdMethod: PsiMethod

    private lateinit var methodWithParam: PsiMethod

    private val srcClassFieldName = "className"

    @BeforeEach
    fun setUp() {
        val emptyParameterList = mock<PsiParameterList> {
            on { parametersCount } doReturn 0
        }

        val parameter = mock<PsiParameter> {
            on { name } doReturn "age"
        }

        val parameterListWithOneParam = mock<PsiParameterList> {
            on { parametersCount } doReturn 1
            on { parameters } doReturn arrayOf(parameter)
        }

        val method1 = mock<PsiMethod> {
            on { name } doReturn "Builder"
            on { parameterList } doReturn emptyParameterList
        }

        val method2 = mock<PsiMethod> {
            on { name } doReturn "aBuilder"
            on { parameterList } doReturn emptyParameterList
        }

        methodWithParam = mock {
            on { name } doReturn "withAge"
            on { parameterList } doReturn parameterListWithOneParam
        }

        given(builderClass.methods)
            .willReturn(arrayOf(method1, method2, methodWithParam))
    }

    @Test
    fun shouldCreateButMethod() {
        // given
        given(
            psiElementFactory.createMethodFromText(
                "public Builder but() { return aBuilder().withAge(age); }",
                srcClass,
            ),
        ).willReturn(createdMethod)

        // when
        val result = butMethodCreator
            .butMethod("Builder", builderClass, srcClass, srcClassFieldName, false)

        // then
        assertThat(result).isEqualTo(createdMethod)
    }

    @Test
    fun shouldCreateButMethodForSingleField() {
        // given
        given(
            psiElementFactory.createMethodFromText(
                "public Builder but() { return aBuilder().withAge(className.getAge()); }",
                srcClass,
            ),
        ).willReturn(createdMethod)

        // when
        val result = butMethodCreator
            .butMethod("Builder", builderClass, srcClass, srcClassFieldName, true)

        // then
        assertThat(result).isEqualTo(createdMethod)
    }
}
