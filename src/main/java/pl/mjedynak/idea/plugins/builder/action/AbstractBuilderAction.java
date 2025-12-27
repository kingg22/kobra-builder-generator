package pl.mjedynak.idea.plugins.builder.action;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.util.pico.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import pl.mjedynak.idea.plugins.builder.action.handler.AbstractBuilderActionHandler;

public abstract class AbstractBuilderAction extends EditorAction {

    protected static AbstractBuilderActionHandler builderActionHandler;

    protected static MutablePicoContainer picoContainer = new DefaultPicoContainer();

    protected AbstractBuilderAction() {
        super(builderActionHandler);
    }
}
