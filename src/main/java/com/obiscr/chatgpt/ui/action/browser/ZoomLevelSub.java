package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ZoomLevelSub extends AnAction {
    private final JBCefBrowser browser;
    public ZoomLevelSub(JBCefBrowser browser) {
        super(() -> "Zoom Level Reduced", AllIcons.General.Remove);
        this.browser = browser;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        browser.setZoomLevel(browser.getZoomLevel() - 0.1D);
    }
}
