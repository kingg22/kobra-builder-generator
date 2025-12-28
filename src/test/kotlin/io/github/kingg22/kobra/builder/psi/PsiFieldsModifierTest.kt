package io.github.kingg22.kobra.builder.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiModifierList
import com.intellij.psi.PsiTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class PsiFieldsModifierTest {
    @Mock
    private lateinit var builderClass: PsiClass

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var psiElementFactory: PsiElementFactory

    private lateinit var psiFieldsForSetters: MutableList<PsiField>
    private lateinit var psiFieldsForConstructor: MutableList<PsiField>

    @BeforeEach
    fun setUp() {
        psiFieldsForConstructor = ArrayList()
        psiFieldsForSetters = ArrayList()
    }

    @Test
    fun shouldAddPrivateFieldsToBuilderClass() {
        // given
        val psiFieldForSetters: PsiField = mock()
        given(psiFieldForSetters.name).willReturn("setterField")
        given(psiFieldForSetters.type).willReturn(PsiTypes.intType())
        psiFieldsForSetters.add(psiFieldForSetters)
        val copyPsiFieldForSetter: PsiField = mock()
        val copyPsiFieldForSetterModifierList: PsiModifierList = mock()
        given(copyPsiFieldForSetter.modifierList).willReturn(copyPsiFieldForSetterModifierList)
        given(psiElementFactory.createField("setterField", PsiTypes.intType())).willReturn(copyPsiFieldForSetter)

        val psiFieldForConstructor: PsiField = mock()
        given(psiFieldForConstructor.name).willReturn("constructorField")
        given(psiFieldForConstructor.type).willReturn(PsiTypes.booleanType())
        psiFieldsForConstructor.add(psiFieldForConstructor)
        val copyPsiFieldForConstructor: PsiField = mock()
        val copyPsiFieldForConstructorModifierList: PsiModifierList = mock()
        given(copyPsiFieldForConstructor.modifierList).willReturn(copyPsiFieldForConstructorModifierList)
        given(
            psiElementFactory.createField(
                "constructorField",
                PsiTypes.booleanType(),
            ),
        ).willReturn(copyPsiFieldForConstructor)

        given(builderClass.project).willReturn(project)
        given(project.getService(PsiElementFactory::class.java)).willReturn(psiElementFactory)

        // when
        PsiFieldsModifier.modifyFields(psiFieldsForSetters, psiFieldsForConstructor, builderClass)

        verify(builderClass).add(copyPsiFieldForSetter)
        verify(copyPsiFieldForSetterModifierList).setModifierProperty(PsiModifier.PRIVATE, true)
        verify(builderClass).add(copyPsiFieldForConstructor)
        verify(copyPsiFieldForConstructorModifierList).setModifierProperty(PsiModifier.PRIVATE, true)
        verifyNoMoreInteractions(builderClass)
    }
}
