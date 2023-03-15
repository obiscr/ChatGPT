package com.obiscr.chatgpt.util;

import cn.hutool.http.HttpUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.obiscr.chatgpt.settings.OpenAISettingsState;

import javax.swing.*;
import java.nio.charset.StandardCharsets;

import static com.obiscr.chatgpt.settings.OpenAISettingsPanel.CREATE_API_KEY;
import static com.obiscr.chatgpt.settings.OpenAISettingsPanel.FIND_GRANTS;

/**
 * @author Wuzi
 */
public class OpenAIUtil {

    public static void refreshGranted(String apiKey, JComponent component) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        try {
            String grants = HttpUtil.createGet(FIND_GRANTS).
                    header("Authorization", "Bearer " + apiKey).setProxy(state.getProxy()).
                    timeout(5000)
                    .execute().body();
            JsonObject object = JsonParser.parseString(grants).getAsJsonObject();
            if (object.keySet().contains("error")) {
                String errorMessage = object.get("error").getAsJsonObject().get("message").getAsString();
                MessageDialogBuilder.yesNo("Refresh Failed",
                                "Refresh grant failed, error: " + errorMessage)
                        .show();
                return;
            }
            if (!object.keySet().contains("total_used") || !object.keySet().contains("total_granted")) {
                MessageDialogBuilder.yesNo("Refresh Failed",
                                "Refresh grant failed, please try again later.")
                        .show();
                return;
            }
            double used = object.get("total_used").getAsDouble();
            double available = object.get("total_available").getAsDouble();
            double granted = object.get("total_granted").getAsDouble();
            String info = "Tips: Usage data may be delayed by up to 5 minutes.\n\n" +
                    "<b>Used</b>: " + used + "\n" +
                    "<b>Available</b>: " + available + "\n" +
                    "<b>Total</b>: " + granted;
            MessageDialogBuilder.okCancel("Usage info", info).ask(component);
        } catch (Exception e) {
            MessageDialogBuilder.yesNo("Refresh Failed",
                            "Refresh grant failed, error: " + e.getMessage())
                    .show();
        }
    }

    public static void createAPIKey(String apiKey, JComponent component) {
        JsonObject params = new JsonObject();
        params.addProperty("action", "create");
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        try {
            String grants = HttpUtil.createPost(CREATE_API_KEY).
                    header("Authorization", "Bearer " + apiKey).
                    header("Content-Type", "application/json")
                    .body(params.toString().getBytes(StandardCharsets.UTF_8))
                    .timeout(5000)
                    .setProxy(state.getProxy())
                    .execute().body();
            JsonObject object = JsonParser.parseString(grants).getAsJsonObject();
            if (object.keySet().contains("error")) {
                String errorMessage = object.get("error").getAsJsonObject().get("message").getAsString();
                MessageDialogBuilder.yesNo("Create API Key Failed",
                                "Refresh grant failed, error: " + errorMessage)
                        .show();
                return;
            }
            if (!object.keySet().contains("result") || !object.get("result").getAsString().equals("success")) {
                MessageDialogBuilder.yesNo("Create API Key Failed",
                                "Create API Key failed, please try again later.")
                        .show();
                return;
            }
            JsonObject key = object.get("key").getAsJsonObject();
            String newKey = key.get("sensitive_id").getAsString();
            boolean ask = MessageDialogBuilder.yesNo("Create API Key successful",
                            "Your API Key is: \n\n" + newKey + " \n\nThe API Key will only be displayed once, please record the API " +
                                    "Key immediately. Do you want to save it to GPT-3.5-Turbo?\n")
                    .yesText("Save it")
                    .noText("No, thanks").isYes();
            if(ask) {
                OpenAISettingsState.getInstance().apiKey = newKey;
            }
        } catch (Exception e) {
            MessageDialogBuilder.yesNo("Create API Key Failed",
                            "Create API Key failed, error: " + e.getMessage())
                    .show();
        }
    }
}
