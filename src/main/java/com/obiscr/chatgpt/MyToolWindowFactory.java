package com.obiscr.chatgpt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.BrowserContent;
import com.obiscr.chatgpt.ui.action.BrowserAction;
import com.obiscr.chatgpt.ui.action.RefreshAction;
import com.obiscr.chatgpt.ui.action.SettingAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuzi
 */
public class MyToolWindowFactory implements ToolWindowFactory {

  /**
   * Create the tool window content.
   *
   * @param project    current project
   * @param toolWindow current tool window
   */
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    DataFactory.getInstance().setData(project, toolWindow);
    ContentFactory contentFactory = ContentFactory.getInstance();

    MyToolWindow myToolWindow = new MyToolWindow();
    Content content = contentFactory.createContent(myToolWindow.getContent(), ChatGPTBundle.message("toolwindows.content.search"), false);
    content.setCloseable(false);
    BrowserContent browserContent = new BrowserContent();
    Content browser = contentFactory.createContent(browserContent.getContentPanel(), ChatGPTBundle.message("toolwindows.content.online"), false);

    toolWindow.getContentManager().addContent(content);
    toolWindow.getContentManager().addContent(browser);
    List<AnAction> actionList = new ArrayList<>();
    actionList.add(new RefreshAction(ChatGPTBundle.message("action.refresh"),myToolWindow.getContentPanel()));
    actionList.add(new SettingAction(ChatGPTBundle.message("action.settings")));
    actionList.add(new BrowserAction(ChatGPTBundle.message("action.open_online_chatgpt")));
    toolWindow.setTitleActions(actionList);
  }

}
