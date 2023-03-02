package com.obiscr.chatgpt.ui.listener;

import com.obiscr.chatgpt.core.SendAction;
import com.obiscr.chatgpt.ui.MainPanel;

import java.awt.event.*;
import java.io.IOException;

/**
 * @author Wuzi
 */
public class SendListener implements ActionListener,KeyListener {

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
        String text = mainPanel.getSearchTextArea().
                getTextArea().getText();
        SendAction sendAction = mainPanel.getProject().getService(SendAction.class);
        sendAction.doActionPerformed(mainPanel,text);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isControlDown() && !e.isShiftDown()){
            e.consume();
            mainPanel.getButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
