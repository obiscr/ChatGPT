package com.obiscr.chatgpt.ui;

import com.intellij.ui.jcef.JBCefBrowser;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;

/**
 * @author Wuzi
 */
public class BrowserContent {

    private final JPanel contentPanel;

    public BrowserContent() {
        JBCefBrowser browser = new JBCefBrowser("https://chat.openai.com/chat");
        AtomicReference<JComponent> component = new AtomicReference<>(browser.getComponent());
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(component.get(),BorderLayout.CENTER);
        contentPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int notches = e.getWheelRotation();
                    if (notches < 0) {
                        browser.setZoomLevel(browser.getZoomLevel() + 0.1D);
                    } else {
                        browser.setZoomLevel(browser.getZoomLevel() - 0.1D);
                    }
                }
            }
        });
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
