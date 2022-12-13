package com.obiscr.chatgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.BrowserContent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wuzi
 */
public class BrowserAction extends DumbAwareAction {
  private static final Logger LOG = LoggerFactory.getLogger(BrowserAction.class);

  public BrowserAction(@NotNull @Nls String text) {
    super(() -> text,AllIcons.Xml.Browsers.Chrome);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ToolWindowManager instance = ToolWindowManager.getInstance(e.getProject());
    ToolWindow chatGPT = instance.getToolWindow("ChatGPT");
    if (chatGPT ==null) {
      return;
    }

    ContentManager contentManager = chatGPT.getContentManager();
    Content browserContent = contentManager.findContent(ChatGPTBundle.
            message("toolwindows.content.online"));

    if (browserContent != null) {
      contentManager.removeContent(browserContent, false);
    }

    ContentFactory contentFactory = ContentFactory.getInstance();
    BrowserContent newBrowserContent = new BrowserContent();
    Content browser = contentFactory.createContent(newBrowserContent.getContentPanel(),
            ChatGPTBundle.message("toolwindows.content.online"), false);
    contentManager.addContent(browser);
    contentManager.setSelectedContent(browser);
  }


}
