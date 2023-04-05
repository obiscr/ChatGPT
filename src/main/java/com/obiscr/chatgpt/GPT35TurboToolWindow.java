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
public class GPT35TurboToolWindow {

  private final MainPanel panel;

  public GPT35TurboToolWindow(@NotNull Project project) {
    panel = new MainPanel(project, false);
  }

  public JPanel getContent() {
    return panel.init();
  }

  public MainPanel getPanel() {
    return panel;
  }

/**
 * rapidly get input focus by keystorke f key
 */
  public void registerKeystrokeFocus(){
    MyUIUtil.registerKeystrokeFocusForInput(panel.getSearchTextArea().getTextArea());
  }
}
