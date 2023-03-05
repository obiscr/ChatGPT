package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.obiscr.chatgpt.core.SendAction;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.obiscr.chatgpt.MyToolWindowFactory.ACTIVE_CONTENT;

/**
 * @author Wuzi
 */
public abstract class AbstractEditorAction extends AnAction {

    protected String text = "";
    protected String key = "";

    public AbstractEditorAction(@NotNull Supplier<String> dynamicText) {
        super(dynamicText);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();

        String apiText = ChatGPTBundle.message(key, "<pre><code>" + selectedText + "</code></pre>");

        SendAction sendAction = e.getProject().getService(SendAction.class);
        Object mainPanel = e.getProject().getUserData(ACTIVE_CONTENT);

        String formattedText = apiText.replace("\n", "<br />");
        sendAction.doActionPerformed((MainPanel) mainPanel, formattedText);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        boolean hasSelection = editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(hasSelection);
    }
}
