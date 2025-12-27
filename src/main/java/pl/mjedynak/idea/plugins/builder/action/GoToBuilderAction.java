package pl.mjedynak.idea.plugins.builder.action;

import pl.mjedynak.idea.plugins.builder.action.handler.AbstractBuilderActionHandler;
import pl.mjedynak.idea.plugins.builder.action.handler.GoToBuilderActionHandler;

public class GoToBuilderAction extends AbstractBuilderAction {

    static {
        picoContainer.registerComponentImplementation(GoToBuilderActionHandler.class);
        builderActionHandler =
                (AbstractBuilderActionHandler) picoContainer.getComponentInstanceOfType(GoToBuilderActionHandler.class);
    }
}
