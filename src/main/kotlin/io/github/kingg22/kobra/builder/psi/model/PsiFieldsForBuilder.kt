package io.github.kingg22.kobra.builder.psi.model

import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod

@JvmRecord
public data class PsiFieldsForBuilder(
    val fieldsForSetters: List<PsiField>,
    val fieldsForConstructor: List<PsiField>,
    val allSelectedFields: List<PsiField>,
    @JvmField val bestConstructor: PsiMethod?,
)
