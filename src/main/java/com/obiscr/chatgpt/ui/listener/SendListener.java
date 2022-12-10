package com.obiscr.chatgpt.ui.listener;

import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.core.SseParams;
import com.obiscr.chatgpt.core.SseParamsBuilder;
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
import java.util.Objects;
import java.util.UUID;
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
        doActionPerformed();
    }

    public void doActionPerformed() {
        SettingsState state = SettingsState.getInstance().getState();
        assert state != null;

        JButton button = mainPanel.getButton();
        button.setEnabled(false);
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
                MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.config.title"),
                        ChatGPTBundle.message("notify.config.text"));
                return;
            }
            String data = "{\n" + "\"action\": \"next\",\n" + "\"messages\": [\n" + "{\n" + "\"id\": \"" + UUID.randomUUID() + "\",\n" + "\"role\": \"user\",\n" + "\"content\": {\n" + "\"content_type\": \"text\",\n" + "\"parts\": [\n\"" + text + "\"]\n" + "}\n" + "}\n" + "],\n" + "\"parent_message_id\": \""+ UUID.randomUUID() +"\",\n" + "\"model\": \"text-davinci-002-render\"\n" + "}";
            builder.buildUrl(HttpUtil.OFFICIAL_CONVERSATION_URL).buildToken(accessToken).buildData(data);
        } else if (state.urlType == SettingConfiguration.SettingURLType.DEFAULT) {
            builder.buildUrl(HttpUtil.DEFAULT_CONVERSATION_URL);
        } else if (state.urlType == SettingConfiguration.SettingURLType.CLOUDFLARE) {
            String data = "{ \"id\" : \"" + UUID.randomUUID()+ "\", \"message\" : \"" + text +"\", \"message_id\" :\"" + UUID.randomUUID()+ "\" }";
            builder.buildUrl(state.cloudFlareUrl).buildData(data);
        }

        dispatch(builder.build(), button);
    }

    public void dispatch(SseParams params, JButton button) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                HttpUtil.sse(params, mainPanel.getContentPanel());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            } finally {
                button.setEnabled(true);
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
