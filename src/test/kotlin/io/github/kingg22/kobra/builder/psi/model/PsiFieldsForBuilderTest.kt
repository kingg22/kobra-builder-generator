package io.github.kingg22.kobra.builder.psi.model

import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

// Probably delete this inútil test
@ExtendWith(MockitoExtension::class)
class PsiFieldsForBuilderTest {
    private lateinit var psiFieldsForBuilder: PsiFieldsForBuilder

    private lateinit var psiFieldsForSetters: MutableList<PsiField>
    private lateinit var psiFieldsForConstructor: MutableList<PsiField>
    private lateinit var allSelectedPsiFields: MutableList<PsiField>
    private lateinit var bestConstructor: PsiMethod

    @BeforeEach
    fun setUp() {
        psiFieldsForSetters = mutableListOf()
        psiFieldsForSetters.add(mock())

        psiFieldsForConstructor = mutableListOf()
        psiFieldsForConstructor.add(mock())

        allSelectedPsiFields = mutableListOf()
        allSelectedPsiFields.add(mock())
        allSelectedPsiFields.add(mock())
        bestConstructor = mock()
        psiFieldsForBuilder = PsiFieldsForBuilder(
            psiFieldsForSetters,
            psiFieldsForConstructor,
            allSelectedPsiFields,
            bestConstructor,
        )
    }

    @Test
    fun shouldGetThreeListsOfFieldsAndBestConstructor() {
        assertThat(psiFieldsForBuilder.fieldsForSetters).isEqualTo(psiFieldsForSetters)
        assertThat(psiFieldsForBuilder.fieldsForConstructor).isEqualTo(psiFieldsForConstructor)
        assertThat(psiFieldsForBuilder.allSelectedFields).isEqualTo(allSelectedPsiFields)
        assertThat(psiFieldsForBuilder.bestConstructor).isEqualTo(bestConstructor)
    }
}
