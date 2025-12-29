package io.github.kingg22.kobra.builder.factory

import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.ide.util.MemberChooser
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Contract
import kotlin.collections.toTypedArray

public object MemberChooserDialogFactory {
    public const val TITLE: String = "Select Fields to Be Available in Builder"

    @JvmStatic
    public fun getMemberChooserDialog(
        elements: List<PsiElementClassMember<*>>,
        project: Project,
    ): MemberChooser<PsiElementClassMember<*>> {
        val psiElementClassMembers = elements.toTypedArray()
        val memberChooserDialog = createNewInstance(project, psiElementClassMembers)
        memberChooserDialog.setCopyJavadocVisible(false)
        memberChooserDialog.selectElements(psiElementClassMembers)
        memberChooserDialog.title = TITLE
        return memberChooserDialog
    }

    @Contract("_, _ -> new")
    private fun createNewInstance(
        project: Project,
        psiElementClassMembers: Array<PsiElementClassMember<*>>,
    ): MemberChooser<PsiElementClassMember<*>> = MemberChooser(psiElementClassMembers, false, true, project, false)
}
