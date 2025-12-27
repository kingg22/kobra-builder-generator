package pl.mjedynak.idea.plugins.builder.gui.displayer;

import org.jetbrains.annotations.NotNull;

public class GenerateBuilderPopupDisplayer extends AbstractPopupDisplayer {

    private static final String TITLE = "Builder already exists";

    @Override
    protected @NotNull String getTitle() {
        return TITLE;
    }
}
