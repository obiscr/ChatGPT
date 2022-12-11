package com.obiscr.chatgpt.core.parser;

/**
 * @author Wuzi
 */
public interface SseParser {

    /**
     * Parse the response content
     * @param response Response content
     * @return Parse result
     */
    String parse(String response);
}
