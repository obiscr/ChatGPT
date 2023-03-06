package com.obiscr.chatgpt.ui;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.JBUI;
import com.obiscr.chatgpt.ui.action.browser.*;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;

/**
 * @author Wuzi
 */
public class BrowserContent {

    public static final String url = "https://chat.openai.com/chat";
    private final JPanel contentPanel;
    private final JBCefBrowser browser;
    public BrowserContent() {
        contentPanel = new JPanel(new BorderLayout());
        browser = new JBCefBrowser(url);

        if (!JBCefApp.isSupported()) {
            String message = "The current IDE does not support Online ChatGPT, because the JVM RunTime does not support JCEF.\n" +
                    "\n" +
                    "Please refer to the following settings: \nhttps://chatgpt.en.obiscr.com/faq/#4-plugins-are-not-available-in-android-studio";
            JTextPane area = new JTextPane();
            area.setEditable(false);
            area.setText(message);
            area.setBorder(JBUI.Borders.empty(10));
            contentPanel.add(area,BorderLayout.CENTER);
            return;
        }

        AtomicReference<JComponent> component = new AtomicReference<>(browser.getComponent());
        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        toolbarActions.add(new RefreshPage(browser));
        toolbarActions.add(new Separator());
        toolbarActions.add(new ClearCookies(browser,contentPanel));
        toolbarActions.add(new Separator());
        toolbarActions.add(new ZoomLevelAdd(browser));
        toolbarActions.add(new ZoomLevelSub(browser));
        toolbarActions.add(new ZoomLevelDefault(browser));
        ActionToolbarImpl browserToolbar = new ActionToolbarImpl("Browser Toolbar", toolbarActions, true);
        browserToolbar.setTargetComponent(null);
        contentPanel.add(browserToolbar,BorderLayout.NORTH);
        contentPanel.add(component.get(),BorderLayout.CENTER);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void execute(String question) {
        question = question.replace("'", "\\'");

        String fillQuestion = "document.getElementsByTagName(\"textarea\")[0].value = '" + question + "'";
        String doClick = "document.getElementsByTagName(\"textarea\")[0].nextSibling.click()";
        // Fill the question
        String formattedQuestion = fillQuestion.replace("\n", "\\n");
        browser.getCefBrowser().executeJavaScript(formattedQuestion,url,0);
        browser.getCefBrowser().executeJavaScript(doClick,url,0);
    }
}

