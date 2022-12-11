package com.obiscr.chatgpt.core.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.obiscr.chatgpt.core.DataFactory;

/**
 * @author Wuzi
 */
public abstract class AbstractParser implements SseParser{

    @Override
    public String parse(String response) {
        JSONObject object = JSON.parseObject(response);
        JSONArray resultArray = object.getJSONObject("message").getJSONObject("content").getJSONArray("parts");
        StringBuilder sb = new StringBuilder();
        for (Object s : resultArray) {
            sb.append(s.toString());
        }
        // Store the conversation_id
        String conversationId = object.getString("conversation_id");
        DataFactory.getInstance().setConversationId(conversationId);
        return sb.toString();
    }
}
