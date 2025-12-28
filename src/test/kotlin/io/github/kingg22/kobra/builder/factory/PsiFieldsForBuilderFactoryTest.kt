package io.github.kingg22.kobra.builder.factory

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import io.github.kingg22.kobra.builder.psi.BestConstructorSelector
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class PsiFieldsForBuilderFactoryTest {
    @Mock
    private lateinit var psiFieldVerifier: MockedStatic<PsiFieldVerifier>

    @Mock
    private lateinit var psiClass: PsiClass

    @Mock
    private lateinit var psiElementClassMember: PsiElementClassMember<*>

    @Mock
    private lateinit var psiElementClassMemberInSetterOnly: PsiElementClassMember<*>

    @Mock
    private lateinit var psiElementClassMemberInConstructorOnly: PsiElementClassMember<*>

    @Mock
    private lateinit var psiElementClassMemberInSetterAndConstructor: PsiElementClassMember<*>

    @Mock
    private lateinit var psiElementClassMemberNowhere: PsiElementClassMember<*>

    @Mock
    private lateinit var psiField: PsiField

    @Mock
    private lateinit var psiFieldInSetterOnly: PsiField

    @Mock
    private lateinit var psiFieldInConstructorOnly: PsiField

    @Mock
    private lateinit var psiFieldInSetterAndConstructor: PsiField

    @Mock
    private lateinit var psiFieldNowhere: PsiField

    @Mock
    private lateinit var bestConstructorSelector: MockedStatic<BestConstructorSelector>

    @Mock
    private lateinit var bestConstructor: PsiMethod

    private lateinit var argumentCaptor: KArgumentCaptor<List<PsiField>>

    private lateinit var psiElementClassMembers: List<PsiElementClassMember<*>>

    @BeforeEach
    fun setUp() {
        argumentCaptor = argumentCaptor()
    }

    private fun initCommonMock() {
        psiElementClassMembers = listOf(psiElementClassMember)
        given(psiElementClassMember.getPsiElement()).willReturn(psiField)
        given(psiField.name).willReturn(PSI_FIELD_NAME)
    }

    @Test
    fun shouldCreateObjectWithPsiFieldsForSetters() {
        // given
        initCommonMock()
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) }
            .thenReturn(true)
        bestConstructorSelector
            .`when`<PsiMethod> { BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)) }
            .thenReturn(bestConstructor)
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.checkConstructor(psiField, bestConstructor) }
            .thenReturn(false)

        // when
        val result = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass)

        assertThat(result).isNotNull()
        assertThat(result.fieldsForConstructor).isNotNull().hasSize(0)
        assertThat(result.fieldsForSetters).isNotNull().hasSize(1).containsOnly(psiField)

        psiFieldVerifier.verify { PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) }
        bestConstructorSelector.verify {
            BestConstructorSelector.getBestConstructor(
                argumentCaptor.capture(),
                eq(psiClass),
            )
        }

        assertThat(argumentCaptor.lastValue).isNotNull().hasSize(0)
        psiFieldVerifier.verify { PsiFieldVerifier.checkConstructor(psiField, bestConstructor) }
    }

    @Test
    fun shouldCreateObjectWithPsiFieldsForConstructor() {
        // given
        initCommonMock()
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) }
            .thenReturn(false)
        bestConstructorSelector
            .`when`<PsiMethod> { BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)) }
            .thenReturn(bestConstructor)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiField,
                    bestConstructor,
                )
            }
            .thenReturn(true)

        // when
        val result = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass)

        assertThat(result).isNotNull()
        assertThat(result.fieldsForSetters).isNotNull().hasSize(0)
        assertThat(result.fieldsForConstructor).isNotNull().hasSize(1).containsOnly(psiField)

        psiFieldVerifier.verify {
            PsiFieldVerifier.isSetInSetterMethod(
                psiField,
                psiClass,
            )
        }
        bestConstructorSelector.verify {
            BestConstructorSelector.getBestConstructor(
                argumentCaptor.capture(),
                eq(psiClass),
            )
        }

        assertThat(argumentCaptor.lastValue)
            .isNotNull()
            .hasSize(1)
            .extracting("name")
            .containsOnly(PSI_FIELD_NAME)

        psiFieldVerifier.verify {
            PsiFieldVerifier.checkConstructor(
                psiField,
                bestConstructor,
            )
        }
    }

    @Test
    fun shouldCreateObjectWithEmptyList() {
        // given
        initCommonMock()
        psiFieldVerifier
            .`when`<Boolean> { PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass) }
            .thenReturn(false)
        bestConstructorSelector
            .`when`<PsiMethod?> { BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)) }
            .thenReturn(bestConstructor)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiField,
                    bestConstructor,
                )
            }
            .thenReturn(false)

        // when
        val result = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass)

        assertThat(result).isNotNull()
        assertThat(result.fieldsForSetters).isNotNull().hasSize(0)
        assertThat(result.fieldsForConstructor).isNotNull().hasSize(0)

        psiFieldVerifier.verify {
            PsiFieldVerifier.isSetInSetterMethod(
                psiField,
                psiClass,
            )
        }
        bestConstructorSelector.verify {
            BestConstructorSelector.getBestConstructor(
                argumentCaptor.capture(),
                eq(psiClass),
            )
        }

        assertThat(argumentCaptor.lastValue)
            .isNotNull()
            .hasSize(1)
            .extracting("name")
            .containsOnly(PSI_FIELD_NAME)

        psiFieldVerifier.verify {
            PsiFieldVerifier.checkConstructor(psiField, bestConstructor)
        }
    }

    @Test
    fun shouldManageTrickyCaseAccordingToBestConstructorSelection() {
        // given
        psiElementClassMembers = listOf(
            psiElementClassMemberInSetterOnly,
            psiElementClassMemberInConstructorOnly,
            psiElementClassMemberInSetterAndConstructor,
            psiElementClassMemberNowhere,
        )

        given(psiElementClassMemberInSetterOnly.getPsiElement())
            .willReturn(psiFieldInSetterOnly)
        given(psiElementClassMemberInConstructorOnly.getPsiElement())
            .willReturn(psiFieldInConstructorOnly)
        given(psiElementClassMemberInSetterAndConstructor.getPsiElement())
            .willReturn(psiFieldInSetterAndConstructor)
        given(psiElementClassMemberNowhere.getPsiElement()).willReturn(psiFieldNowhere)

        given(psiFieldInSetterOnly.name).willReturn(
            PSI_FIELD_NAME_IN_SETTER_ONLY,
        )
        given(psiFieldInConstructorOnly.name).willReturn(
            PSI_FIELD_NAME_IN_CONSTRUCTOR_ONLY,
        )
        given(psiFieldInSetterAndConstructor.name).willReturn(
            PSI_FIELD_NAME_IN_SETTER_AND_CONSTRUCTOR,
        )
        given(psiFieldNowhere.name).willReturn(PSI_FIELD_NAME_NOWHERE)

        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.isSetInSetterMethod(
                    psiFieldInSetterOnly,
                    psiClass,
                )
            }
            .thenReturn(true)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.isSetInSetterMethod(
                    psiFieldInConstructorOnly,
                    psiClass,
                )
            }
            .thenReturn(false)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.isSetInSetterMethod(
                    psiFieldInSetterAndConstructor,
                    psiClass,
                )
            }
            .thenReturn(true)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.isSetInSetterMethod(
                    psiFieldNowhere,
                    psiClass,
                )
            }
            .thenReturn(false)

        bestConstructorSelector
            .`when`<PsiMethod> {
                BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass))
            }.thenReturn(bestConstructor)

        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiFieldInSetterOnly,
                    bestConstructor,
                )
            }
            .thenReturn(false)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiFieldInConstructorOnly,
                    bestConstructor,
                )
            }
            .thenReturn(true)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiFieldInSetterAndConstructor,
                    bestConstructor,
                )
            }
            .thenReturn(true)
        psiFieldVerifier
            .`when`<Boolean> {
                PsiFieldVerifier.checkConstructor(
                    psiFieldNowhere,
                    bestConstructor,
                )
            }
            .thenReturn(false)

        // when
        val result = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass)

        assertThat(result).isNotNull()

        assertThat(result.allSelectedFields)
            .isNotNull()
            .hasSize(4)
            .containsOnly(
                psiFieldInSetterOnly,
                psiFieldInConstructorOnly,
                psiFieldInSetterAndConstructor,
                psiFieldNowhere,
            )
        assertThat(result.fieldsForConstructor)
            .isNotNull()
            .hasSize(2)
            .containsOnly(psiFieldInConstructorOnly, psiFieldInSetterAndConstructor)

        assertThat(result.fieldsForSetters).isNotNull().hasSize(1)
            .containsOnly(psiFieldInSetterOnly)

        assertThat(result.bestConstructor).isEqualTo(bestConstructor)
    }

    companion object {
        private const val PSI_FIELD_NAME = "psiFieldName"
        private const val PSI_FIELD_NAME_IN_SETTER_ONLY = "psiFieldNameInSetterOnly"
        private const val PSI_FIELD_NAME_IN_CONSTRUCTOR_ONLY = "psiFieldNameInConstructorOnly"
        private const val PSI_FIELD_NAME_IN_SETTER_AND_CONSTRUCTOR = "psiFieldNameInSetterAndConstructor"
        private const val PSI_FIELD_NAME_NOWHERE = "psiFieldNameNowhere"
    }
}
