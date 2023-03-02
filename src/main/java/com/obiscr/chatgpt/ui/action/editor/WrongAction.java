package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class WrongAction extends AbstractEditorAction {

    public WrongAction() {
        super(() -> ChatGPTBundle.message("action.code.wrong.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "action.code.wrong.text";
        super.actionPerformed(e);
    }

}
