package pl.mjedynak.idea.plugins.builder.action;

import pl.mjedynak.idea.plugins.builder.action.handler.AbstractBuilderActionHandler;
import pl.mjedynak.idea.plugins.builder.action.handler.GenerateBuilderActionHandler;

public class GenerateBuilderAction extends AbstractBuilderAction {

    static {
        picoContainer.registerComponentImplementation(GenerateBuilderActionHandler.class);
        builderActionHandler = (AbstractBuilderActionHandler)
                picoContainer.getComponentInstanceOfType(GenerateBuilderActionHandler.class);
    }
}
