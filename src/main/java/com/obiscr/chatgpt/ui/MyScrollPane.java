package com.obiscr.chatgpt.ui;

import com.intellij.ui.components.JBScrollPane;

import java.awt.*;

/**
 * @author Wuzi
 */
public class MyScrollPane extends JBScrollPane {

    public MyScrollPane (Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    @Override
    public void updateUI() {
        setBorder(null);
        super.updateUI();
    }

    @Override
    public void setCorner(String key, Component corner) {
        setBorder(null);
        super.setCorner(key, corner);
    }
}
