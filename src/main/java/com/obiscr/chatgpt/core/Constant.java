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
                    "**Getting Started**: \n\n<br />" +
                    "Please following this to configure the ChatGPT: [https://chatgpt.en.obiscr.com/settings/chatgpt-settings/](https://chatgpt.en.obiscr.com/settings/chatgpt-settings/) \n\n<br />";

    public static final String GPT35_TURBO_CONTENT =
            "**Important tip**: \n\n<br />"+
                    "This model is the official GPT 3.5 Turbo model. \n\n<br />" +
                    "**Instructions**: \n\n<br />" +
                    "Please following this to configure the GPT 3.5 Turbo: [https://chatgpt.en.obiscr.com/settings/gpt-3.5-trubo-settings/](https://chatgpt.en.obiscr.com/settings/gpt-3.5-trubo-settings/) \n\n<br />";


    public static String getChatGPTContent() {
        return HtmlUtil.md2html(CHATGPT_CONTENT);
    }

    public static String getGpt35TurboContent() {
        return HtmlUtil.md2html(GPT35_TURBO_CONTENT);
    }
}
