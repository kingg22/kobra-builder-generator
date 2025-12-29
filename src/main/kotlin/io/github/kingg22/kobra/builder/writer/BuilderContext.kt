package io.github.kingg22.kobra.builder.writer

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import io.github.kingg22.kobra.builder.psi.model.PsiFieldsForBuilder

// @JvmRecord
public data class BuilderContext(
    public val project: Project,
    public val psiFieldsForBuilder: PsiFieldsForBuilder,
    public val targetDirectory: PsiDirectory?,
    public val className: String,
    @JvmField public val psiClassFromEditor: PsiClass,
    @JvmField public val methodPrefix: String?,
    @JvmField public val isInnerBuilder: Boolean,
    public val hasButMethod: Boolean,
    public val useSingleField: Boolean,
    public val hasAddCopyConstructor: Boolean,
) {
    override fun hashCode(): Int = arrayOf(
        project,
        psiFieldsForBuilder,
        targetDirectory,
        className,
        psiClassFromEditor,
        methodPrefix,
    ).contentHashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is BuilderContext) {
            return false
        }
        return this.project == other.project &&
            this.psiFieldsForBuilder == other.psiFieldsForBuilder &&
            this.targetDirectory == other.targetDirectory &&
            this.className == other.className &&
            this.psiClassFromEditor == other.psiClassFromEditor &&
            this.methodPrefix == other.methodPrefix
    }
}
