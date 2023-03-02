package com.obiscr.chatgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;


/**
 * @author Wuzi
 */
public class GitHubAction extends DumbAwareAction {

    public GitHubAction() {
        super(() -> "Wuzi's GitHub", AllIcons.Vcs.Vendors.Github);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("https://github.com/obiscr");
    }
}
