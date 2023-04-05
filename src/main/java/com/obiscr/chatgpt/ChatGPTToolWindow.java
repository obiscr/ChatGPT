package com.obiscr.chatgpt;

import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.util.MyUIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/**
 * @author Wuzi
 */
public class ChatGPTToolWindow {

  private final MainPanel panel;

  public ChatGPTToolWindow(@NotNull Project project) {
    panel = new MainPanel(project, true);
  }

  public JPanel getContent() {
    return panel.init();
  }

  public MainPanel getPanel() {
    return panel;
  }

  public void registerKeystrokeFocus(){
    MyUIUtil.registerKeystrokeFocusForInput(panel.getSearchTextArea().getTextArea());
  }
}
