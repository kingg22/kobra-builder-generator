package pl.mjedynak.idea.plugins.builder.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.idea.plugins.builder.psi.BestConstructorSelector;
import pl.mjedynak.idea.plugins.builder.psi.model.PsiFieldsForBuilder;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

@ExtendWith(MockitoExtension.class)
public class PsiFieldsForBuilderFactoryTest {

    private static final String PSI_FIELD_NAME = "psiFieldName";
    private static final String PSI_FIELD_NAME_IN_SETTER_ONLY = "psiFieldNameInSetterOnly";
    private static final String PSI_FIELD_NAME_IN_CONSTRUCTOR_ONLY = "psiFieldNameInConstructorOnly";
    private static final String PSI_FIELD_NAME_IN_SETTER_AND_CONSTRUCTOR = "psiFieldNameInSetterAndConstructor";
    private static final String PSI_FIELD_NAME_NOWHERE = "psiFieldNameNowhere";

    @Mock
    private MockedStatic<PsiFieldVerifier> psiFieldVerifier;

    @Mock
    private PsiClass psiClass;

    @Mock
    private PsiElementClassMember<?> psiElementClassMember;

    @Mock
    private PsiElementClassMember<?> psiElementClassMemberInSetterOnly;

    @Mock
    private PsiElementClassMember<?> psiElementClassMemberInConstructorOnly;

    @Mock
    private PsiElementClassMember<?> psiElementClassMemberInSetterAndConstructor;

    @Mock
    private PsiElementClassMember<?> psiElementClassMemberNowhere;

    @Mock
    private PsiField psiField;

    @Mock
    private PsiField psiFieldInSetterOnly;

    @Mock
    private PsiField psiFieldInConstructorOnly;

    @Mock
    private PsiField psiFieldInSetterAndConstructor;

    @Mock
    private PsiField psiFieldNowhere;

    @Mock
    private MockedStatic<BestConstructorSelector> bestConstructorSelector;

    @Mock
    private PsiMethod bestConstructor;

    @Captor
    private ArgumentCaptor<List<PsiField>> argumentCaptor;

    private List<PsiElementClassMember<?>> psiElementClassMembers;

    private void initCommonMock() {
        psiElementClassMembers = List.of(psiElementClassMember);
        given(psiElementClassMember.getPsiElement()).willReturn(psiField);
        given(psiField.getName()).willReturn(PSI_FIELD_NAME);
    }

