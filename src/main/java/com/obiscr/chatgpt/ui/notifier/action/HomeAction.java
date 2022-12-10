package com.obiscr.chatgpt.ui.notifier.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.obiscr.chatgpt.core.Constant;
import com.obiscr.chatgpt.settings.ChatGPTSettingsPanel;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class HomeAction extends DumbAwareAction {

  private final MarkdownJCEFHtmlPanel contentPanel;
  public HomeAction(@NotNull @Nls String text, MarkdownJCEFHtmlPanel contentPanel) {
    super(() -> text,AllIcons.Nodes.HomeFolder);
    this.contentPanel = contentPanel;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    String s = HtmlUtil.md2html(Constant.HOME_CONTENT);
    contentPanel.setHtml(s,0);
  }
}
