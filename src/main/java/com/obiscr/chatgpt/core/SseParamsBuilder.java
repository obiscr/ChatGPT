package com.obiscr.chatgpt.core;

/**
 * @author Wuzi
 */
public class SseParamsBuilder {
    private final SseParams params = new SseParams();

    public SseParamsBuilder buildUrl(String url) {
        this.params.setUrl(url);
        return this;
    }

    public SseParamsBuilder buildQuestion(String question) {
        this.params.setQuestion(question);
        return this;
    }

    public SseParamsBuilder buildToken(String accessToken) {
        this.params.setAccessToken(accessToken);
        return this;
    }

    public SseParamsBuilder buildData(String data) {
        this.params.setData(data);
        return this;
    }

    public SseParams build() {
        return this.params;
    }


}
