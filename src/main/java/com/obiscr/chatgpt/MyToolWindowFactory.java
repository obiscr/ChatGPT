package com.obiscr.chatgpt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.action.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Wuzi
 */
public class MyToolWindowFactory implements ToolWindowFactory {

    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");

    public static final String CHATGPT_CONTENT_NAME = "ChatGPT";
    public static final String GPT35_TRUBO_CONTENT_NAME = "GPT-3.5-Turbo";
    public static final String ONLINE_CHATGPT_CONTENT_NAME = "Online ChatGPT";

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

        GPT35TurboToolWindow gpt35TurboToolWindow = new GPT35TurboToolWindow(project);
        Content gpt35Turbo = contentFactory.createContent(gpt35TurboToolWindow.getContent(), GPT35_TRUBO_CONTENT_NAME, false);
        gpt35Turbo.setCloseable(false);

        BrowserToolWindow browserToolWindow = new BrowserToolWindow();
        Content browser = contentFactory.createContent(browserToolWindow.getContent(),
                ONLINE_CHATGPT_CONTENT_NAME, false);
        browser.setCloseable(false);

        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        Map<Integer, String> contentSort = settingsState.contentOrder;

        for (int i = 0 ; i <= 2 ; i++) {
            toolWindow.getContentManager().addContent(getContent(contentSort.get(i + 1),chatGpt,
                    gpt35Turbo,browser), i);
        }


        // Set the default component. It require the 1st container
        String firstContentName = contentSort.get(1);
        switch (firstContentName) {
            case CHATGPT_CONTENT_NAME:
                project.putUserData(ACTIVE_CONTENT, chatGPTToolWindow.getPanel());
                break;
            case GPT35_TRUBO_CONTENT_NAME:
                project.putUserData(ACTIVE_CONTENT, gpt35TurboToolWindow.getPanel());
                break;
            case ONLINE_CHATGPT_CONTENT_NAME:
                project.putUserData(ACTIVE_CONTENT, browserToolWindow.getPanel());
                break;
            default:
                throw new RuntimeException("Error content name, content name must be one of ChatGPT, GPT-3.5-Turbo, Online ChatGPT");
        }


        // Add the selection listener
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                String displayName = event.getContent().getDisplayName();
                if (CHATGPT_CONTENT_NAME.equals(displayName)) {
                    project.putUserData(ACTIVE_CONTENT,chatGPTToolWindow.getPanel());
                } else if (GPT35_TRUBO_CONTENT_NAME.equals(displayName)) {
                    project.putUserData(ACTIVE_CONTENT,gpt35TurboToolWindow.getPanel());
                } else if (ONLINE_CHATGPT_CONTENT_NAME.equals(displayName)) {
                    project.putUserData(ACTIVE_CONTENT,browserToolWindow.getPanel());
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

    private Content getContent(String key, Content chatgpt ,
                                 Content gpt35Turbo,
                                 Content browser) {
        if (CHATGPT_CONTENT_NAME.equals(key)) {
            return chatgpt;
        } else if (GPT35_TRUBO_CONTENT_NAME.equals(key)) {
            return gpt35Turbo;
        } else if (ONLINE_CHATGPT_CONTENT_NAME.equals(key)) {
            return browser;
        }
        return null;
    }
}
