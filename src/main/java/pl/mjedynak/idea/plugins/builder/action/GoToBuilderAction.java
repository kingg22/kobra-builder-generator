package pl.mjedynak.idea.plugins.builder.action;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import pl.mjedynak.idea.plugins.builder.action.handler.GoToBuilderActionHandler;

public class GoToBuilderAction extends EditorAction {
    public GoToBuilderAction() {
        super(new GoToBuilderActionHandler());
    }
}
