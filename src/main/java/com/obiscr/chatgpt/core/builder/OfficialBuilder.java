package com.obiscr.chatgpt.core.builder;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.ConversationManager;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.MessageGroupComponent;
import com.obiscr.chatgpt.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Wuzi
 */
public class OfficialBuilder {

    public static JSONObject buildChatGPT(@NotNull Project project, String text) {
        JSONObject result = new JSONObject();
        result.put("action","next");

        JSONArray messages = new JSONArray();
        JSONObject message0 = new JSONObject();
        message0.put("id", UUID.randomUUID());
        message0.put("role", "user");

        JSONObject content = new JSONObject();
        content.put("content_type","text");
        JSONArray parts = new JSONArray();
        parts.add(text);
        content.put("parts",parts);

        JSONObject author = new JSONObject();
        author.put("role", "user");
        message0.put("content", content);
        message0.put("author", author);
        messages.add(message0);
        result.put("messages", messages);

        result.put("parent_message_id", ConversationManager.getInstance(project).getParentMessageId());
        String conversationId = ConversationManager.getInstance(project).getConversationId();
        if (StringUtil.isNotEmpty(conversationId)) {
            result.put("conversation_id",conversationId);
        }
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.put("model",settingsState.chatGptModel);
        return result;
    }

    public static JSONObject buildGpt35Turbo(String text) {
        JSONObject result = new JSONObject();
        result.put("model","gpt-3.5-turbo");
        JSONArray messages = new JSONArray();
        JSONObject message0 = new JSONObject();
        message0.put("role","user");
        message0.put("content",text);
        messages.add(message0);
        result.put("messages",messages);
        return result;
    }

    public static JSONObject buildGpt35Turbo(String text, MessageGroupComponent component) {
        JSONObject result = new JSONObject();
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.put("model",settingsState.gpt35Model);
        component.getMessages().add(userMessage(text));
        result.put("messages",component.getMessages());
        return result;
    }

    private static JSONObject message(String role, String text) {
        JSONObject message = new JSONObject();
        message.put("role",role);
        message.put("content",text);
        return message;
    }

    public static JSONObject userMessage(String text) {
        return message("user",text);
    }

    public static JSONObject systemMessage(String text) {
        return message("system",text);
    }

    public static JSONObject assistantMessage(String text) {
        return message("assistant",text);
    }
}
