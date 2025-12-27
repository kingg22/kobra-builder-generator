package pl.mjedynak.idea.plugins.builder.factory;

import static java.util.Arrays.asList;

import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.action.AbstractBuilderAdditionalAction;
import pl.mjedynak.idea.plugins.builder.action.GoToBuilderAdditionalAction;
import pl.mjedynak.idea.plugins.builder.action.RegenerateBuilderAdditionalAction;

public class GenerateBuilderPopupListFactory extends AbstractPopupListFactory {
    @Override
    protected @NotNull JBList<AbstractBuilderAdditionalAction> createList() {
        return new JBList<>(asList(new GoToBuilderAdditionalAction(), new RegenerateBuilderAdditionalAction()));
    }
}
