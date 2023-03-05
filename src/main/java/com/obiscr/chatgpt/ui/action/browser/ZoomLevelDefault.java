package com.obiscr.chatgpt.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ZoomLevelDefault extends AnAction {

    private final JBCefBrowser browser;
    private final double defaultZoomLevel;
    public ZoomLevelDefault(JBCefBrowser browser) {
        super(() -> "Restore Default Zoom Level", AllIcons.Actions.SetDefault);
        this.browser = browser;
        this.defaultZoomLevel = browser.getZoomLevel();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        browser.setZoomLevel(defaultZoomLevel);
    }
}
