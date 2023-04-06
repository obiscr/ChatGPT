package com.obiscr.chatgpt.core;

import com.intellij.openapi.application.ApplicationManager;

import com.obiscr.chatgpt.settings.OpenAISettingsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
