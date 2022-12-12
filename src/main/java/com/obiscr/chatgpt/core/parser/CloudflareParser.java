package com.obiscr.chatgpt.core.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.notifier.MyNotifier;

import static com.obiscr.chatgpt.util.HttpUtil.EMPTY_RESPONSE;

/**
 * @author Wuzi
 */
public class CloudflareParser extends AbstractParser {
    @Override
    public String parse(String response) {
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.containsKey("detail")) {
            JSONObject detail = jsonObject.getJSONObject("detail");
            if ("token_expired".equals(detail.getString("code"))) {
                MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.token_expired.error.title"),
                        ChatGPTBundle.message("notify.token_expired.error.text"));
            }
            return EMPTY_RESPONSE;
        }
        return super.parse(response);
    }
}
