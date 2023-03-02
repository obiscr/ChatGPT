package com.obiscr.chatgpt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.*;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.action.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuzi
 */
public class MyToolWindowFactory implements ToolWindowFactory {

    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");

    public static final String CHATGPT_CONTENT_NAME = "ChatGPT";
    public static final String GPT35_TRUBO_CONTENT_NAME = "GPT-3.5-Turbo";

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();

        ChatGPTToolWindow chatGPTToolWindow = new ChatGPTToolWindow(project);
        Content chatGpt = contentFactory.createContent(chatGPTToolWindow.getContent(), CHATGPT_CONTENT_NAME, false);
        chatGpt.setCloseable(false);
        toolWindow.getContentManager().addContent(chatGpt);
        project.putUserData(ACTIVE_CONTENT,chatGPTToolWindow.getPanel());

        GPT35TurboToolWindow gpt35TurboToolWindow = new GPT35TurboToolWindow(project);
        Content gpt35Turbo = contentFactory.createContent(gpt35TurboToolWindow.getContent(), GPT35_TRUBO_CONTENT_NAME, false);
        gpt35Turbo.setCloseable(false);
        toolWindow.getContentManager().addContent(gpt35Turbo);

        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                String displayName = event.getContent().getDisplayName();
                if (CHATGPT_CONTENT_NAME.equals(displayName)) {
                    project.putUserData(ACTIVE_CONTENT,chatGPTToolWindow.getPanel());
                } else if (GPT35_TRUBO_CONTENT_NAME.equals(displayName)) {
                    project.putUserData(ACTIVE_CONTENT,gpt35TurboToolWindow.getPanel());
                }
            }
        });

        List<AnAction> actionList = new ArrayList<>();
        actionList.add(new DocumentationAction());
        actionList.add(new SettingAction(ChatGPTBundle.message("action.settings")));
        actionList.add(new GitHubAction());
        actionList.add(new PluginAction());
        toolWindow.setTitleActions(actionList);
    }

}
