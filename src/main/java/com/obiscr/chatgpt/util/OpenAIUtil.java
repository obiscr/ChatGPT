package com.obiscr.chatgpt.util;

import cn.hutool.http.HttpUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.ide.BrowserUtil;
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

    public static void refreshGranted(JComponent component) {
        boolean confirm = MessageDialogBuilder.okCancel("Refresh Tips",
                        "In recent days, OpenAI has banned usage restrictions via API queries. \n" +
                                "Does it open a browser for you to make a query? (Login is required first)")
                .ask(component);
        if (confirm) {
            BrowserUtil.browse("https://platform.openai.com/account/usage");
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
                MessageDialogBuilder.okCancel("Create API Key Failed",
                                "Refresh grant failed, error: " + errorMessage)
                        .ask(component);
                return;
            }
            if (!object.keySet().contains("result") || !object.get("result").getAsString().equals("success")) {
                MessageDialogBuilder.okCancel("Create API Key Failed",
                                "Create API Key failed, please try again later.")
                        .ask(component);
                return;
            }
            JsonObject key = object.get("key").getAsJsonObject();
            String newKey = key.get("sensitive_id").getAsString();
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
