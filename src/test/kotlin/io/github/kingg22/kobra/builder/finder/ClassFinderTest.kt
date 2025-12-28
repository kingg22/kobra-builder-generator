package io.github.kingg22.kobra.builder.finder

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import io.github.kingg22.kobra.builder.psi.PsiHelper
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
class ClassFinderTest {
    @Mock
    private lateinit var psiHelper: MockedStatic<PsiHelper>

    @Mock
    private lateinit var psiShortNamesCache: PsiShortNamesCache

    @Mock
    private lateinit var project: Project

    @Mock
    private lateinit var globalSearchScope: GlobalSearchScope

    @BeforeEach
    fun setUp() {
        given<Any?>(project.getUserData(any()))
            .willReturn(globalSearchScope)
        psiHelper.`when`<PsiShortNamesCache> { PsiHelper.getPsiShortNamesCache(project) }
            .thenReturn(psiShortNamesCache)
    }

    @Test
    fun shouldNotFindClassWhenSearchPatternNotFound() {
        // given
        val emptyArray = emptyArray<PsiClass>()
        given(psiShortNamesCache.getClassesByName(CLASS_NAME, globalSearchScope))
            .willReturn(emptyArray)

        // when
        val result = ClassFinder.findClass(CLASS_NAME, project)

        assertThat(result).isNull()
    }

    @Test
    fun shouldFoundClassWhenBuilderSearchPatternFound() {
        // given
        val foundClass: PsiClass = mock()
        given(foundClass.name).willReturn(CLASS_NAME)
        val foundClassArray = arrayOf(foundClass)

        given(psiShortNamesCache.getClassesByName(CLASS_NAME, globalSearchScope))
            .willReturn(foundClassArray)

        // when
        val result = ClassFinder.findClass(CLASS_NAME, project)

        // then
        verifyClassIsFound(CLASS_NAME, result)
    }

    private fun verifyClassIsFound(name: String?, result: PsiClass?) {
        assertThat(result).isNotNull()
        assertThat(result!!.name).isEqualTo(name)
    }

    companion object {
        private const val CLASS_NAME = "SomeClass"
    }
}
