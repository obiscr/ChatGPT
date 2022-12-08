package com.obiscr.chatgpt.ui.listener;

import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuzi
 */
public class SendListener implements ActionListener,KeyListener {
    private static final Logger LOG = LoggerFactory.getLogger(SendListener.class);

    private final MainPanel mainPanel;
    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJ3dXppQG9iaXNjci5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZ2VvaXBfY291bnRyeSI6IlVTIn0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vYXV0aCI6eyJ1c2VyX2lkIjoidXNlci1Bd0hYM1FRVDlxaE43ZDR5NnlGODE0SzIifSwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5vcGVuYWkuY29tLyIsInN1YiI6ImF1dGgwfDYzOGVhZDExZDg4YTQ1NDAyZTNlNmJiMyIsImF1ZCI6WyJodHRwczovL2FwaS5vcGVuYWkuY29tL3YxIiwiaHR0cHM6Ly9vcGVuYWkuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTY3MDQ4NDc0NSwiZXhwIjoxNjcwNTcxMTQ1LCJhenAiOiJUZEpJY2JlMTZXb1RIdE45NW55eXdoNUU0eU9vNkl0RyIsInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUgbW9kZWwucmVhZCBtb2RlbC5yZXF1ZXN0IG9yZ2FuaXphdGlvbi5yZWFkIG9mZmxpbmVfYWNjZXNzIn0.neuTjG0PyReK_iW2-40zw53AGisHTYlsPkaU4uh2_4kU50oJnKH4ylaKJOz92WqJjWBO0VEx4IjELdvhfSHZiFFVsCtr2BdltNZ88FLlghsOa5bfPik1pNu7ZThhehVgjffVKc7OmuF25e8663FWCUnwcF6Ibi2Bo9wgwSN2vzy5NiBRxjkTQLePbkqZKMoB-dQurwfjC_O-0u3C7H4fUjJI9kvYk80JZWgDrmnf1DLYZbqS4380V4KgYZT8YnJuXO7GI8_eCswJOFuqcFStmGwhqJcofhouvqpA4naTjgB1p9qm2qxLDdjDS1UlerQjdJpug3ghQLJB_X_YhVLhUg";

    public SendListener(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doActionPerformed();
    }

    public void doActionPerformed() {
        String accessToken = Objects.requireNonNull(SettingsState.getInstance()
                .getState()).getAccessToken();
        if (accessToken== null|| accessToken.isEmpty()) {
            return;
        }

        JButton button = mainPanel.getButton();
        button.setEnabled(false);
        String text = mainPanel.getSearchTextArea().
                getTextArea().getText();
        LOG.info("ChatGPT Search: {}", text);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                HttpUtil.sse(text, accessToken, mainPanel.getContentPanel());
            } catch (Exception ex) {
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
