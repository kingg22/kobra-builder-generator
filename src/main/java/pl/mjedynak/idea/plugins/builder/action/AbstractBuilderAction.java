package pl.mjedynak.idea.plugins.builder.action;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.util.pico.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import pl.mjedynak.idea.plugins.builder.action.handler.AbstractBuilderActionHandler;
import pl.mjedynak.idea.plugins.builder.action.handler.DisplayChoosers;
import pl.mjedynak.idea.plugins.builder.psi.BuilderPsiClassBuilder;
import pl.mjedynak.idea.plugins.builder.writer.BuilderWriter;

public abstract class AbstractBuilderAction extends EditorAction {

    protected static AbstractBuilderActionHandler builderActionHandler;

    protected static MutablePicoContainer picoContainer = new DefaultPicoContainer();

    static {
        picoContainer.registerComponentImplementation(BuilderPsiClassBuilder.class);
        picoContainer.registerComponentImplementation(BuilderWriter.class);
        picoContainer.registerComponentImplementation(DisplayChoosers.class);
    }

    protected AbstractBuilderAction() {
        super(builderActionHandler);
    }
}
