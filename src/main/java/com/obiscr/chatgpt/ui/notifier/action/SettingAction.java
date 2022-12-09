package com.obiscr.chatgpt.ui.notifier.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.obiscr.chatgpt.settings.SettingsConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class SettingAction extends DumbAwareAction {

  public SettingAction(@NotNull @Nls String text) {
    super(() -> text,AllIcons.General.Settings);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), SettingsConfigurable.class);
  }
}
