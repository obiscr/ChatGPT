package com.obiscr.chatgpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.core.Constant;
import com.obiscr.chatgpt.ui.listener.SendListener;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 */
public class MainPanel {

    private final JPanel myToolWindowContent;

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final MarkdownJCEFHtmlPanel contentPanel;

    public MainPanel() {
        SendListener listener = new SendListener(this);

        searchTextArea = new SearchTextArea(new JBTextArea(),true);
        searchTextArea.getTextArea().addKeyListener(listener);

        button = new JButton("Send");
        button.addActionListener(listener);

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchTextArea, BorderLayout.CENTER);
        top.add(button, BorderLayout.EAST);
        top.setBorder(UIUtil.getTextFieldBorder());
        contentPanel = new MarkdownJCEFHtmlPanel();

        String s = HtmlUtil.md2html(Constant.HOME_CONTENT);
        contentPanel.setHtml(s,0);

        myToolWindowContent = new JPanel(new BorderLayout());
        myToolWindowContent.add(top,BorderLayout.NORTH);
        myToolWindowContent.add(contentPanel.getComponent(),BorderLayout.CENTER);
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MarkdownJCEFHtmlPanel getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return myToolWindowContent;
    }

    public JButton getButton() {
        return button;
    }
}
