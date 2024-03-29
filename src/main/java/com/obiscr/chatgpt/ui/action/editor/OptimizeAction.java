package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
@Deprecated
public class OptimizeAction extends AbstractEditorAction {

    public OptimizeAction() {
        super(() -> ChatGPTBundle.message("action.code.optimize.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt2Name;
        super.actionPerformed(e);
    }

}
