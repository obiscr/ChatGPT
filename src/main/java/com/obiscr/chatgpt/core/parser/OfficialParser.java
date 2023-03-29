package com.obiscr.chatgpt.core.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.ConversationManager;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.MessageComponent;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * @author Wuzi
 */
public class OfficialParser {
    private static final String PREFIX = "data: ";
    private static final String DETAIL = "detail";
    private static final String DONE = "[DONE]";

    public static ParseResult parseChatGPT(@NotNull Project project, MessageComponent component, String response) {
        response = new String(response.getBytes(StandardCharsets.UTF_8));
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        // Handler the error info from the proxy server.
        if (object.keySet().contains("detail")) {
            String detail = object.get("detail").getAsString();
            component.setSourceContent(detail);
            component.setContent(detail);
            return null;
        }
        JsonArray partsArray = object.get("message").getAsJsonObject()
                .get("content").getAsJsonObject()
                .get("parts").getAsJsonArray();
        String conversationId = object.get("conversation_id").getAsString();
        String parentId = (object.get("message").getAsJsonObject()).get("id").getAsString();
        ConversationManager.getInstance(project).setParentMessageId(parentId);
        ConversationManager.getInstance(project).setConversationId(conversationId);

        if (partsArray.size() == 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0 ; i < partsArray.size() ; i++) {
            result.append(partsArray.get(i).getAsString());
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = result.toString();
        parseResult.html = HtmlUtil.md2html(result.toString());
        return parseResult;
    }

    public static ParseResult parseGPT35Turbo(String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = object.get("choices").getAsJsonArray();
        StringBuilder result = new StringBuilder();
        for (JsonElement element : choices) {
            JsonObject messages = element.getAsJsonObject().get("message").getAsJsonObject();
            String content = messages.get("content").getAsString();
            result.append(content);
        }

        OpenAISettingsState state = OpenAISettingsState.getInstance();
        StringBuilder usageResult = new StringBuilder(result);
        if (state.enableTokenConsumption) {
            JsonObject usage = object.get("usage").getAsJsonObject();
            usageResult.append("<br /><br />");
            usageResult.append("*");
            usageResult.
                    append("Prompt tokens: ").append("<b>").append(usage.get("prompt_tokens").getAsInt()).append("</b>").append(", ").
                    append("Completion tokens: ").append("<b>").append(usage.get("completion_tokens").getAsInt()).append("</b>").append(", ").
                    append("Total tokens: ").append("<b>").append(usage.get("total_tokens").getAsInt()).append("</b>");
            usageResult.append("*");
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = result.toString();
        parseResult.html = HtmlUtil.md2html(usageResult.toString());
        return parseResult;
    }

    public static ParseResult parseGPT35TurboWithStream(MessageComponent component, String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = object.get("choices").getAsJsonArray();
        StringBuilder result = new StringBuilder();
        for (JsonElement element : choices) {
            JsonObject messages = element.getAsJsonObject().get("delta").getAsJsonObject();
            if (!messages.keySet().contains("content")) {
                continue;
            }
            String content = messages.get("content").getAsString();
            result.append(content);
            component.getAnswers().add(result.toString());
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = component.prevAnswers();
        parseResult.html = HtmlUtil.md2html(component.prevAnswers());
        return parseResult;
    }

    public static class ParseResult {
        private String source;
        private String html;

        public String getSource() {
            return source;
        }

        public String getHtml() {
            return html;
        }
    }

}
