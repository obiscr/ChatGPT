package com.obiscr.chatgpt;

import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


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
}
