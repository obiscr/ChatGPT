package com.obiscr.chatgpt.core.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.ConversationManager;
import com.obiscr.chatgpt.ui.MessageComponent;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class OfficialParser {
    private static final String PREFIX = "data: ";
    private static final String DETAIL = "detail";
    private static final String DONE = "[DONE]";

    public static ParseResult parseChatGPT(@NotNull Project project, MessageComponent component, String response) {
        JSONObject jsonObject = JSON.parseObject(response);
        // Handler the error info from the proxy server.
        if (jsonObject.containsKey("detail")) {
            String detail = jsonObject.getString("detail");
            component.setSourceContent(detail);
            component.setContent(detail);
            return null;
        }
        JSONArray partsArray = jsonObject.getJSONObject("message")
                .getJSONObject("content")
                .getJSONArray("parts");
        String conversationId = jsonObject.getString("conversation_id");
        String parentId = (jsonObject.getJSONObject("message")).getString("id");
        ConversationManager.getInstance(project).setParentMessageId(parentId);
        ConversationManager.getInstance(project).setConversationId(conversationId);

        if (partsArray.size() == 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0 ; i < partsArray.size() ; i++) {
            result.append(partsArray.getString(i));
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = HtmlUtil.md2html(result.toString());
        parseResult.html = HtmlUtil.md2html(result.toString());
        return parseResult;
    }

    public static ParseResult parseGPT35Turbo(String response) {
        JSONObject object = JSON.parseObject(response);
        StringBuilder result = new StringBuilder();
        JSONArray resultArray = object.getJSONArray("choices");
        for (Object s : resultArray) {
            JSONObject choice = JSON.parseObject(s.toString());
            JSONObject messages  = choice.getJSONObject("message");
            String content = JSON.parseObject(messages.toString()).getString("content");
            result.append(content);
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = HtmlUtil.md2html(result.toString());
        parseResult.html = HtmlUtil.md2html(result.toString());
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
