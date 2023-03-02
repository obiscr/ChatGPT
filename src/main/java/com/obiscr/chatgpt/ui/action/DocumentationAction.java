package com.obiscr.chatgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.obiscr.chatgpt.core.Constant;
//import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.core.ConversationManager;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class DocumentationAction extends DumbAwareAction {

  public DocumentationAction() {
    super(() -> "Documentation",AllIcons.Toolwindows.Documentation);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    BrowserUtil.browse("https://chatgpt.en.obiscr.com/");
  }


}
