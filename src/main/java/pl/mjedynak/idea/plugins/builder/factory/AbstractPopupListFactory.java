package pl.mjedynak.idea.plugins.builder.factory;

import javax.swing.JList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.renderer.ActionCellRenderer;

public abstract class AbstractPopupListFactory {
    private ActionCellRenderer actionCellRenderer;

    @NotNull
    public JList<?> getPopupList() {
        JList<?> list = createList();
        list.setCellRenderer(cellRenderer());
        return list;
    }

    @Contract(value = "->new")
    @NotNull
    protected abstract JList<?> createList();

    private @NotNull ActionCellRenderer cellRenderer() {
        if (actionCellRenderer == null) {
            actionCellRenderer = new ActionCellRenderer();
        }
        return actionCellRenderer;
    }
}
