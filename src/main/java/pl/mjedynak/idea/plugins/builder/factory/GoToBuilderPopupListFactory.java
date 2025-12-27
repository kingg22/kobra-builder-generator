package pl.mjedynak.idea.plugins.builder.factory;

import static java.util.Collections.singletonList;

import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.action.GenerateBuilderAdditionalAction;

public class GoToBuilderPopupListFactory extends AbstractPopupListFactory {
    @Override
    protected @NotNull JBList<GenerateBuilderAdditionalAction> createList() {
        return new JBList<>(singletonList(new GenerateBuilderAdditionalAction()));
    }
}
