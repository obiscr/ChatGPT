package com.obiscr.chatgpt.core;

import com.alibaba.fastjson2.JSON;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.ProjectManager;
import com.obiscr.OpenAIAuth;
import com.obiscr.OpenAIProxy;
import com.obiscr.OpenAISession;

import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Wuzi
 */
public class TokenManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenManager.class);
    private final Map<String,String> headers = new HashMap<>();
    private OpenAISettingsState settings = OpenAISettingsState.getInstance();
    public static TokenManager getInstance() {
        return ApplicationManager.getApplication().getService(TokenManager.class);
    }

    public Map<String, String> getChatGPTHeaders() {
        headers.put("Accept","text/event-stream");
        headers.put("Authorization","Bearer " + settings.accessToken);
        headers.put("Content-Type","application/json");
        headers.put("X-Openai-Assistant-App-Id","");
        headers.put("Connection","close");
        headers.put("Accept-Language","en-US,en;q=0.9");
        headers.put("Referer","https://chat.openai.com/chat");
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15");
        return headers;
    }

    public Map<String, String> getGPT35TurboHeaders() {
        headers.put("Authorization","Bearer " + settings.apiKey);
        headers.put("Content-Type","application/json");
        return headers;
    }

    public String refreshToken(String email, String password) {
        String title = StringUtil.isNotEmpty(email) && StringUtil.isNotEmpty(password) ?
                "OpenAI: Login" :
                "OpenAI: Refresh Access Token";
        String refreshStatus;
        try {
            refreshStatus =  ProgressManager.getInstance().run(new Task.WithResult<String, Exception>(
                    ProjectManager.getInstance().getDefaultProject(),
                    title, false) {
                @Override
                protected String compute(@NotNull ProgressIndicator indicator) {
                    return doRefreshToken(email, password);
                }
            });
        } catch (Exception e) {
            LOG.error("ChatGPT: refreshToken failed, message: {}", e.getMessage());
            refreshStatus = "Login or refresh token failed, please try it later. Use a proxy if necessary.";
        }
        return refreshStatus;
    }

    public void refreshTokenAsync() {
        doRefreshToken(null,null);
    }

    public String doRefreshToken(String email, String password) {
        if (StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) {
            if (StringUtil.isEmpty(settings.email) || StringUtil.isEmpty(settings.password)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "No login details provided!",
                                "To refresh access token, the email and password are required, " +
                                        "please configure it at first.",
                                NotificationType.ERROR));
                LOG.error("No login details provided! To refresh access token, the email and password are required, please configure it at first.");
                return "No login details provided! To login or refresh access token, the email and password are required, please configure it at first.";
            } else {
                email = settings.email;
                password = settings.password;
            }
        }
        OpenAIAuth auth;
        if (settings.enableProxy) {
            Proxy.Type type = settings.proxyType ==
                    SettingConfiguration.SettingProxyType.HTTP ? Proxy.Type.HTTP :
                    settings.proxyType == SettingConfiguration.SettingProxyType.SOCKS ? Proxy.Type.SOCKS :
                            Proxy.Type.DIRECT;
            Proxy proxy = new OpenAIProxy(settings.proxyHostname, Integer.parseInt(settings.proxyPort),
                    type).build();
            auth = new OpenAIAuth(email, password, proxy);
        } else {
            auth = new OpenAIAuth(email, password);
        }
        try {
            OpenAISession sessions = auth.auth();
            settings.expireTime = sessions.getExpires();
            settings.accessToken = sessions.getAccessToken();
            String image = URLDecoder.decode(sessions.getUser().getImage(), StandardCharsets.UTF_8);
            settings.imageUrl = image.substring(image.lastIndexOf("=") + 1);
            LOG.info(JSON.toJSONString(sessions));
            return "success";
        } catch (Exception e) {
            Notifications.Bus.notify(
                    new Notification(ChatGPTBundle.message("group.id"),
                            "Refresh access token failed",
                            "Refresh access token failed, please try it later.",
                            NotificationType.ERROR));
            LOG.error("ChatGPT: Refresh access token failed, error = {}", e.getMessage());
            return "Refresh access token failed, please try it later.";
        }
    }

}
