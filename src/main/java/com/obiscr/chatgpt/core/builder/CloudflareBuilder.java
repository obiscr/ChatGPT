package com.obiscr.chatgpt.core.builder;

import com.alibaba.fastjson2.JSONObject;

import java.util.UUID;

/**
 * @author Wuzi
 */
public class CloudflareBuilder {

    public static JSONObject build(String text) {
        JSONObject result = new JSONObject();
        result.put("id", UUID.randomUUID());
        result.put("message", text);
        result.put("message_id",UUID.randomUUID());
        return result;
    }
}
