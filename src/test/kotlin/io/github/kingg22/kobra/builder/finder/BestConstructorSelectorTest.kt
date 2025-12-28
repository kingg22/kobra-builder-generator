package io.github.kingg22.kobra.builder.finder

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiParameterList
import com.intellij.psi.PsiParameterListOwner
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiTypes
import com.intellij.psi.PsiVariable
import io.github.kingg22.kobra.builder.psi.BestConstructorSelector
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BestConstructorSelectorTest {
    @Mock
    private lateinit var psiClass: PsiClass

    @Mock
    private lateinit var constructor0: PsiMethod

    @Mock
    private lateinit var constructor1: PsiMethod

    @Mock
    private lateinit var constructor2a: PsiMethod

    @Mock
    private lateinit var constructor2b: PsiMethod

    @Mock
    private lateinit var constructor3: PsiMethod

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameterList0: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameterList1: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameterList2a: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameterList2b: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var parameterList3: PsiParameterList

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField1: PsiField

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField2: PsiField

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField3: PsiField

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField4: PsiField

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiField5: PsiField

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiParameter1: PsiParameter

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiParameter2: PsiParameter

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiParameter3: PsiParameter

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiParameter4: PsiParameter

    @BeforeEach
    fun initMock() {
        mockPsiVariable(psiField1, NAME_1, PsiTypes.intType())
        mockPsiVariable(psiField2, NAME_2, PsiTypes.intType())
        mockPsiVariable(psiField3, NAME_3, PsiTypes.intType())
        mockPsiVariable(psiField4, NAME_4, PsiTypes.intType())
        mockPsiVariable(psiField5, NAME_5, PsiTypes.intType())

        mockPsiVariable(psiParameter1, NAME_1, PsiTypes.intType())
        mockPsiVariable(psiParameter2, NAME_2, PsiTypes.intType())
        mockPsiVariable(psiParameter3, NAME_3, PsiTypes.intType())
        mockPsiVariable(psiParameter4, NAME_4, PsiTypes.intType())

        mockConstructor(constructor0, parameterList0, *EMPTY_PSI_PARAMETERS)
        mockConstructor(constructor1, parameterList1, psiParameter4)
        mockConstructor(constructor2a, parameterList2a, psiParameter1, psiParameter2)
        mockConstructor(constructor2b, parameterList2b, psiParameter1, psiParameter4)
        mockConstructor(constructor3, parameterList3, psiParameter1, psiParameter2, psiParameter3)
    }

    private fun mockPsiVariable(psiVariable: PsiVariable, name: String?, type: PsiPrimitiveType?) {
        given(psiVariable.name).willReturn(name)
        given(psiVariable.type).willReturn(type)
    }

    private fun mockConstructor(
        constructor: PsiParameterListOwner,
        parameterList: PsiParameterList,
        vararg psiParameters: PsiParameter,
    ) {
        given(constructor.parameterList).willReturn(parameterList)
        given(parameterList.parameters).willReturn(psiParameters)
        given(parameterList.parametersCount).willReturn(psiParameters.size)
    }

    @Test
    fun shouldFindConstructorWithLeastParametersIfAnyFieldsToFind() {
        doTest(
            emptyList(),
            arrayOf(constructor0, constructor1, constructor2a, constructor2b, constructor3),
            constructor0,
        )
    }

    @Test
    fun shouldFindConstructorWithLeastParametersIfAnyFieldsToFindFoundInConstructors() {
        doTest(
            listOf(psiField5),
            arrayOf(constructor0, constructor1, constructor2a, constructor2b, constructor3),
            constructor0,
        )
    }

    @Test
    fun shouldFindConstructorWithExactMatching() {
        doTest(
            listOf(psiField1, psiField2),
            arrayOf(constructor0, constructor1, constructor2a, constructor2b, constructor3),
            constructor2a,
        )
    }

    @Test
    fun shouldFindConstructorWithAllFieldsFoundButExtraParameters() {
        doTest(
            listOf(psiField2, psiField3),
            arrayOf(constructor0, constructor1, constructor2a, constructor2b, constructor3),
            constructor3,
        )
    }

    @Test
    fun shouldFindConstructorWithMaxFieldsFoundAndLessParameters() {
        doTest(
            listOf(psiField2, psiField4),
            arrayOf(constructor0, constructor1, constructor2a, constructor2b, constructor3),
            constructor1,
        )
    }

    private fun doTest(psiFields: Collection<PsiField>, psiMethods: Array<PsiMethod>, expectedConstructor: PsiMethod) {
        // given
        given(psiClass.constructors).willReturn(psiMethods)

        // when
        val bestConstructor = BestConstructorSelector.getBestConstructor(psiFields, psiClass)

        assertThat(bestConstructor).isEqualTo(expectedConstructor)
    }

    companion object {
        private const val NAME_1 = "name1"
        private const val NAME_2 = "name2"
        private const val NAME_3 = "name3"
        private const val NAME_4 = "name4"
        private const val NAME_5 = "name5"
        private val EMPTY_PSI_PARAMETERS = emptyArray<PsiParameter>()
    }
}
