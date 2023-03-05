package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefCookie;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.BrowserContent;
import com.obiscr.chatgpt.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class RefreshCookie extends AnAction {

    private final JBCefBrowser browser;
    public RefreshCookie(JBCefBrowser browser) {
        super(() -> "RefreshPage", AllIcons.Actions.Refresh);
        this.browser = browser;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String accessToken = OpenAISettingsState.getInstance().accessToken;
        if (StringUtil.isNotEmpty(accessToken)) {
            JBCefCookie jbCefCookie = new JBCefCookie("__Secure-next-auth.session-token", accessToken , "chat.openai.com", "/", true, true);
            browser.getJBCefCookieManager().setCookie(BrowserContent.url,jbCefCookie);
        }
    }
}
