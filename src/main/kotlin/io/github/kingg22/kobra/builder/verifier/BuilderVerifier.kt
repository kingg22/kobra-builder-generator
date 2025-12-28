package io.github.kingg22.kobra.builder.verifier

import com.intellij.psi.PsiClass

public object BuilderVerifier {
    private const val SUFFIX = "Builder"

    @JvmStatic
    public fun isBuilder(psiClass: PsiClass): Boolean {
        val className = psiClass.name
        return className != null && className.endsWith(SUFFIX)
    }
}
