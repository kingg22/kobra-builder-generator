package io.github.kingg22.kobra.builder.finder

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class BuilderFinderTest {
    @Mock
    private lateinit var classFinder: MockedStatic<ClassFinder>

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var psiClass: PsiClass

    @Mock(strictness = Mock.Strictness.LENIENT)
    private lateinit var builderClass: PsiClass

    @Mock
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        given(psiClass.isEnum).willReturn(false)
        given(psiClass.isInterface).willReturn(false)
        given(psiClass.isAnnotationType).willReturn(false)
        given(psiClass.project).willReturn(project)
        given(psiClass.name).willReturn(CLASS_NAME)
        given(psiClass.allInnerClasses).willReturn(emptyArray())
        given(builderClass.name).willReturn(BUILDER_NAME)
        given(builderClass.project).willReturn(project)
    }

    @Test
    fun shouldNotFindBuilderForEnum() {
        // given
        given(psiClass.isEnum).willReturn(true)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindBuilderForInterface() {
        // given
        given(psiClass.isAnnotationType).willReturn(true)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindBuilderForAnnotationType() {
        // given
        given(psiClass.isAnnotationType).willReturn(true)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindBuilderForClassWhenClassFounderReturnsNull() {
        // given
        classFinder.`when`<PsiClass?> { ClassFinder.findClass(CLASS_NAME, project) }
            .thenReturn(null)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldFindBuilderForClassWhenBuilderWithTheExactClassNameIsPresent() {
        // given

        val builderClass: PsiClass = mock()
        given(builderClass.name).willReturn(BUILDER_NAME)
        classFinder.`when`<PsiClass> { ClassFinder.findClass(BUILDER_NAME, project) }
            .thenReturn(builderClass)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNotNull()
        assertThat(result!!.name).isEqualTo(BUILDER_NAME)
    }

    @Test
    fun shouldFindInnerBuilder() {
        // given
        val innerClass: PsiClass = mock()
        val innerClasses = arrayOf(innerClass)
        given(innerClass.name).willReturn(BuilderFinder.SEARCH_PATTERN)
        given(psiClass.allInnerClasses).willReturn(innerClasses)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isEqualTo(innerClass)
    }

    @Test
    fun shouldNotFindInnerBuilderWhenInnerClassNameDoesNotMatchPattern() {
        // given
        val innerClass: PsiClass = mock()
        val innerClasses = arrayOf<PsiClass?>(innerClass)
        given(innerClass.name).willReturn("SomeInnerClass")
        given(psiClass.allInnerClasses).willReturn(innerClasses)
        classFinder.`when`<PsiClass?> {
            ClassFinder.findClass(
                anyString(),
                eq(project),
            )
        }.thenReturn(null)

        // when
        val result = BuilderFinder.findBuilderForClass(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindClassForEnum() {
        // given
        given(psiClass.isEnum).willReturn(true)

        // when
        val result = BuilderFinder.findClassForBuilder(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindClassForInterface() {
        // given
        given(psiClass.isAnnotationType).willReturn(true)

        // when
        val result = BuilderFinder.findClassForBuilder(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindClassForAnnotationType() {
        // given
        given(psiClass.isAnnotationType).willReturn(true)

        // when
        val result = BuilderFinder.findClassForBuilder(psiClass)

        assertThat(result).isNull()
    }

    @Test
    fun shouldNotFindClassForBuilderWhenClassFounderReturnsNull() {
        // given
        classFinder.`when`<PsiClass?> { ClassFinder.findClass(BUILDER_NAME, project) }
            .thenReturn(null)

        // when
        val result = BuilderFinder.findClassForBuilder(builderClass)

        assertThat(result).isNull()
        classFinder.verify { ClassFinder.findClass(CLASS_NAME, project) }
    }

    @Test
    fun shouldFindClassForBuilderWhenClassWithTheExactBuildersNameIsPresent() {
        // given
        classFinder.`when`<PsiClass> { ClassFinder.findClass(CLASS_NAME, project) }
            .thenReturn(psiClass)

        // when
        val result = BuilderFinder.findClassForBuilder(psiClass)

        assertThat(result).isNotNull()
        assertThat(result!!.name).isEqualTo(CLASS_NAME)
    }

    companion object {
        private const val CLASS_NAME = "SomeClass"
        private const val BUILDER_NAME: String = CLASS_NAME + BuilderFinder.SEARCH_PATTERN
    }
}
