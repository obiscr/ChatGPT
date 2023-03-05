package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ZoomLevelAdd extends AnAction {

    private final JBCefBrowser browser;
    public ZoomLevelAdd(JBCefBrowser browser) {
        super(() -> "Zoom Level Increases", AllIcons.General.Add);
        this.browser = browser;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        browser.setZoomLevel(browser.getZoomLevel() + 0.1D);
    }
}
