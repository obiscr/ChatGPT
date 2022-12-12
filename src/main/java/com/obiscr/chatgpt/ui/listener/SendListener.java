package com.obiscr.chatgpt.ui.listener;

import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.core.SseParams;
import com.obiscr.chatgpt.core.SseParamsBuilder;
import com.obiscr.chatgpt.core.builder.CloudflareBuilder;
import com.obiscr.chatgpt.core.builder.OfficialBuilder;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.notifier.MyNotifier;
import com.obiscr.chatgpt.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuzi
 */
public class SendListener implements ActionListener,KeyListener {
    private static final Logger LOG = LoggerFactory.getLogger(SendListener.class);

    private final MainPanel mainPanel;

    public SendListener(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            doActionPerformed();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void doActionPerformed() throws IOException {
        SettingsState state = SettingsState.getInstance().getState();
        assert state != null;

        String text = mainPanel.getSearchTextArea().
                getTextArea().getText();
        LOG.info("ChatGPT Search: {}", text);
        if (text.isEmpty()) {
            return;
        }

        SseParamsBuilder builder = new SseParamsBuilder();
        // If the url type is official, required access token
        if (state.urlType == SettingConfiguration.SettingURLType.OFFICIAL) {
            String accessToken = Objects.requireNonNull(SettingsState.getInstance()
                    .getState()).getAccessToken();
            if (accessToken== null|| accessToken.isEmpty()) {
//                CookieManager cookieManager = new CookieManager();
//                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//                CookieHandler.setDefault(cookieManager);
//                String s = HttpUtil.get("https://chat.openai.com/chat");
//                System.out.println(s);
//                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
//                for (HttpCookie cookie : cookies) {
//                    String name = cookie.getName();
//                    String value = cookie.getValue();
//                    // Do something with the cookie name and value
//                }
                MyNotifier.notifyErrorWithAction(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.config.title"),
                        ChatGPTBundle.message("notify.config.text"));
                return;
            }
            builder.buildUrl(HttpUtil.OFFICIAL_CONVERSATION_URL).buildToken(accessToken).buildData(OfficialBuilder.build(text));
        } else if (state.urlType == SettingConfiguration.SettingURLType.DEFAULT) {
            builder.buildUrl(HttpUtil.DEFAULT_CONVERSATION_URL).buildData(OfficialBuilder.build(text));
        } else if (state.urlType == SettingConfiguration.SettingURLType.CUSTOMIZE) {
            if (state.customizeUrl== null|| state.customizeUrl.isEmpty()) {
                MyNotifier.notifyErrorWithAction(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.config.title"),
                        ChatGPTBundle.message("notify.config.text"));
                return;
            }
            builder.buildUrl(state.customizeUrl).buildData(OfficialBuilder.build(text));
        } else if (state.urlType == SettingConfiguration.SettingURLType.CLOUDFLARE) {
            if (state.cloudFlareUrl== null|| state.cloudFlareUrl.isEmpty()) {
                MyNotifier.notifyErrorWithAction(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.config.title"),
                        ChatGPTBundle.message("notify.config.text"));
                return;
            }
            builder.buildUrl(state.cloudFlareUrl).buildData(CloudflareBuilder.build(text));
        }

        dispatch(builder.build());
    }

    public void dispatch(SseParams params) {
        mainPanel.aroundRequest(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                HttpUtil.sse(params, mainPanel);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.timeout.error.title"),
                        ChatGPTBundle.message("notify.timeout.error.text"));
                mainPanel.aroundRequest(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                mainPanel.aroundRequest(false);
                throw new RuntimeException(ex);
            }
        });
        executorService.shutdown();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            e.consume();
            mainPanel.getButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
