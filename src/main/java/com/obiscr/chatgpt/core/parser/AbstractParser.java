package com.obiscr.chatgpt.core.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

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
        return sb.toString();
    }
}