    @Test
    void shouldCreateObjectWithPsiFieldsForSetters() {
        // given
        initCommonMock();
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass))
                .thenReturn(true);
        bestConstructorSelector
                .when(() -> BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)))
                .thenReturn(bestConstructor);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor))
                .thenReturn(false);

        // when
        PsiFieldsForBuilder result =
                PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.fieldsForConstructor()).isNotNull().hasSize(0);
        assertThat(result.fieldsForSetters()).isNotNull().hasSize(1).containsOnly(psiField);

        psiFieldVerifier.verify(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass));
        bestConstructorSelector.verify(
                () -> BestConstructorSelector.getBestConstructor(argumentCaptor.capture(), eq(psiClass)));
        assertThat(argumentCaptor.getValue()).isNotNull().hasSize(0);
        psiFieldVerifier.verify(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor));
    }

    @Test
    void shouldCreateObjectWithPsiFieldsForConstructor() {
        // given
        initCommonMock();
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass))
                .thenReturn(false);
        bestConstructorSelector
                .when(() -> BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)))
                .thenReturn(bestConstructor);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor))
                .thenReturn(true);

        // when
        PsiFieldsForBuilder result =
                PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.fieldsForSetters()).isNotNull().hasSize(0);
        assertThat(result.fieldsForConstructor()).isNotNull().hasSize(1).containsOnly(psiField);

        psiFieldVerifier.verify(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass));
        bestConstructorSelector.verify(
                () -> BestConstructorSelector.getBestConstructor(argumentCaptor.capture(), eq(psiClass)));
        assertThat(argumentCaptor.getValue())
                .isNotNull()
                .hasSize(1)
                .extracting("name")
                .containsOnly(PSI_FIELD_NAME);
        psiFieldVerifier.verify(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor));
    }

    @Test
    void shouldCreateObjectWithEmptyList() {
        // given
        initCommonMock();
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass))
                .thenReturn(false);
        bestConstructorSelector
                .when(() -> BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)))
                .thenReturn(bestConstructor);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor))
                .thenReturn(false);

        // when
        PsiFieldsForBuilder result =
                PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.fieldsForSetters()).isNotNull().hasSize(0);
        assertThat(result.fieldsForConstructor()).isNotNull().hasSize(0);

        psiFieldVerifier.verify(() -> PsiFieldVerifier.isSetInSetterMethod(psiField, psiClass));
        bestConstructorSelector.verify(
                () -> BestConstructorSelector.getBestConstructor(argumentCaptor.capture(), eq(psiClass)));
        assertThat(argumentCaptor.getValue())
                .isNotNull()
                .hasSize(1)
                .extracting("name")
                .containsOnly(PSI_FIELD_NAME);
        psiFieldVerifier.verify(() -> PsiFieldVerifier.checkConstructor(psiField, bestConstructor));
    }

    @Test
    void shouldManageTrickyCaseAccordingToBestConstructorSelection() {
        // given
        psiElementClassMembers = Lists.newArrayList(
                psiElementClassMemberInSetterOnly,
                psiElementClassMemberInConstructorOnly,
                psiElementClassMemberInSetterAndConstructor,
                psiElementClassMemberNowhere);

        given(psiElementClassMemberInSetterOnly.getPsiElement()).willReturn(psiFieldInSetterOnly);
        given(psiElementClassMemberInConstructorOnly.getPsiElement()).willReturn(psiFieldInConstructorOnly);
        given(psiElementClassMemberInSetterAndConstructor.getPsiElement()).willReturn(psiFieldInSetterAndConstructor);
        given(psiElementClassMemberNowhere.getPsiElement()).willReturn(psiFieldNowhere);

        given(psiFieldInSetterOnly.getName()).willReturn(PSI_FIELD_NAME_IN_SETTER_ONLY);
        given(psiFieldInConstructorOnly.getName()).willReturn(PSI_FIELD_NAME_IN_CONSTRUCTOR_ONLY);
        given(psiFieldInSetterAndConstructor.getName()).willReturn(PSI_FIELD_NAME_IN_SETTER_AND_CONSTRUCTOR);
        given(psiFieldNowhere.getName()).willReturn(PSI_FIELD_NAME_NOWHERE);

        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiFieldInSetterOnly, psiClass))
                .thenReturn(true);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiFieldInConstructorOnly, psiClass))
                .thenReturn(false);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiFieldInSetterAndConstructor, psiClass))
                .thenReturn(true);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.isSetInSetterMethod(psiFieldNowhere, psiClass))
                .thenReturn(false);

        bestConstructorSelector
                .when(() -> BestConstructorSelector.getBestConstructor(anyList(), eq(psiClass)))
                .thenReturn(bestConstructor);

        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiFieldInSetterOnly, bestConstructor))
                .thenReturn(false);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiFieldInConstructorOnly, bestConstructor))
                .thenReturn(true);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiFieldInSetterAndConstructor, bestConstructor))
                .thenReturn(true);
        psiFieldVerifier
                .when(() -> PsiFieldVerifier.checkConstructor(psiFieldNowhere, bestConstructor))
                .thenReturn(false);

        // when
        PsiFieldsForBuilder result =
                PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(psiElementClassMembers, psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.allSelectedFields())
                .isNotNull()
                .hasSize(4)
                .containsOnly(
                        psiFieldInSetterOnly,
                        psiFieldInConstructorOnly,
                        psiFieldInSetterAndConstructor,
                        psiFieldNowhere);
        assertThat(result.fieldsForConstructor())
                .isNotNull()
                .hasSize(2)
                .containsOnly(psiFieldInConstructorOnly, psiFieldInSetterAndConstructor);
        assertThat(result.fieldsForSetters()).isNotNull().hasSize(1).containsOnly(psiFieldInSetterOnly);
        assertThat(result.bestConstructor()).isEqualTo(bestConstructor);
    }
}
