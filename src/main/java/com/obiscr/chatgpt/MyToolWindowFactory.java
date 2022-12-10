package com.obiscr.chatgpt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.ui.notifier.action.HomeAction;
import com.obiscr.chatgpt.ui.notifier.action.SettingAction;
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
    MyToolWindow myToolWindow = new MyToolWindow();
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(myToolWindow.getContent(), "Search", false);
    toolWindow.getContentManager().addContent(content);
    List<AnAction> actionList = new ArrayList<>();
    actionList.add(new HomeAction("Home",myToolWindow.getContentPanel()));
    actionList.add(new SettingAction("Settings"));
    toolWindow.setTitleActions(actionList);
  }

}
