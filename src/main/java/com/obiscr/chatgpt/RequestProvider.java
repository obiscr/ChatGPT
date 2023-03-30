package com.obiscr.chatgpt;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.TokenManager;
import com.obiscr.chatgpt.core.builder.OfficialBuilder;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.util.StringUtil;

import java.util.Map;

/**
 * @author Wuzi
 */
public class RequestProvider {

    public static final String OFFICIAL_CONVERSATION_URL = "https://bypass.duti.tech/api/conversation";
    public static final String OFFICIAL_GPT35_TURBO_URL = "https://api.openai.com/v1/chat/completions";
    private Project myProject;
    private String url;
    private String data;
    private Map<String, String> header;

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public RequestProvider create(MainPanel mainPanel, String question) {
        myProject = mainPanel.getProject();
        RequestProvider provider = new RequestProvider();

        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (mainPanel.isChatGPTModel()) {
            if (instance.urlType == SettingConfiguration.SettingURLType.CUSTOMIZE) {
                if (StringUtil.isEmpty(instance.gpt35TurboUrl)) {
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Error settings",
                                    "You currently have a custom ChatGPT Server enabled, \n" +
                                            "but do not have the correct server address set. \n" +
                                            "The default configuration will be used.",
                                    NotificationType.WARNING));
                    provider.url = OFFICIAL_CONVERSATION_URL;
                } else {
                    provider.url = instance.gpt35TurboUrl;
                }
            } else {
                provider.url = OFFICIAL_CONVERSATION_URL;
            }
            provider.header = TokenManager.getInstance().getChatGPTHeaders();
            provider.data = OfficialBuilder.buildChatGPT(myProject,question).toString();
        } else {
            if (instance.enableCustomizeGpt35TurboUrl) {
                if (StringUtil.isEmpty(instance.gpt35TurboUrl)) {
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Error settings",
                                    "You currently have a custom GPT 3.5 Turbo Server enabled, \n" +
                                            "but do not have the correct server address set. \n" +
                                            "The default configuration will be used.",
                                    NotificationType.WARNING));
                    provider.url = OFFICIAL_GPT35_TURBO_URL;
                } else {
                    provider.url = instance.gpt35TurboUrl;
                }
            } else {
                provider.url = OFFICIAL_GPT35_TURBO_URL;
            }
            provider.header = TokenManager.getInstance().getGPT35TurboHeaders();
            if (instance.enableContext) {
                provider.data = OfficialBuilder.buildGpt35Turbo(question,mainPanel.getContentPanel()).toString();
            } else {
                provider.data = OfficialBuilder.buildGpt35Turbo(question).toString();
            }
        }
        return provider;
    }
}
