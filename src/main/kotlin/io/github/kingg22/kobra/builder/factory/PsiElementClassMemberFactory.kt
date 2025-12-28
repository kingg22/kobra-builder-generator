package io.github.kingg22.kobra.builder.factory

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.codeInsight.generation.PsiFieldMember
import com.intellij.psi.PsiField
import org.jetbrains.annotations.Contract

public object PsiElementClassMemberFactory {
    @JvmStatic
    @Contract("_ -> new")
    public fun createPsiElementClassMember(psiField: PsiField): PsiElementClassMember<*> = PsiFieldMember(psiField)
}
