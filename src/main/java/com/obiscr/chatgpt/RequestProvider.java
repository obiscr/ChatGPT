package com.obiscr.chatgpt;

import com.alibaba.fastjson2.JSON;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.TokenManager;
import com.obiscr.chatgpt.core.builder.OfficialBuilder;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Wuzi
 */
public class RequestProvider {

    public static final String OFFICIAL_CONVERSATION_URL = "https://chatgpt.duti.tech/api/conversation";
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
                provider.url = instance.customizeUrl;
            } else {
                provider.url = OFFICIAL_CONVERSATION_URL;
            }
            provider.header = TokenManager.getInstance().getChatGPTHeaders();
            provider.data = JSON.toJSONString(OfficialBuilder.buildChatGPT(myProject,question));
        } else {
            provider.url = "https://api.openai.com/v1/chat/completions";
            provider.header = TokenManager.getInstance().getGPT35TurboHeaders();
            provider.data = JSON.toJSONString(OfficialBuilder.buildGpt35Turbo(question));
        }
        return provider;
    }
}
