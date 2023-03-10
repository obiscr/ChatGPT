package com.obiscr.chatgpt.core;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.obiscr.chatgpt.ChatGPTHandler;
import com.obiscr.chatgpt.GPT35TurboHandler;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.MessageComponent;
import com.obiscr.chatgpt.ui.MessageGroupComponent;
import com.obiscr.chatgpt.util.StringUtil;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static com.obiscr.chatgpt.MyToolWindowFactory.ACTIVE_CONTENT;

/**
 * @author Wuzi
 */
public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(ACTIVE_CONTENT);
        doActionPerformed((MainPanel) mainPanel, data);
    }

    private boolean presetCheck(boolean isChatGPTModel) {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (isChatGPTModel) {
            if (instance.urlType == SettingConfiguration.SettingURLType.OFFICIAL) {
                if (StringUtil.isEmpty(instance.accessToken)){
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Wrong setting",
                                    "Please configure the access token or login in at first.\n" +
                                            "Open Setting/Preference - Tools - OpenAI - ChatGPT, and login.",
                                    NotificationType.ERROR));
                    return false;
                }
            } else if (instance.urlType == SettingConfiguration.SettingURLType.CUSTOMIZE) {
                if (StringUtil.isEmpty(instance.customizeUrl)) {
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Wrong setting",
                                    "Please configure a Customize URL first.",
                                    NotificationType.ERROR));
                    return false;
                }
            }
        } else {
            if (StringUtil.isEmpty(instance.apiKey)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure a API Key first.",
                                NotificationType.ERROR));
                return false;
            }
        }
        return true;
    }

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
            return;
        }

        // Check the configuration first
        if (!presetCheck(mainPanel.isChatGPTModel())) {
            return;
        }

        // Reset the question container
        mainPanel.getSearchTextArea().getTextArea().setText("");
        mainPanel.aroundRequest(true);
        Project project = mainPanel.getProject();
        ChatGPTHandler chatGPTHandler = project.getService(ChatGPTHandler.class);
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        // Add the message component to container
        MessageComponent question = new MessageComponent(data,true);
        MessageComponent answer = new MessageComponent("Waiting for response...",false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = mainPanel.getExecutorService();
            // Request the server.
            if (mainPanel.isChatGPTModel()) {
                executorService.submit(() -> {
                    EventSource handle = chatGPTHandler.handle(mainPanel, answer, data);
                    mainPanel.setRequestHolder(handle);
                    contentPanel.updateLayout();
                    contentPanel.scrollToBottom();
                });
            } else {
                executorService.submit(() -> {
                    new GPT35TurboHandler().handle(mainPanel, answer, data);
                    contentPanel.updateLayout();
                    contentPanel.scrollToBottom();
                });
                // Because this Http request is blocked, it cannot be placed in Runnable
                // So it needs to be executed outside Runnable
                mainPanel.setRequestHolder(answer);
            }
        } catch (Exception e) {
            mainPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }
}
