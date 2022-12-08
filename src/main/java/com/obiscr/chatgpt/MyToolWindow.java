package com.obiscr.chatgpt;

import com.obiscr.chatgpt.ui.MainPanel;

import javax.swing.*;


/**
 * @author Wuzi
 */
public class MyToolWindow {

  public MyToolWindow() {

  }

  public JPanel getContent() {
    return new MainPanel().init();
  }
}
