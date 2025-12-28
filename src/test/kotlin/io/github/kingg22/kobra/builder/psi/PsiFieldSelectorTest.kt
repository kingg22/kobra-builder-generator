package io.github.kingg22.kobra.builder.psi

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import io.github.kingg22.kobra.builder.factory.PsiElementClassMemberFactory
import io.github.kingg22.kobra.builder.factory.PsiElementClassMemberFactory.createPsiElementClassMember
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class PsiFieldSelectorTest {
    @Mock
    private lateinit var psiElementClassMemberFactory: MockedStatic<PsiElementClassMemberFactory>

    @Mock
    private lateinit var psiFieldVerifier: MockedStatic<PsiFieldVerifier>

    @Mock
    private lateinit var psiClass: PsiClass

    @Mock
    private lateinit var psiField: PsiField

    @BeforeEach
    fun setUp() {
        val fieldsArray = arrayOf(psiField)
        given(psiClass.allFields).willReturn(fieldsArray)
        psiElementClassMemberFactory
            .`when`<PsiElementClassMember<*>> { createPsiElementClassMember(any()) }
            .thenReturn(mock())
    }

    @Test
    fun shouldSelectFieldIfVerifierAcceptsItAsSetInSetter() {
        doTest(
            isSetInConstructor = false,
            isSetInSetter = true,
            hasGetter = false,
            isInnerBuilder = false,
            useSingleField = false,
            hasButMethod = false,
            size = 1,
        )
    }

    @Test
    fun shouldSelectFieldIfVerifierAcceptsItAsSetInConstructor() {
        doTest(
            isSetInConstructor = true,
            isSetInSetter = false,
            hasGetter = false,
            isInnerBuilder = false,
            useSingleField = false,
            hasButMethod = false,
            size = 1,
        )
    }

    @Test
    fun shouldNotSelectFieldIfVerifierDoesNotAcceptsItAsSetInConstructorOrInSetter() {
        doTest(
            isSetInConstructor = false,
            isSetInSetter = false,
            hasGetter = true,
            isInnerBuilder = false,
            useSingleField = false,
            hasButMethod = false,
            size = 0,
        )
    }

    @Test
    fun shouldSelectAllFieldsIfInnerBuilder() {
        doTest(
            isSetInConstructor = false,
            isSetInSetter = false,
            hasGetter = false,
            isInnerBuilder = true,
            useSingleField = false,
            hasButMethod = false,
            size = 1,
        )
    }

    @Test
    fun shouldNeverSelectSerialVersionUIDField() {
        given(psiField.name).willReturn("serialVersionUID")
        doTest(
            isSetInConstructor = true,
            isSetInSetter = true,
            hasGetter = true,
            isInnerBuilder = false,
            useSingleField = false,
            hasButMethod = false,
            size = 0,
        )
    }

    @Test
    fun shouldSelectFieldIfUseSingleFieldAndHasSetter() {
        doTest(
            isSetInConstructor = false,
            isSetInSetter = true,
            hasGetter = false,
            isInnerBuilder = false,
            useSingleField = true,
            hasButMethod = false,
            size = 1,
        )
    }

    @Test
    fun shouldNotSelectFieldIfUseSingleFieldAndHasNoSetter() {
        doTest(
            isSetInConstructor = true,
            isSetInSetter = false,
            hasGetter = true,
            isInnerBuilder = false,
            useSingleField = true,
            hasButMethod = false,
            size = 0,
        )
    }

    @Test
    fun shouldSelectFieldIfUseSingleFieldAndButMethodAndHasSetterAndGetter() {
        doTest(
            isSetInConstructor = false,
            isSetInSetter = true,
            hasGetter = true,
            isInnerBuilder = false,
            useSingleField = true,
            hasButMethod = true,
            size = 1,
        )
    }

    @Test
    fun shouldNotSelectFieldIfUseSingleFieldAndButMethodAndHasSetterAndNoGetter() {
        doTest(
            isSetInConstructor = true,
            isSetInSetter = true,
            hasGetter = false,
            isInnerBuilder = false,
            useSingleField = true,
            hasButMethod = true,
            size = 0,
        )
    }

    private fun doTest(
        isSetInConstructor: Boolean,
        isSetInSetter: Boolean,
        hasGetter: Boolean,
        isInnerBuilder: Boolean,
        useSingleField: Boolean,
        hasButMethod: Boolean,
        size: Int,
    ) {
        // given
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.isSetInConstructor(psiField, psiClass) }
            .thenReturn(isSetInConstructor)
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) }
            .thenReturn(isSetInSetter)
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.hasGetterMethod(psiField, psiClass) }
            .thenReturn(hasGetter)

        // when
        val result = PsiFieldSelector.selectFieldsToIncludeInBuilder(
            psiClass,
            isInnerBuilder,
            useSingleField,
            hasButMethod,
        )

        // then
        assertThat(result).hasSize(size)
    }
}
