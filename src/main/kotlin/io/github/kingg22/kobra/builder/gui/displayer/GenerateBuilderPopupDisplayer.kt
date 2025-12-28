package io.github.kingg22.kobra.builder.gui.displayer

public class GenerateBuilderPopupDisplayer : AbstractPopupDisplayer() {
    override val title: String get() = TITLE

    public companion object {
        private const val TITLE = "Builder already exists"
    }
}
