package com.obiscr.chatgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.awt.RelativePoint;
//import com.obiscr.chatgpt.core.DataFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuzi
 */
public class HelpAction extends DumbAwareAction {

  public HelpAction() {
    super(() -> "Documentation",AllIcons.Actions.Help);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ListPopup actionGroupPopup = JBPopupFactory.getInstance().createActionGroupPopup("Helps",
            new HelpActionGroup(), e.getDataContext(), true, null, Integer.MAX_VALUE);
    MouseEvent source = (MouseEvent) e.getInputEvent();
    actionGroupPopup.show(new RelativePoint(source));
  }

  static class HelpActionGroup extends ActionGroup {
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
      List<AnAction> actions = new ArrayList<>();
      actions.add(new Announcement());
      actions.add(new Separator());
      actions.add(new DocumentAction());
      return actions.toArray(new AnAction[0]);
    }
  }

  static class DocumentAction extends DumbAwareAction {
    public DocumentAction() {
      super(() -> "Documents",AllIcons.Toolwindows.Documentation);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      BrowserUtil.browse("https://chatgpt.en.obiscr.com");
    }
  }

  static class Announcement extends DumbAwareAction {
    public Announcement() {
      super(() -> "Announcement",AllIcons.General.InspectionsEye);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      BrowserUtil.browse("https://github.com/obiscr/ChatGPT/discussions/categories/announcements");
    }
  }

}
