package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.NlsActions;
import com.obiscr.chatgpt.core.SendAction;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.BrowserContent;
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

    public AbstractEditorAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText) {
        super(dynamicText);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        // Browser text does not require code blocks
        String browserText = selectedText;
        selectedText = "<pre><code>" + selectedText + "</code></pre>";
        text = ChatGPTBundle.message(key, selectedText);
        SendAction sendAction = e.getProject().getService(SendAction.class);
        Object mainPanel = e.getProject().getUserData(ACTIVE_CONTENT);

        // If the Online Chat GPT mode is currently selected, the selected
        // code needs to be processed with Cef Browser
        if (mainPanel instanceof BrowserContent) {
            ((BrowserContent) mainPanel).execute(browserText);
            return;
        }

        String formattedText = text.replace("\n", "<br />");
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
