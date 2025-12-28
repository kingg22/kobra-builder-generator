package io.github.kingg22.kobra.builder.action

import com.intellij.openapi.editor.actionSystem.EditorAction
import io.github.kingg22.kobra.builder.action.handler.GoToBuilderActionHandler

public class GoToBuilderAction : EditorAction(GoToBuilderActionHandler())
