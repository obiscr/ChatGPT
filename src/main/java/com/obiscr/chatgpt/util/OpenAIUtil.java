package com.obiscr.chatgpt.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.ui.components.JBTextField;
import com.obiscr.OpenAIProxy;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.settings.SettingConfiguration;

import javax.swing.*;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;

import static com.obiscr.chatgpt.settings.OpenAISettingsPanel.CREATE_API_KEY;
import static com.obiscr.chatgpt.settings.OpenAISettingsPanel.FIND_GRANTS;

/**
 * @author Wuzi
 */
public class OpenAIUtil {

    public static void refreshGranted(String apiKey, JComponent component,
                                      JTextField usedField,JTextField availableField,
                                      JTextField grantField) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        try {
            String grants = HttpUtil.createGet(FIND_GRANTS).
                    header("Authorization", "Bearer " + apiKey).setProxy(state.getProxy()).
                    timeout(5000)
                    .execute().body();
            JSONObject object = JSON.parseObject(grants);
            if (object.containsKey("error")) {
                String errorMessage = object.getJSONObject("error").getString("message");
                MessageDialogBuilder.okCancel("Refresh Failed",
                                "Refresh grant failed, error: " + errorMessage)
                        .ask(component);
                return;
            }
            if (!object.containsKey("total_used") || !object.containsKey("total_granted")) {
                MessageDialogBuilder.okCancel("Refresh Failed",
                                "Refresh grant failed, please try again later.")
                        .ask(component);
                return;
            }
            Double used = object.getDouble("total_used");
            Double available = object.getDouble("total_available");
            Double granted = object.getDouble("total_granted");
            usedField.setText(String.valueOf(used));
            availableField.setText(String.valueOf(available));
            grantField.setText(String.valueOf(granted));
        } catch (Exception e) {
            MessageDialogBuilder.okCancel("Refresh Failed",
                            "Refresh grant failed, error: " + e.getMessage())
                    .ask(component);
        }
    }

    public static void createAPIKey(String apiKey, JComponent component) {
        JSONObject params = new JSONObject();
        params.put("action", "create");
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        try {
            String grants = HttpUtil.createPost(CREATE_API_KEY).
                    header("Authorization", "Bearer " + apiKey).
                    header("Content-Type", "application/json")
                    .body(params.toJSONString().getBytes(StandardCharsets.UTF_8))
                    .timeout(5000)
                    .setProxy(state.getProxy())
                    .execute().body();
            JSONObject object = JSON.parseObject(grants);
            if (object.containsKey("error")) {
                String errorMessage = object.getJSONObject("error").getString("message");
                MessageDialogBuilder.okCancel("Create API Key Failed",
                                "Refresh grant failed, error: " + errorMessage)
                        .ask(component);
                return;
            }
            if (!object.containsKey("result") || !object.getString("result").equals("success")) {
                MessageDialogBuilder.okCancel("Create API Key Failed",
                                "Create API Key failed, please try again later.")
                        .ask(component);
                return;
            }
            JSONObject key = object.getJSONObject("key");
            String newKey = key.getString("sensitive_id");
            boolean ask = MessageDialogBuilder.yesNo("Create API Key successful",
                            "Your API Key is: \n\n" + newKey + " \n\nThe API Key will only be displayed once, please record the API " +
                                    "Key immediately. Do you want to save it to GPT-3.5-Turbo?\n")
                    .yesText("Save it")
                    .noText("No, thanks").ask(component);
            if(ask) {
                OpenAISettingsState.getInstance().apiKey = newKey;
            }
        } catch (Exception e) {
            MessageDialogBuilder.okCancel("Create API Key Failed",
                            "Create API Key failed, error: " + e.getMessage())
                    .ask(component);
        }
    }
}
