package com.obiscr.chatgpt;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.obiscr.chatgpt.core.parser.OfficialParser;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.MessageComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wuzi
 */
public class GPT35TurboHandler  extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GPT35TurboHandler.class);

    public MessageComponent handle(MainPanel mainPanel, MessageComponent component, String question) {
        RequestProvider provider = new RequestProvider().create(mainPanel, question);
        try {
            LOG.info("ChatGPT Request: question={}",question);
            HttpResponse response = HttpUtil.createPost(provider.getUrl())
                    .headerMap(provider.getHeader(),true)
                    .setProxy(getProxy())
                    .body(provider.getData()).executeAsync();
            LOG.info("ChatGPT Response: answer={}",response.body());
            if (response.getStatus() != 200) {
                LOG.info("ChatGPT: Request failure. Url={}, response={}",provider.getUrl(), response.body());
                component.setContent("Response failure, please try again. Error message: " + response.body());
                mainPanel.aroundRequest(false);
                return component;
            }
            OfficialParser.ParseResult parseResult = OfficialParser.
                    parseGPT35Turbo(response.body());

            component.setSourceContent(parseResult.getSource());
            component.setContent(parseResult.getHtml());
            mainPanel.aroundRequest(false);
            component.scrollToBottom();
        } catch (Exception e) {
            component.setSourceContent(e.getMessage());
            component.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
        }
        return component;
    }
}
