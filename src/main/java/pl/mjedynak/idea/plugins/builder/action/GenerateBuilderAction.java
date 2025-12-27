package pl.mjedynak.idea.plugins.builder.action;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import pl.mjedynak.idea.plugins.builder.action.handler.GenerateBuilderActionHandler;

public class GenerateBuilderAction extends EditorAction {
    public GenerateBuilderAction() {
        super(new GenerateBuilderActionHandler());
    }
}
