package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
@Deprecated
public class BugAction extends AbstractEditorAction {

    public BugAction() {
        super(() -> ChatGPTBundle.message("action.code.wrong.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt1Name;
        super.actionPerformed(e);
    }

}
