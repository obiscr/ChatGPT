package com.obiscr.chatgpt.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author Wuzi
 */
public class StringUtil extends com.intellij.openapi.util.text.StringUtil {

    public static boolean isNumber(String s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String appendMe(String question) {
        return "> ![](https://intellij-icons.jetbrains.design/icons/AllIcons/general/user.svg) 我\n\n" + question + "\n\n";
    }

    public static String appendQuestion(String response) {
        return "\n\n> ![](https://intellij-icons.jetbrains.design/icons/AllIcons/general/balloonInformation.svg) ChatGPT\n\n" + response + "\n\n";
    }
}
