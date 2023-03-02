package com.obiscr.chatgpt.core;

import com.obiscr.chatgpt.util.HtmlUtil;

/**
 * @author Wuzi
 */
public class Constant {

    public static final String CHATGPT_CONTENT =
                    "**Important tip**: \n\n<br />"+
                    "Currently using a third-party proxy service. will be unstable. And there is a limit on the number of requests per hour. <br />" +
                            "**Please don't give me bad reviews. I have been working hard to provide you with a better service.**\n\n<br />" +
                    "**Instructions**: \n\n<br />" +
                    "Open *File* - *Setting/Preference* , select *Tool* - *OpenAI* - *ChatGPT*. Enter your own OpenAI account in Official.\n\n<br />" +
                    "Click Login below to start logging in, and the Access Token below will be automatically refreshed after successful login.\n" +
                    "The expiration time of the current Token is displayed below the Access Token. After the expiration, you can click Login again to refresh the Access Token.\n\n";

    public static final String GPT35_TURBO_CONTENT =
            "**Important tip**: \n\n<br />"+
                    "This model is the official GPT 3.5 Turbo model. <br />" +
                    "**Instructions**: \n\n<br />" +
                    "At first, login to: [https://platform.openai.com/account/api-keys](https://platform.openai.com/account/api-keys). Then create an API Key.\n" +
                    "Open *File* - *Setting/Preference* , select *Tool* - *OpenAI* - *GPT 3.5 Turbo*. And fill the API Key in text field.";


    public static String getChatGPTContent() {
        return HtmlUtil.md2html(CHATGPT_CONTENT);
    }

    public static String getGpt35TurboContent() {
        return HtmlUtil.md2html(GPT35_TURBO_CONTENT);
    }
}
