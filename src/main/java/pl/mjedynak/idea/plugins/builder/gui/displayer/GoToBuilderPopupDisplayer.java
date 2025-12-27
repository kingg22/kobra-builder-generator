package pl.mjedynak.idea.plugins.builder.gui.displayer;

import org.jetbrains.annotations.NotNull;

public class GoToBuilderPopupDisplayer extends AbstractPopupDisplayer {

    private static final String TITLE = "Builder not found";

    @Override
    protected @NotNull String getTitle() {
        return TITLE;
    }
}
