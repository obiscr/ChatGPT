package com.obiscr.chatgpt.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.settings.ChatGPTSettingsPanel;
import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.notifier.MyNotifier;
import com.obiscr.chatgpt.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Wuzi
 */
public class TokenManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenManager.class);

    private static final TokenManager INSTANCE = new TokenManager();

    private TokenManager() {

    }

    public static TokenManager getInstance() {
        return INSTANCE;
    }

    public void autoRefreshToken() {
        String url = "https://chat.openai.com/api/auth/session";
        try {
            String s = HttpUtil.get(url);
            JSONObject jsonObject = JSON.parseObject(s);
            String token = jsonObject.getJSONObject("user").getString("accessToken");
            if (token == null || token.isEmpty()) {
                return;
            }
            SettingsState settings = SettingsState.getInstance();
            settings.accessToken = token;
            settings.loadState(settings);
        } catch (Exception e) {
            LOG.error("ChatGPT refreshToken error: {}", e.getMessage());
            MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                    "ChatGPT: Auto refresh token failed", "Please manually refresh the Token");
        }
    }

    public void manuallyRefreshToken(Project project) {
        BrowserUtil.browse("https://chat.openai.com/api/auth/session");
        ShowSettingsUtil.getInstance().showSettingsDialog(project, ChatGPTSettingsPanel.class);
    }
}
