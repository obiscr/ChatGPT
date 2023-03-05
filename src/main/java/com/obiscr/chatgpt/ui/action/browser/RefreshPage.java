package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class RefreshPage extends AnAction {

    private final JBCefBrowser browser;
    public RefreshPage(JBCefBrowser browser) {
        super(() -> "RefreshPage", AllIcons.Actions.Refresh);
        this.browser = browser;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        browser.getCefBrowser().reload();
    }
}
