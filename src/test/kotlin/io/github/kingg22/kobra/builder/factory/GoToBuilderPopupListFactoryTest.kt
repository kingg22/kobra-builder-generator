package io.github.kingg22.kobra.builder.factory

import com.intellij.ui.ExpandedItemListCellRendererWrapper
import com.intellij.ui.components.JBList
import io.github.kingg22.kobra.builder.renderer.ActionCellRenderer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GoToBuilderPopupListFactoryTest {
    private val popupListFactory: AbstractPopupListFactory = GoToBuilderPopupListFactory()

    @Test
    fun shouldCreateJBListWithActionCellRenderer() {
        // when
        val popupList = popupListFactory.popupList

        // then
        assertThat(popupList).isInstanceOf(JBList::class.java)
        assertThat(popupList.getCellRenderer()).isInstanceOf(ExpandedItemListCellRendererWrapper::class.java)
        assertThat(
            (popupList.getCellRenderer() as ExpandedItemListCellRendererWrapper<*>).wrappee,
        ).isInstanceOf(ActionCellRenderer::class.java)
        assertThat((popupList as JBList<*>).getItemsCount()).isEqualTo(1)
    }

    @Test
    fun shouldLazilyInitializeCellRenderer() {
        // then
        assertThat(popupListFactory.actionCellRenderer).isNull()
    }

    @Test
    fun shouldUseTheSameCellRendererForConsequentInvocations() {
        // when
        popupListFactory.popupList
        val firstRenderer = popupListFactory.actionCellRenderer
        popupListFactory.popupList
        val secondRenderer = popupListFactory.actionCellRenderer

        // then
        assertThat(firstRenderer).isSameAs(secondRenderer)
    }
}
