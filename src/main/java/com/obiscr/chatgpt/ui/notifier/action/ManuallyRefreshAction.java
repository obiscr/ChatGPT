package com.obiscr.chatgpt.ui.notifier.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.obiscr.chatgpt.core.TokenManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ManuallyRefreshAction extends DumbAwareAction {

  public ManuallyRefreshAction(@NotNull @Nls String text) {
    super(text);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    TokenManager.getInstance().manuallyRefreshToken(e.getProject());
  }
}
