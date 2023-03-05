package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.ui.jcef.JBCefBrowser;
import com.obiscr.chatgpt.ui.BrowserContent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


/**
 * @author Wuzi
 */
public class ClearCookies extends AnAction {

    private final JBCefBrowser browser;
    private final JPanel contentPanel;
    public ClearCookies(JBCefBrowser browser, JPanel contentPanel) {
        super(() -> "Clear Cookies", AllIcons.Actions.Cancel);
        this.browser = browser;
        this.contentPanel = contentPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        boolean yes = MessageDialogBuilder.yesNo("Are you sure you want to clear?",
                        "Once the cookies are cleared, you will need to " +
                                "login again, are you sure to continue?")
                .yesText("Yes")
                .noText("No").ask(contentPanel);
        if (yes) {
            browser.getJBCefCookieManager().getCefCookieManager().deleteCookies(BrowserContent.url,"");
        }
    }
}
