package com.obiscr.chatgpt.core.builder;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.UUID;

/**
 * @author Wuzi
 */
public class OfficialBuilder {

    public static JSONObject build(String text) {
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
        message0.put("content", content);
        messages.add(message0);
        result.put("messages", messages);
        result.put("parent_message_id", UUID.randomUUID());
        result.put("model","text-davinci-002-render");
        return result;
    }
}
